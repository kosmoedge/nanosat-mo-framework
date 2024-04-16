package esa.mo.nmf.apps.util;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class OpenCvFileTransformer {

    private OpenCvFileTransformer() {}
    
    public static Mat byteArrayImage2Mat(byte[] imageByteArray) {
        return Imgcodecs.imdecode(new MatOfByte(imageByteArray), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

    public static byte[] mat2ByteArrayImage(Mat matrix) {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        return mob.toArray();
    }

}
