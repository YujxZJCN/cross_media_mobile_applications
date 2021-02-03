package com.example.mycamera.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.text.TextDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.text.Text;
import com.huawei.hiai.vision.visionkit.text.TextBlock;
import com.huawei.hiai.vision.visionkit.text.TextDetectType;
import com.huawei.hiai.vision.visionkit.text.config.TextConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

public class OCRAlgorithm extends VisionAlgorithm {

    TextDetector textDetector;
    Text textResult;

    public OCRAlgorithm(Context context) {
        textDetector = new TextDetector(context);
        TextConfiguration config = new TextConfiguration();
        config.setEngineType(TextDetectType.TYPE_TEXT_DETECT_SCREEN_SHOT_GENERAL);
        textDetector.setTextConfiguration(config);
    }

    @Override
    public int run(Bitmap bitmap) throws JSONException {
        Frame frame = new Frame();
        frame.setBitmap(bitmap);
        JSONObject jsonObject = textDetector.detect(frame,null);
        textResult = textDetector.convertResult(jsonObject);
        textDetector.release();
        return Integer.valueOf(jsonObject.getString("resultCode"));
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("OCR[通用文字识别]执行成功^_^\n");
        sbuf.append("识别的文字如下：\n");
        for (TextBlock item: textResult.getBlocks()) {
            sbuf.append(" " + item.getValue() + "\n");
        }
        return sbuf.toString();
    }
}
