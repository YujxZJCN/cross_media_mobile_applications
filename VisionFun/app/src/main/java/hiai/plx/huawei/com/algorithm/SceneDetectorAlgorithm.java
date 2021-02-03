package hiai.plx.huawei.com.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.image.detector.SceneDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.detector.Scene;

import org.json.JSONException;
import org.json.JSONObject;

public class SceneDetectorAlgorithm extends VisionAlgorithm {

    SceneDetector sceneDetector;
    Scene scene;
    final String sceneClasses[] = {"UNKNOWN", "UNSUPPORT", "BEACH", "BLUESKY",
            "SUNSET",
            "FOOD", "FLOWER", "GREENPLANT", "SNOW", "NIGHT",
            "TEXT", "STAGE", "CAT", "DOG", "FIREWORK",
            "OVERCAST", "FALLEN", "PANDA", "CAR", "OLDBUILDINGS",
            "BICYCLE", "WATERFALL"};

    public SceneDetectorAlgorithm(Context context){
        /** 定义detector实例，将此工程的Context当做入参*/
        sceneDetector = new SceneDetector(context);
    }

    @Override
    public int run(Bitmap bitmap) throws JSONException {

        /** 定义frame，将需进行场景检测图像的bitmap放入frame中*/
        Frame frame = new Frame();
        frame.setBitmap(bitmap);
        /** 调用detect方法得到场景检测结果*/
        /** 注意：此行代码必须放到工作线程中而不是主线程*/
        JSONObject jsonScene = sceneDetector.detect(frame, null);
        scene = sceneDetector.convertResult(jsonScene);
        sceneDetector.release();
        return Integer.valueOf(jsonScene.getString("resultCode"));
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("场景检测成功^_^\n");
        sbuf.append("图片分类: " + scene.getType());
        return sbuf.toString();
    }
}


