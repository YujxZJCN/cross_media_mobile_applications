package com.huawei.hiai.vision.videosummarydemo.videocover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huawei.hiai.vision.videosummarydemo.R;
import com.huawei.hiai.vision.videosummarydemo.videocover.adapter.AEVideoAdapter;
import com.huawei.hiai.vision.videosummarydemo.videocover.bean.AEVideoItemModel;
import com.huawei.hiai.vision.videosummarydemo.videocover.helpers.Constants;
import com.huawei.hiai.vision.videosummarydemo.videocover.utils.GetPathFromUri4kitkat;
import com.huawei.hiai.vision.videosummarydemo.videocover.utils.ProUtils;
import com.huawei.hiai.vision.visionkit.image.detector.AEVideoResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.huawei.hiai.vision.videosummarydemo.videocover.utils.CutVideoUtils.cropMp4;
import static com.huawei.hiai.vision.videosummarydemo.videocover.utils.MergeVideoUtils.appendMp4List;

public class VideoCoverActivity extends AppCompatActivity implements VideoCoverListener {

    private String TAG = "VideoCoverActivity";

    private Button btnLoadVideo;
    private Button btnNewVideo;
    private VideoView videoView;
    private ProgressDialog mDialog;

    private RecyclerView recyclerView;
    private AEVideoAdapter videoAdapter;
    private List<AEVideoItemModel> videoItems = new ArrayList<>();
    private String[] videoPaths;
    private String videoAbsPaths;
    private String result_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_select);

        btnLoadVideo = findViewById(R.id.btn_load_video);
        btnNewVideo = findViewById(R.id.btn_new_video);
        recyclerView = findViewById(R.id.recycler_view);
        videoView = findViewById(R.id.videoView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoAdapter = new AEVideoAdapter(videoItems);
        recyclerView.setAdapter(videoAdapter);

        btnLoadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVideo();
            }
        });

        btnNewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<String> cutVideoPaths = new ArrayList<>();

                double[][] timeList = resultParse(result_content);

                double[] startTimeListFinal = timeList[0];
                double[] endTimeListFinal = timeList[1];

                for(int i =0;i<startTimeListFinal.length;i++){
                    String finalFilePath = ProUtils.getSDCartPath() + File.separator  + System.currentTimeMillis() +"_cut.mp4";
                    cutVideoPaths.add(finalFilePath);
                    cropMp4(videoAbsPaths,startTimeListFinal[i],endTimeListFinal[i],finalFilePath);
                }

                String resultVideoPath = ProUtils.getSDCartPath() + File.separator  + System.currentTimeMillis() +"_result.mp4";

                try{
                appendMp4List(cutVideoPaths,resultVideoPath);
                Toast.makeText(VideoCoverActivity.this, "视频总结生成成功！", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Log.println(Log.ERROR,"appendMp4List Error", String.valueOf(e));
                }

                //播放视频
                videoView.setVideoPath(resultVideoPath);
                MediaController mediaController = new MediaController(VideoCoverActivity.this);
                videoView.setMediaController(mediaController);
                videoView.requestFocus();
            }
        });
    }

    private void loadVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("video/mp4");
        startActivityForResult(intent, Constants.VIDEO_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.VIDEO_GALLERY) {
            showProgressDialog();
            getSelectedVideos(data);
            VCVideoTask cnnTask = new VCVideoTask(this, this);
            cnnTask.execute(videoPaths);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTaskCompleted(String result) {

        AEVideoResult aeVideoResult;
        try {
            aeVideoResult = new Gson().fromJson(result, AEVideoResult.class);
        } catch (JsonSyntaxException e) {
            aeVideoResult = null;
            mDialog.dismiss();
        }
        if (aeVideoResult == null) {
            Toast.makeText(this, "error code:" + result, Toast.LENGTH_LONG).show();
            return;
        }
        AEVideoItemModel test_ae = new AEVideoItemModel(getBaseContext(), aeVideoResult, videoPaths[0], "");
        videoItems.add(new AEVideoItemModel(getBaseContext(), aeVideoResult, videoPaths[0], ""));
        result_content = test_ae.getAESegments();
        videoAdapter.notifyDataSetChanged();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void getSelectedVideos(Intent data) {
            videoPaths = new String[1];
            Uri videoURI = data.getData();
            grantUriPermission("com.huawei.hiai", videoURI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoPaths[0] = videoURI.toString();
            videoAbsPaths = GetPathFromUri4kitkat.getPath(this,videoURI);
    }

    private void showProgressDialog() {
            mDialog = ProgressDialog.show(VideoCoverActivity.this,
                    getString(R.string.predicting), getString(R.string.wait), true);
    }

    private double[][] resultParse(String result){

        String[] eachresult = result.split(".mp4: ");

        double[][] timeList = new double[2][eachresult.length-1];
        double[] startTimeList = new double[eachresult.length-1];
        double[] endTimeList = new double[eachresult.length-1];

        for(int i=1;i<eachresult.length;i++){
            String TimeTemp = eachresult[i].split("\n ")[0];

            String startTimeTemp1 = TimeTemp.split(" - ")[0];
            String startTimeTemp2 = startTimeTemp1.split("s")[0];
            double startTime = Double.parseDouble(startTimeTemp2);
            startTimeList[i-1] = startTime;

            String endTimeTemp1 = TimeTemp.split(" - ")[1];
            String endTimeTemp2 = endTimeTemp1.split("s")[0];
            double endTime = Double.parseDouble(endTimeTemp2);
            endTimeList[i-1] = endTime;
        }
        timeList[0]=startTimeList;
        timeList[1]=endTimeList;

        Log.println(Log.ERROR, "timeList.length", startTimeList.length+"");

        return timeList;
    };
}
