package hiai.plx.huawei.com.algorithm;

import android.content.Context;

import com.huawei.hiai.vision.visionkit.image.segmentation.SegmentationConfiguration;

public final class VisionAlgorithmFactory {

    public static VisionAlgorithm createVisionAlgorithm(Context context,
                                                        String algorithmType) {
        switch (algorithmType) {
            case "人脸检测":
                return new FaceDetectAglorithm(context);
            case "五官特征检测":
                return new FaceLandMarkAlgorithm(context);
            case "人脸属性检测":
                return new FaceAttributesAlgorithm(context);
            case "人脸分析":
                return new FaceParsingAlgorithm(context);
            case "美学评分":
                return new AestheticsScoreAlgorithm(context);
            case "图片分类标签":
                return new LabelDetectorAlgorithm(context);
            case "场景检测":
                return new SceneDetectorAlgorithm(context);
            case "图片图像超分":
                return new ImageSuperResolutionAlgorithm(context);
            case "文字图像超分":
                return new TxtSuperResolutionAlgorithm(context);
            case "人像分割":
                return new ImageSegmentationAlgorithm(context, SegmentationConfiguration.TYPE_PORTRAIT);
            case "图像语义分割":
                return new ImageSegmentationAlgorithm(context,SegmentationConfiguration.TYPE_SEMANTIC);
            case "码检测":
                return new BarcodeDetectorAlgorithm(context);
            case "OCR识别":
                return new OCRAlgorithm(context);
            case "文档检测校正":
                return new DocDetectAlgorithm(context);
            default:
                return null;
        }
    }
}
