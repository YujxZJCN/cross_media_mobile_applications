package hiai.plx.huawei.com.algorithm;

import android.content.Context;
import android.graphics.Bitmap;
import com.huawei.hiai.vision.face.FaceParsing;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.ImageResult;

import org.json.JSONException;

import hiai.plx.huawei.com.auxiliary.VisionFunHandler;

public class FaceParsingAlgorithm extends VisionAlgorithm {

    private final static int HANDLE_WHAT_MODEL_SETIMAGE = 0x10;
    FaceParsing faceParsing;

    public FaceParsingAlgorithm(Context context){
        faceParsing = new FaceParsing(context);
    }

    @Override
    public int run(Bitmap bitmap) throws JSONException {
        /** 准备人脸解析的输入图片*/
        Frame frame = new Frame();
        frame.setBitmap(bitmap);
        /** 进行人脸解析*/
        ImageResult srt = faceParsing.doFaceParsing(frame, null);
        /** 将结果转化成bitmap格式*/
        Bitmap newbmp = srt.getBitmap();
        VisionFunHandler.sendMsgToHandler(super.getHandler(),
                VisionFunHandler.HANDLE_WHAT_MODEL_SETIMAGE, newbmp);
        faceParsing.release();
        return srt.getResultCode();
    }

    @Override
    public String getResult() {
        return "执行成功\n";
    }
}


