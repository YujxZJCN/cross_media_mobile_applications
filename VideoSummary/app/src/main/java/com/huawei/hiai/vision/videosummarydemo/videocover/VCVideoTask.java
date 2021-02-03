package com.huawei.hiai.vision.videosummarydemo.videocover;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.image.detector.AestheticsScoreDetector;
import com.huawei.hiai.vision.videosummarydemo.videocover.helpers.ConnectManager;
import com.huawei.hiai.vision.visionkit.common.Video;
import com.huawei.hiai.vision.visionkit.image.detector.AEModelConfiguration;
import com.huawei.hiai.vision.visionkit.image.detector.AEVideoResult;

import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class VCVideoTask extends AsyncTask<String, Void, String> {
    private static final String LOG_TAG = "aesthetics_score";
    private Context context;
    private VideoCoverListener listener;
    private AestheticsScoreDetector aestheticsScoreDetector;


    public VCVideoTask(Context context, VideoCoverListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... paths) {
        Log.i(LOG_TAG, "init VisionBase");
        VisionBase.init(context, ConnectManager.getInstance().getmConnectionCallback());
        if (!ConnectManager.getInstance().isConnected()) {
            ConnectManager.getInstance().waitConnect();
        }

        Log.i(LOG_TAG, "init videoCover");
        aestheticsScoreDetector = new AestheticsScoreDetector(context);
        AEModelConfiguration aeModelConfiguration;
        aeModelConfiguration = new AEModelConfiguration();
        aeModelConfiguration.getSummerizationConf().setSummerizationMaxLen(20);
        aeModelConfiguration.getSummerizationConf().setSummerizationMinLen(2);
        aestheticsScoreDetector.setAEModelConfiguration(aeModelConfiguration);
        String videoResult = null;

        videoResult = videoCover(paths);
        aestheticsScoreDetector.release();

        //release engine after detect finished
        return videoResult;
    }

    @Override
    protected void onPostExecute(String resultScore) {
        if (!resultScore.equals("-10000")) {
            listener.onTaskCompleted(resultScore);
        }
        super.onPostExecute(String.valueOf(resultScore));
    }

    private String videoCover(String[] videoPaths) {
        if (videoPaths == null) {
            Log.e(LOG_TAG, "uri is null ");
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        int position = 0;
        Video[] videos = new Video[videoPaths.length];
        for (String path : videoPaths) {
            Video video = new Video();
            video.setPath(path);
            videos[position++] = video;
        }
        jsonObject = aestheticsScoreDetector.getVideoSummerization(videos, null);

        if (jsonObject == null) {
            Log.e(LOG_TAG, "return JSONObject is null");
            return "return JSONObject is null";
        }

        if (!jsonObject.optString("resultCode").equals("0")) {
            Log.e(LOG_TAG, "return JSONObject resultCode is not 0");
            return jsonObject.optString("resultCode");
        }

        Log.d(LOG_TAG, "videoCover get score end");
        AEVideoResult aeVideoResult = aestheticsScoreDetector.convertVideoSummaryResult(jsonObject);
        if (null == aeVideoResult) {
            Log.e(LOG_TAG, "aeVideoResult is null ");
            return null;
        }
        String result = new Gson().toJson(aeVideoResult, AEVideoResult.class);
        return result;
    }
}