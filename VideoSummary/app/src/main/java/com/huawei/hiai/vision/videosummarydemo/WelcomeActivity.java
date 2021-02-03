package com.huawei.hiai.vision.videosummarydemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.huawei.hiai.vision.videosummarydemo.videocover.VideoCoverActivity;
import com.huawei.hiai.vision.videosummarydemo.videocover.helpers.Constants;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private Button btn_summary;
    private Button btn_cover;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tryToStart();

        btn_summary = findViewById(R.id.btn_tab_snack1);
        btn_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, VideoCoverActivity.class);
                startActivity(intent);
            }
        });

        btn_cover = findViewById(R.id.btn_tab_snack2);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "User grant the permission of READ_EXTERNAL_STORAGE");
                } else {
                    Toast.makeText(this, "Please grant the permission", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "User did not grant the permission of READ_EXTERNAL_STORAGE");
                    this.finish();
                }
                break;
        }
    }

    private void tryToStart() {
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
    }
}
