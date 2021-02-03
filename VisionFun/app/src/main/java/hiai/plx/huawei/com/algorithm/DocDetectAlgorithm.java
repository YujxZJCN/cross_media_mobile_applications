package hiai.plx.huawei.com.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.image.docrefine.DocRefine;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.detector.DocCoordinates;

import org.json.JSONException;
import org.json.JSONObject;

import hiai.plx.huawei.com.auxiliary.VisionFunHandler;

public class DocDetectAlgorithm extends VisionAlgorithm {
    DocRefine docRefine;
    Bitmap newBmp;
    DocCoordinates ds;
    public DocDetectAlgorithm(Context context) {
        this.docRefine = new DocRefine(context);
    }

    public int run(Bitmap bitmap) throws JSONException {
        Frame frame = new Frame();
        frame.setBitmap(bitmap);
        JSONObject jsonDoc = docRefine.docDetect(frame, null);
        ds = docRefine.convertResult(jsonDoc);
        newBmp = docRefine.docRefine(frame, ds, null).getBitmap();
        docRefine.release();
        VisionFunHandler.sendMsgToHandler(super.getHandler(),
                VisionFunHandler.HANDLE_WHAT_MODEL_SETIMAGE,
                newBmp);
        return Integer.valueOf(jsonDoc.getString("resultCode"));
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("文档检测校正执行成功^_^\n");
        sbuf.append(String.format("发现文本，文本位置："));
        sbuf.append(String.format("\n 左上：" +
                ds.getTopLeftCoordinate().toString()
                + "\n 右上：" + ds.getTopRightCoordinate().toString()
                + "\n 左下：" + ds.getBottomLeftCoordinate().toString()
                + "\n 右下：" +
                ds.getBottomRightCoordinate().toString()));
        return sbuf.toString();
    }
}
