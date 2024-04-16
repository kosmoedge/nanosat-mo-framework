package esa.mo.nmf.apps.operator;

import esa.mo.nmf.apps.util.OpenCvFileTransformer;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.logging.Logger;

public class OpenCVSobelOperator {

    private OpenCVSobelOperator() {
    }

    private static final Logger logger = Logger.getLogger(OpenCVSobelOperator.class.getName());

    public static byte[] apply(byte[] image) throws IOException {
        logger.info("Sobel operation (via OpenCV) is currently being applied...");

        Mat src = OpenCvFileTransformer.byteArrayImage2Mat(image);
        Mat dst = new Mat();

        Imgproc.Sobel(src, dst, -1, 0, 1);
        Imgproc.Sobel(src, dst, -1, 1, 0);
        Imgproc.Sobel(src, dst, -1, 1, 1);

        return OpenCvFileTransformer.mat2ByteArrayImage(dst);
    }

}
