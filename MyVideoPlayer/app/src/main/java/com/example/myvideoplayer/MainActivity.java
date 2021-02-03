package com.example.myvideoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private Button video_play = null;
    private Button video_pause = null;
    private Button video_stop = null;
    private VideoView video_player = null;

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        video_play = findViewById(R.id.video_play);
        video_pause = findViewById(R.id.video_pause);
        video_stop = findViewById(R.id.video_stop);
        video_player = findViewById(R.id.video_player);

        if (Build.VERSION.SDK_INT > 21) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        initVideoPlayer();
        video_pause.setEnabled(false);
        video_stop.setEnabled(false);

        // 为video_play按钮添加点击事件
        video_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!video_player.isPlaying()){
                    video_player.start();
                    video_play.setEnabled(false);
                    video_pause.setEnabled(true);
                    video_stop.setEnabled(true);
                }
            }
        });

        // 为video_pause按钮添加点击事件
        video_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_player.isPlaying()){
                    video_player.pause();
                    video_play.setEnabled(true);
                    video_pause.setEnabled(false);
                    video_stop.setEnabled(true);
                }
            }
        });

        // 为video_stop按钮添加点击事件
        video_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_player.isPlaying()){
                    video_player.resume();
                    video_play.setEnabled(true);
                    video_pause.setEnabled(false);
                    video_stop.setEnabled(false);
                }
            }
        });
    }

    // 初始化视频播放器
    private  void initVideoPlayer(){
        video_player.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.video);
    }

    // 申请权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(video_player != null){
            video_player.suspend();
        }
    }
}
