package hiai.plx.huawei.com.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.image.detector.AestheticsScoreDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.detector.AEModelConfiguration;
import com.huawei.hiai.vision.visionkit.image.detector.AestheticsScore;

import org.json.JSONException;
import org.json.JSONObject;

public class AestheticsScoreAlgorithm extends VisionAlgorithm {

    String ospModel[] = {"OSPOverall", "VisualBalance", "ColorHarmony",
            "RuleOfThirds", "Simplicity", "LightingCondition", "Colorfulness",
            "DepthOfField", "Blurriness", "FreeOfNoise", "CenterFocus", "AttentionObjec"};

    AestheticsScoreDetector aestheticsScoreDetector;

    AestheticsScore aestheticsScores;

    public AestheticsScoreAlgorithm(Context context) {
        aestheticsScoreDetector = new AestheticsScoreDetector(context);
    }

    @Override
    public int run(Bitmap bitmap) throws JSONException {

        AEModelConfiguration aeModelConfiguration = new AEModelConfiguration();

        aeModelConfiguration.getDetectImageConf().setDetectImageMode(3);

        aestheticsScoreDetector.setAEModelConfiguration(aeModelConfiguration);

        Frame frame = new Frame();

        frame.setBitmap(bitmap);

        JSONObject jsonObject = aestheticsScoreDetector.detect(frame,null);

        aestheticsScores = aestheticsScoreDetector.convertResult(jsonObject);

        return Integer.valueOf(jsonObject.getString("resultCode"));
    }

    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("美学评分成功^_^\n");
        sbuf.append("总分数: " + aestheticsScores.getScore());
        sbuf.append("OSPScore: 共" + aestheticsScores.getOSPScores().length + "项 \n");
        for (int i = 0; i < aestheticsScores.getOSPScores().length; i++) {
            sbuf.append(" " + ospModel[i] + ": " +
                    aestheticsScores.getOSPScores()[i] + "\n");
        }
        sbuf.append("HFScore: 共 1 项\n");
        sbuf.append(" human: " + aestheticsScores.getHFSCore()[0] + "\n");
        return sbuf.toString();
    }

}
