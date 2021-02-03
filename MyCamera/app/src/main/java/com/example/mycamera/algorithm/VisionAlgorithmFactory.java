package com.example.mycamera.algorithm;

import android.content.Context;
import android.content.Intent;

import com.huawei.hiai.vision.visionkit.image.segmentation.SegmentationConfiguration;


public final class VisionAlgorithmFactory {

    public static VisionAlgorithm createVisionAlgorithm(Context context,
                                                        String algorithmType) {
        switch (algorithmType) {
            case "人脸检测"://pass
                return new FaceDetectAglorithm(context);
            case "五官特征检测"://pass
                return new FaceLandMarkAlgorithm(context);
            case "人脸属性检测": //pass
                return new FaceAttributesAlgorithm(context);
            case "人脸分析"://pass
                return new FaceParsingAlgorithm(context);
            case "美学评分"://pass
                return new AestheticsScoreAlgorithm(context);
            case "图片分类标签": //pass
                return new LabelDetectorAlgorithm(context);
            case "场景检测": //出来的是编号
                return new SceneDetectorAlgorithm(context);
            case "图片图像超分"://pass
                return new ImageSuperResolutionAlgorithm(context);
            case "文字图像超分"://pass
                return new TxtSuperResolutionAlgorithm(context);
            case "人像分割"://pass
                return new ImageSegmentationAlgorithm(context, SegmentationConfiguration.TYPE_PORTRAIT);
            case "图像语义分割"://pass
                return new ImageSegmentationAlgorithm(context,SegmentationConfiguration.TYPE_SEMANTIC);
            case "码检测"://pass
                return new BarcodeDetectorAlgorithm(context);
            case "OCR": //pass
                return new OCRAlgorithm(context);
            case "文档检测校正"://pass
                return new DocDetectAlgorithm(context);
            default:
                return null;
        }
    }
}



