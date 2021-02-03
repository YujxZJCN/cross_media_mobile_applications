package com.huawei.hiai.vision.videosummarydemo.videocover.bean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.huawei.hiai.vision.videosummarydemo.utils.VideoUtils;
import com.huawei.hiai.vision.visionkit.image.detector.AEVideoResult;
import com.huawei.hiai.vision.visionkit.image.detector.aestheticsmodel.AestheticVideoSummerization;


public class AEVideoItemModel {

    private String videoPath;
    private AEVideoResult aeVideoResult;
    private String trimVideoPath;
    private Context mContext;


    public AEVideoItemModel(Context context, AEVideoResult s, String src, String dest) {
        this.videoPath = src;
        this.aeVideoResult = s;
        this.trimVideoPath = dest;
        this.mContext = context;
    }

    @SuppressLint("DefaultLocale")
    public String getAESegments() {
        StringBuilder sb = new StringBuilder();
        if (aeVideoResult.getVideoStaticCover() != null) {
            sb.append(String.format("%.0f to %.0f\n",
                    aeVideoResult.getVideoStaticCover().floatValue() / 1000000.0f,
                    aeVideoResult.getVideoStaticCover().floatValue() / 1000000.0f));

        } else if (aeVideoResult.getVideoLiveCover() != null) {
            sb.append(String.format("%.0f to %.0f\n",
                    aeVideoResult.getVideoLiveCover().first.floatValue() / 1000000.0f,
                    aeVideoResult.getVideoLiveCover().second.floatValue() / 1000000.0f));
        } else if (aeVideoResult.getVideoSummerization() != null) {
            float totalTime = 0.0f;
            for (AestheticVideoSummerization videoSummerization : aeVideoResult.getVideoSummerization()) {
                String path = videoSummerization.getSrcVideo();
                String absolutePath = VideoUtils.getRealPathFromURI(mContext, Uri.parse(path));
                String filename=absolutePath.substring(absolutePath.lastIndexOf("/")+1);
                sb.append(filename);
                sb.append(": ");
                sb.append(videoSummerization.getStartFrameTimeStamp() / 1000000.0f);
                sb.append("s - ");
                sb.append(videoSummerization.getEndFrameTimeStamp() / 1000000.0f);
                sb.append("s\n");
                totalTime += ((videoSummerization.getEndFrameTimeStamp() - videoSummerization.getStartFrameTimeStamp()) / 1000000.0f);
            }
            sb.append(String.format("\ntotal time: %s\n", totalTime));
        }
        if (trimVideoPath != "") {
            sb.append(String.format("\nTrimmed video at: %s\n", trimVideoPath));
        }

        return sb.toString();
    }

    public String getVideoPath() {
        return VideoUtils.getRealPathFromURI(mContext, Uri.parse(videoPath));
    }

    public void setVideoPath(String p) {
        this.videoPath = p;
    }
}
