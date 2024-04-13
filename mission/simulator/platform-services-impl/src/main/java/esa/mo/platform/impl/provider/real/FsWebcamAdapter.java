package esa.mo.platform.impl.provider.real;

import com.github.sarxos.webcam.WebcamResolution;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.platform.impl.provider.gen.CameraAdapterInterface;
import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;

/**
 * This is an adapter used by the platform service of the supervisor to achieve certain functionalities
 * with the actual underlying camera hardware through the use of a
 * <a href="https://github.com/sarxos/webcam-capture">Java Webcam Capture library</a> and
 * <a href="https://manpages.ubuntu.com/manpages/bionic/man1/fswebcam.1.html">fswebcam library</a>,
 * which is a small and simple webcam handling library for *nix.
 *
 * @author Kosmoedge
 */
public class FsWebcamAdapter implements CameraAdapterInterface {

    private static final Logger LOGGER = Logger.getLogger(FsWebcamAdapter.class.getName());
    private static final Duration PREVIEW_EXPOSURE_TIME = new Duration(0.002);
    private static final Duration MINIMUM_PERIOD = new Duration(1);
    private static final float PREVIEW_GAIN = 8.f;
    private static final Dimension PREVIEW_DIMENSIONS = WebcamResolution.QQVGA.getSize();
    private final PictureFormatList supportedFormats = new PictureFormatList();
    private final PowerControlAdapterInterface pcAdapter;

    public FsWebcamAdapter(PowerControlAdapterInterface pcAdapter) {
        this.pcAdapter = pcAdapter;
        this.supportedFormats.add(PictureFormat.RAW);
        this.supportedFormats.add(PictureFormat.RGB24);
        this.supportedFormats.add(PictureFormat.BMP);
        this.supportedFormats.add(PictureFormat.PNG);
        this.supportedFormats.add(PictureFormat.JPG);
        Webcam.setDriver(new FsWebcamDriver());
        LOGGER.log(Level.INFO, "FsWebcamAdapter Initialised");
    }

    @Override
    public boolean isUnitAvailable() {
        return this.pcAdapter.isDeviceEnabled(DeviceType.CAMERA);
    }

    @Override
    public String getExtraInfo() {
        return "NMF Satellite FsWebcam Adapter - Small and simple webcam for *nix";
    }

    @Override
    public PixelResolutionList getAvailableResolutions() {
        final PixelResolutionList availableResolutions = new PixelResolutionList();
        Webcam webcam = Webcam.getDefault();
        for (Dimension dimension : webcam.getViewSizes()) {
            availableResolutions.add(new PixelResolution(
                    new UInteger((long) dimension.getWidth()), new UInteger((long) dimension.getHeight())));
        }
        return availableResolutions;
    }

    @Override
    public synchronized Picture getPicturePreview() throws IOException {
        final PixelResolution previewResolution = new PixelResolution(
                new UInteger((long) PREVIEW_DIMENSIONS.getWidth()), new UInteger((long) PREVIEW_DIMENSIONS.getHeight()));
        return takePicture(new CameraSettings(previewResolution, PictureFormat.RAW, PREVIEW_EXPOSURE_TIME, PREVIEW_GAIN,
                PREVIEW_GAIN, PREVIEW_GAIN));
    }

    @Override
    public Picture takeAutoExposedPicture(final CameraSettings settings) throws IOException {
        return takePicture(settings);
    }

    @Override
    public Picture takePicture(final CameraSettings settings) throws IOException {
        synchronized (this) {
            LOGGER.log(Level.INFO, "FsWebcamAdapter ready to take picture");
            Webcam webcam = Webcam.getDefault();
            if (webcam == null) {
                throw new IOException("Cannot find camera device.");
            }

            webcam.setViewSize(new Dimension((int) settings.getResolution().getWidth().getValue(),
                    (int) settings.getResolution().getHeight().getValue()));
            LOGGER.log(Level.INFO, "Resolution set.");

            webcam.open();
            LOGGER.log(Level.INFO, "Opened camera");

            final Time timestamp = HelperTime.getTimestampMillis();
            BufferedImage image = webcam.getImage();
            LOGGER.log(Level.INFO, "Snapshot taken");

            webcam.close();
            LOGGER.log(Level.INFO, "Closed camera");
            
            byte[] data = convertImage(image, settings.getFormat());
            LOGGER.log(Level.INFO, "Image converted to byte array");

            final CameraSettings replySettings = new CameraSettings();
            replySettings.setResolution(settings.getResolution());
            replySettings.setExposureTime(settings.getExposureTime());
            replySettings.setGainRed(settings.getGainRed());
            replySettings.setGainGreen(settings.getGainGreen());
            replySettings.setGainBlue(settings.getGainBlue());

            replySettings.setFormat(settings.getFormat());
            return new Picture(timestamp, replySettings, new Blob(data));
        }
    }

    @Override
    public Duration getMinimumPeriod() {
        return MINIMUM_PERIOD;
    }

    @Override
    public PictureFormatList getAvailableFormats() {
        return supportedFormats;
    }

    private byte[] convertImage(BufferedImage image, final PictureFormat targetFormat) throws IOException {
        byte[] ret = null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (targetFormat.equals(PictureFormat.RGB24)) {
            int w = image.getWidth();
            int h = image.getHeight();
            int[] rgba = image.getRGB(0, 0, w, h, null, 0, w);
            ret = new byte[rgba.length * 3];
            for (int i = 0; i < rgba.length; ++i) {
                final int pixelval = rgba[i];
                ret[i * 3 + 0] = (byte) ((pixelval >> 16) & 0xFF); // R
                ret[i * 3 + 1] = (byte) ((pixelval >> 8) & 0xFF); // G
                ret[i * 3 + 2] = (byte) ((pixelval) & 0xFF); // B
                // Ignore Alpha channel
            }
        } else if (targetFormat.equals(PictureFormat.BMP)) {
            ImageIO.write(image, "BMP", stream);
            ret = stream.toByteArray();
            stream.close();
        } else if (targetFormat.equals(PictureFormat.PNG)) {
            ImageIO.write(image, "PNG", stream);
            ret = stream.toByteArray();
            stream.close();
        } else if (targetFormat.equals(PictureFormat.JPG)) {
            ImageIO.write(image, "JPEG", stream);
            ret = stream.toByteArray();
            stream.close();
        } else if (targetFormat.equals(PictureFormat.RAW)) {
            ImageIO.write(image, "RAW", stream);
            ret = stream.toByteArray();
            stream.close();
        } else {
            throw new IOException("Something went wrong! The Image could not be converted into the selected format.");
        }
        return ret;
    }

}