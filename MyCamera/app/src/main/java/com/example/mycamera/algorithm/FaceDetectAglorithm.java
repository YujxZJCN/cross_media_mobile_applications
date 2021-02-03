package com.example.mycamera.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.face.FaceDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.face.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FaceDetectAglorithm extends VisionAlgorithm {

    FaceDetector faceDetector;
    List<Face> faces;

    public FaceDetectAglorithm(Context context) {
        /** 构造人脸检测处理类 */
        this.faceDetector = new FaceDetector(context);
    }

    @Override
    public int run(Bitmap bitmap) throws JSONException {

        /** bitmap 图像资源；定义frame并初始化，将需进行人脸检测图像的bitmap放入frame中*/
        Frame frame = new Frame();
        frame.setBitmap(bitmap);

        /** 调用detect方法得到人脸检测结果*/
        /** 注意：此行代码必须放到工作线程中而不是主线程*/
        JSONObject jsonFace = faceDetector.detect(frame,null); //执行人脸检测
        /** 通过convertResult将json字符串转为java类的形式（您也可以自己解析json字符串）*/
        faces = faceDetector.convertResult(jsonFace);
        faceDetector.release();
        return Integer.valueOf(jsonFace.getString("resultCode"));
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("人脸检测执行成功^_^\n");
        sbuf.append(String.format("图片中总共识别: " + faces.size() + "张人脸\n"));
        return sbuf.toString();
    }
}
