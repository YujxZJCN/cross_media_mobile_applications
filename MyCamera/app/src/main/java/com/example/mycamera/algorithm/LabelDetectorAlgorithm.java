package com.example.mycamera.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.image.detector.LabelDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.detector.Label;
import com.huawei.hiai.vision.visionkit.image.detector.LabelContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LabelDetectorAlgorithm extends VisionAlgorithm {

    Context mainActivity;
    LabelDetector labelDetector;
    Label pictureLabel;
    Map<Integer, String> pictureClasses = new HashMap<>();

    public LabelDetectorAlgorithm(Context context) {
        mainActivity = context;
        labelDetector = new LabelDetector(context);
        loadPictureClasses("labels.txt"); //内容存在labels.txt文件中
    }

    @Override
    public int run(Bitmap bitmap) throws JSONException {
        Frame frame = new Frame();
        frame.setBitmap(bitmap);
        JSONObject jsonLabel = labelDetector.detect(frame, null);
        pictureLabel = labelDetector.convertResult(jsonLabel);
        labelDetector.release();
        return Integer.valueOf(jsonLabel.getString("resultCode"));
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("图片标签分类成功^_^\n");
        sbuf.append("图片分类: " + pictureClasses.get(pictureLabel.getCategory()) +
                "; 可信度: " + pictureLabel.getCategoryProbability() + "\n");
        sbuf.append("总执行结果:\n");
        for (LabelContent item: pictureLabel.getLabelContent()) {
            System.out.print("结果："+pictureClasses.get(item.getLabelId()));
            sbuf.append(" 图片分类: " + pictureClasses.get(item.getLabelId()) +
                    "; 可信度: " + item.getProbability() + "\n");
        }
        return sbuf.toString();
    }

    //==============================================================
    //Get all picture class from labels.txt
    private void loadPictureClasses(String fileName) {
        try {
            InputStream inputStream = mainActivity.getAssets().open(fileName);
            int length = inputStream.available();
            byte[] content = new byte[length];
            inputStream.read(content);
            inputStream.close();
            String label = new String(content);
            String lines[] = label.split("\n");
            for (String item: lines) {
                String vals[] = item.split("\\s+");
                pictureClasses.put(Integer.valueOf(vals[0]), vals[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
