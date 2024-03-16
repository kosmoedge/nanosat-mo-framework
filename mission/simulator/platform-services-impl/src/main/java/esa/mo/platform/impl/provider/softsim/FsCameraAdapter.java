package esa.mo.platform.impl.provider.softsim;

import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.platform.impl.provider.gen.CameraAdapterInterface;
import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import esa.opssat.camera.processing.OPSSATCameraDebayering;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.ccsds.moims.mo.mal.MALException;
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
 *
 * @author Kosmoedge
 */
public class FsCameraAdapter implements CameraAdapterInterface {

    private static final Logger LOGGER = Logger.getLogger(FsCameraAdapter.class.getName());
    private static final Duration PREVIEW_EXPOSURE_TIME = new Duration(0.002);
    private static final Duration MINIMUM_PERIOD = new Duration(1);
    private static final float PREVIEW_GAIN = 8.f;
    private static final int IMAGE_WIDTH = 640;
    private static final int IMAGE_HEIGHT = 480;
    private static final int PREVIEW_WIDTH = 600;
    private static final int PREVIEW_HEIGHT = 600;
    private final PictureFormatList supportedFormats = new PictureFormatList();
    private final PowerControlAdapterInterface pcAdapter;

    public FsCameraAdapter(PowerControlAdapterInterface pcAdapter) {
        this.pcAdapter = pcAdapter;
        this.supportedFormats.add(PictureFormat.RAW);
        this.supportedFormats.add(PictureFormat.RGB24);
        this.supportedFormats.add(PictureFormat.BMP);
        this.supportedFormats.add(PictureFormat.PNG);
        this.supportedFormats.add(PictureFormat.JPG);
        LOGGER.log(Level.INFO, "FsCameraAdapter Initialisation");
        Webcam.setDriver(new FsWebcamDriver());
    }

    @Override
    public boolean isUnitAvailable() {
        return this.pcAdapter.isDeviceEnabled(DeviceType.CAMERA);
    }

    @Override
    public String getExtraInfo() {
        return "NMF Satellite Windows - Kubernetes Camera Adapter";
    }

    @Override
    public PixelResolutionList getAvailableResolutions() {
        final PixelResolutionList availableResolutions = new PixelResolutionList();
        availableResolutions.add(new PixelResolution(new UInteger(IMAGE_WIDTH), new UInteger(IMAGE_HEIGHT)));

        return availableResolutions;
    }

    @Override
    public synchronized Picture getPicturePreview() throws IOException {
        final PixelResolution resolution = new PixelResolution(new UInteger(PREVIEW_WIDTH), new UInteger(
                PREVIEW_HEIGHT));
        return takePicture(new CameraSettings(resolution, PictureFormat.RAW, PREVIEW_EXPOSURE_TIME, PREVIEW_GAIN,
                PREVIEW_GAIN, PREVIEW_GAIN));
    }

    @Override
    public Picture takeAutoExposedPicture(final CameraSettings settings) throws IOException, MALException {
        return takePicture(settings);
    }

    @Override
    public Picture takePicture(final CameraSettings settings) throws IOException {
        synchronized (this) {
            LOGGER.log(Level.INFO, "FsCameraAdapter ready to take picture");
            final Time timestamp = HelperTime.getTimestampMillis();

            Webcam webcam = Webcam.getDefault();

            if (webcam == null) {
                LOGGER.severe("No camera");
            }

            webcam.setViewSize(new Dimension((int) settings.getResolution().getWidth().getValue(),
                    (int) settings.getResolution().getHeight().getValue()));
            LOGGER.log(Level.INFO, "Resolution set.");
            webcam.open();
            LOGGER.log(Level.INFO, "Opened camera");
           
            BufferedImage image = webcam.getImage();
            LOGGER.log(Level.INFO, "Image taken");
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