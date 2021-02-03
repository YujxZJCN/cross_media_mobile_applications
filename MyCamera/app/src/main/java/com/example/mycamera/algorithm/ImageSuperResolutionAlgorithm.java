package com.example.mycamera.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.mycamera.auxiliary.PictureProcess;
import com.example.mycamera.auxiliary.VisionFunHandler;
import com.huawei.hiai.vision.image.sr.ImageSuperResolution;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.ImageResult;
import com.huawei.hiai.vision.visionkit.image.sr.SuperResolutionConfiguration;

import org.json.JSONException;

public class ImageSuperResolutionAlgorithm extends VisionAlgorithm {
    ImageSuperResolution superResolution;
    ImageResult imageResult;
    Bitmap inputBitmap;
    public ImageSuperResolutionAlgorithm(Context context) {
        superResolution = new ImageSuperResolution(context);
    }
    @Override
    public int run(Bitmap bitmap) throws JSONException {
        inputBitmap = bitmap;
        if (bitmap.getHeight() > 600 || bitmap.getWidth() > 600) {
            inputBitmap = PictureProcess.resizeBitmap(bitmap, 640, 480);
        }
        Frame frame = new Frame();
        frame.setBitmap(inputBitmap);
        SuperResolutionConfiguration paras = new SuperResolutionConfiguration(
                SuperResolutionConfiguration.SISR_SCALE_3X,
                SuperResolutionConfiguration.SISR_QUALITY_HIGH);
        superResolution.setSuperResolutionConfiguration(paras);
        imageResult = superResolution.doSuperResolution(frame, null);
        superResolution.release();
        if (imageResult == null) {
            return -1; }
        return imageResult.getResultCode();
    }
    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        if (imageResult.getBitmap() == null) {
            sbuf.append("图片图像超分执行失败!\n");
            return sbuf.toString();
        }
        VisionFunHandler.sendMsgToHandler(super.getHandler(),
                VisionFunHandler.HANDLE_WHAT_MODEL_SETIMAGE, imageResult.getBitmap());
        sbuf.append("图片图像超分执行成功^_^\n");
        sbuf.append("执行前图片(width: " + inputBitmap.getWidth() + ", Height: " +
                inputBitmap.getHeight() + ")\n");
        sbuf.append("执行后图片(width: " + imageResult.getBitmap().getWidth() + ",Height:" + imageResult.getBitmap().getHeight() + ")\n ");
        return sbuf.toString();
    }
}







