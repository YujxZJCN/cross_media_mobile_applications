package hiai.plx.huawei.com.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.image.segmentation.ImageSegmentation;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.ImageResult;
import com.huawei.hiai.vision.visionkit.image.segmentation.SegmentationConfiguration;


import hiai.plx.huawei.com.auxiliary.VisionFunHandler;

public class ImageSegmentationAlgorithm extends VisionAlgorithm {

    ImageSegmentation imageSegmentation;
    int imageSegmentationType;
    ImageResult imageResult;

    public ImageSegmentationAlgorithm(Context context, int type) {
        imageSegmentation = new ImageSegmentation(context);
        imageSegmentationType = type;
        SegmentationConfiguration sc = new SegmentationConfiguration();
        sc.setSegmentationType(type);
        imageSegmentation.setSegmentationConfiguration(sc);
    }

    @Override
    public int run(Bitmap bitmap) {
        Frame frame = new Frame();
        frame.setBitmap(bitmap);
        imageResult = imageSegmentation.doSegmentation(frame, null);//进行人像或语义分割
        imageSegmentation.release();
        return imageResult.getResultCode();
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        if (imageResult.getBitmap() == null) {
            sbuf.append("图像分割执行失败!\n");
            return sbuf.toString();
        }
        if (imageSegmentationType == SegmentationConfiguration.TYPE_PORTRAIT) {
            sbuf.append("执行人像分割成功!\n");
        } else {
            sbuf.append("执行图像语义分割成功!\n");
        }
        VisionFunHandler.sendMsgToHandler(super.getHandler(),
                VisionFunHandler.HANDLE_WHAT_MODEL_SETIMAGE, imageResult.getBitmap());
        return sbuf.toString();
    }
}
