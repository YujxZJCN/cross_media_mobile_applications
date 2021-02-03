package hiai.plx.huawei.com.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.face.FaceAttributesDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.face.FaceAttributesInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FaceAttributesAlgorithm extends VisionAlgorithm {

    //0-高兴、1-伤心、2-惊讶、3-愤怒、4-嘟嘴、5-鬼脸、6-中性

    String emotion[] = {"高兴", "伤心", "惊讶", "愤怒", "嘟嘴", "鬼脸", "中性"};

    FaceAttributesDetector faceabelDetector;

    List<FaceAttributesInfo> faceAttributes;

    public FaceAttributesAlgorithm(Context context) {
        faceabelDetector = new FaceAttributesDetector(context);
    }
    @Override
    public int run(Bitmap bitmap) throws JSONException {
        Frame frame = new Frame();
        frame.setBitmap(bitmap);
        JSONObject result = faceabelDetector.detectFaceAttributes(frame, null);
        faceAttributes = faceabelDetector.convertResultEx(result);
        faceabelDetector.release();
        return Integer.valueOf(result.getString("resultCode"));
    }
    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("人脸属性检测成功^_^\n");
        sbuf.append("图片中共" + faceAttributes.size() + "个人\n");
        for (FaceAttributesInfo face: faceAttributes) {
            sbuf.append("性别:" + face.getSex() + "\n");
            sbuf.append("年龄:" + face.getAge() + "\n");
            if (face.getEmotion() < 0 || face.getEmotion() > 6) {
                sbuf.append("表情:" + emotion[face.getEmotion()] + "\n");
            } else {
                sbuf.append("表情: 未识别\n");
            }
            if (face.getDressInfo().getBeard() == 0) {
                sbuf.append("不留胡须\n");
            } else if (face.getDressInfo().getBeard() == 1) {
                sbuf.append("留胡须\n");
            } else {
                sbuf.append("未识别是否有胡须\n");
            }
            if (face.getDressInfo().getGlass() == 0) {
                sbuf.append("戴太阳镜\n");
            } else if (face.getDressInfo().getGlass() == 1) {
                sbuf.append("不戴眼镜\n");
            } else if (face.getDressInfo().getGlass() == 2) {
                sbuf.append("戴眼镜\n");
            } else {
                sbuf.append("未识别是否戴眼镜\n");
            }
            sbuf.append("-----------------------\n");
        }
        return sbuf.toString();
    }
}
