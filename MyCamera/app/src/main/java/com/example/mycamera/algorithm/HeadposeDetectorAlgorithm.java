package com.example.mycamera.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.mycamera.auxiliary.PictureProcess;
import com.huawei.hiai.vision.face.HeadposeDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.face.HeadPoseResult;

import org.json.JSONException;
import org.json.JSONObject;

public class HeadposeDetectorAlgorithm extends VisionAlgorithm {

    HeadposeDetector headposeDetector;
    HeadPoseResult result;
    String mHeadpose[] = {"无人脸", "人脸朝上", "人脸朝右", "人脸朝下", "人脸朝左"};

    public HeadposeDetectorAlgorithm(Context context){
        /** 构造人脸朝向检测处理类*/
        headposeDetector = new HeadposeDetector(context);

    }

    @Override
    public int run(Bitmap bitmap) throws JSONException {
        /** 准备人脸朝向检测的输入图片*/
        Frame frame = new Frame();
        /** BitmapFactory.decodeFile中输入资源文件路径,输入的图片要求尺寸宽为 640 像素，高为 480 像素。如果图片尺寸不满足此条件，请先调整图
         片尺寸，等比例调整会得到更准确的结果。*/
        frame.setBitmap(PictureProcess.resizeBitmap(bitmap, 640, 480));
        /** 调用detect方法得到人脸朝向检测的结果*/
        /** 注意：此行代码必须放到工作线程中而不是主线程*/
        JSONObject jsonObject = headposeDetector.detect(frame, null);
        /** 通过convertResult将json字符串转为java类的形式（您也可以自己解析json字符串）。其中HeadPoseResult的headpose字段为识别的朝向标签，0表示无人、1表示人脸朝上、2表示人脸朝右、3表示人脸向下、4表示人脸朝左*/
        HeadPoseResult result = headposeDetector.convertResult(jsonObject);
        headposeDetector.release();
        return Integer.valueOf(jsonObject.getString("resultCode"));
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("人脸朝向识别成功^_^\n");
        sbuf.append(mHeadpose[result.getHeadpose()]);
        return sbuf.toString();
    }
}

