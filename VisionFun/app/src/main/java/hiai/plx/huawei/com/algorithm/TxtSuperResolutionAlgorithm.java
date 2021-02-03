package hiai.plx.huawei.com.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.image.sr.TxtImageSuperResolution;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.ImageResult;

import org.json.JSONException;

import hiai.plx.huawei.com.auxiliary.VisionFunHandler;

public class TxtSuperResolutionAlgorithm extends VisionAlgorithm {

    TxtImageSuperResolution txtImageSuperResolution;

    ImageResult imageResult;

    Bitmap inputBitmap;

    public TxtSuperResolutionAlgorithm(Context context) {
        txtImageSuperResolution = new TxtImageSuperResolution (context);
    }
    @Override
    public int run(Bitmap bitmap) throws JSONException {
        inputBitmap = bitmap;
        /*
        float scale = 0.0f;
        float pixel = bitmap.getWidth() * bitmap.getHeight();
        if (pixel > 1300000) {
        scale = 1300000.0f / pixel;
        inputBitmap = PictureProcess.resizeBitmap(bitmap,
        (int)(bitmap.getWidth() * scale), (int)(bitmap.getHeight() * scale));
        }
        */
        Frame frame = new Frame();
        frame.setBitmap(inputBitmap);
        imageResult = txtImageSuperResolution.doSuperResolution(frame, null);
        txtImageSuperResolution.release();
        if (imageResult == null) {
            return -1;
        }
        return imageResult.getResultCode();
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        if (imageResult.getBitmap() == null) {
            sbuf.append("文字图像超分执行失败!\n");
            return sbuf.toString();
        }
        VisionFunHandler.sendMsgToHandler(super.getHandler(),
                VisionFunHandler.HANDLE_WHAT_MODEL_SETIMAGE, imageResult.getBitmap());
        sbuf.append("文字图像超分执行成功^_^\n");
        sbuf.append("执行前图片(width: " + inputBitmap.getWidth() + ", Height: " +
                inputBitmap.getHeight() + ")\n");
        sbuf.append("执行后图片(width: " + imageResult.getBitmap().getWidth() + ",Height: " + imageResult.getBitmap().getHeight() + ")\n");
        return sbuf.toString();
    }
}
