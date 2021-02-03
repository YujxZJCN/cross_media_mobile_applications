package com.example.mycamera.algorithm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.face.FaceLandMarkDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.face.FaceLandmark;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FaceLandMarkAlgorithm extends VisionAlgorithm {
    /** 构造五官特征检测处理类*/
    FaceLandMarkDetector lm;
    List<FaceLandmark> landMarks;
    public FaceLandMarkAlgorithm(Context context){
        lm = new FaceLandMarkDetector(context);
    }
    @Override
    public int run(Bitmap bitmap) throws JSONException {

        /** bitmap 图像资源；准备五官特征检测的输入图片*/
        Frame frame = new Frame();
        /** 将需进行五官特征检测图像的bitmap放入frame中*/
        frame.setBitmap(bitmap);
        /** 调用detectLandMark方法得到五官特征检测结果*/
        JSONObject lmf = lm.detectLandMark(frame, null);
        /** 通过convertResult将json字符串转为java类的形式（您也可以自己解析json字符串）*/
        landMarks = lm.convertResult(lmf);
        /** 获取第一个点的坐标*/
        PointF p = landMarks.get(0).getPositionF();
        lm.release();
        return  Integer.valueOf(lmf.getString("resultCode"));

    }

    @Override
    public String getResult() {

        StringBuilder sbuf = new StringBuilder();
        sbuf.append("五官特征检测执行成功^_^\n");
        sbuf.append("关键数据如下：\n");
        for (FaceLandmark face: landMarks) {
            sbuf.append("(" + face.getPositionF().x + ", " + face.getPositionF().y
                    + ") ");
        }
        return sbuf.toString();

    }

}
