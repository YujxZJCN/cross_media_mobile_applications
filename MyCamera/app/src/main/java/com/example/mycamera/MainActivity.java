package com.example.mycamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.res.AssetFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

import com.example.mycamera.algorithm.VisionAlgorithm;
import com.example.mycamera.algorithm.VisionAlgorithmFactory;
import com.example.mycamera.auxiliary.VisionFunHandler;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;

public class MainActivity extends AppCompatActivity {

    public static  final int TAKE_PHOTO = 1;
    private static final int OPEN_ALBUM = 3;
    private ImageView selectImageView;
    private Uri imageUri;
    private Bitmap bitmap;
    Button myCamera;
    Button myAlbum;
    Button detectButton;
    String pathToFile;
    TextView txtOutput;

    // 场景类别
    final String sceneClasses[] = {"BEACH", "BLUESKY",
            "SUNSET",
            "FOOD", "FLOWER"
    };

    // UI控件
    MediaPlayer player = new MediaPlayer();
    private boolean isWorking = false;
    private Object mWaitResult = new Object();

    // Storage Permissions
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    // 初始化，用于连接代码与UI控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCamera = findViewById(R.id.my_camera);
        myAlbum = findViewById(R.id.my_album);
        detectButton = findViewById(R.id.detectButton);
        selectImageView = findViewById(R.id.selectImageView);
        txtOutput = findViewById(R.id.txtOutput);
        txtOutput.setTypeface(null, Typeface.BOLD);
        txtOutput.setAlpha(0);
        detectButton.setAlpha(0);
        VisionBase.init(MainActivity.this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                //当与服务连接成功时，会调用此回调方法；
                //您可以在这里进行detector类的初始化、标记服务连接状态等
                Log.i("Text", "onServiceConnect ");
            }
            @Override
            public void onServiceDisconnect() {
                //当与服务断开时，会调用此回调方法；
                //您可以选择在这里进行服务的重连，
                //或者对异常进行处理；
                Log.i("Text", "onServiceDisconnect");
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS_STORAGE,2);
        }

        myCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureAndClassify();
            }
        });

        myAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageAndClassify();
            }
        });

        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectImage();
            }
        });

        mThread.start();
    }

    // 监测到图片时...
    private void detectImage() {
        if (isWorking) {
            txtOutput.setText("场景检测正在运行");
            return;
        }
        txtOutput.setText("场景检测准备运行");
        synchronized (mWaitResult) {
            mWaitResult.notifyAll();
        }
    }

    // 通过拍照获取图片
    private void takePictureAndClassify() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = createPhotoFile();
            if(photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.mycamera.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePic,1);
            }
        }
    }

    // 从相册中选取照片
    private void chooseImageAndClassify() {
        Intent fetchPic = new Intent(Intent.ACTION_GET_CONTENT);
        fetchPic.setType("image/*");
        if(fetchPic.resolveActivity(getPackageManager())!= null){
            startActivityForResult(fetchPic,3);
        }
    }

    // 创建文件路径
    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("myLog", "Excep: " + e.toString());
        }
        return image;
    }

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (mWaitResult) {
                        mWaitResult.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e("debug", e.getMessage());
                }
                Log.d("debug", "Running one computing instance");
                isWorking = true;
                VisionAlgorithm alg = VisionAlgorithmFactory.createVisionAlgorithm(MainActivity.this,
                        "场景检测");
                if (null == alg) {
                    isWorking = false;
                    continue;
                }
                alg.setHandler(mHander);
                try {
                    int result = alg.run(bitmap);
                    if (result != 0) {
                        VisionFunHandler.sendMsgToHandler(mHander,
                                VisionFunHandler.HANDLE_WHAT_MODEL_SETTEXT, "发生错误，错误码是：" + result + "\n");
                    } else {
                        VisionFunHandler.sendMsgToHandler(mHander,
                                VisionFunHandler.HANDLE_WHAT_MODEL_SETTEXT, alg.getResult());
                    }
                } catch (Exception e) {
                    VisionFunHandler.sendMsgToHandler(mHander,
                            VisionFunHandler.HANDLE_WHAT_MODEL_SETTEXT, "发生无法处理的错误\n");
                    e.printStackTrace();
                }
                isWorking = false;
            }
        }
    });

    // 处理返回内容
    @SuppressLint("HandlerLeak")
    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int status = msg.what;
            Log.d("debug", "handleMessage status = " + status);
            switch (status) {
                case VisionFunHandler.HANDLE_WHAT_MODEL_SETTEXT:
                    txtOutput.setText((String)msg.obj);
                    try {
                        //播放 assets/a2.mp3 音乐文件
                        AssetFileDescriptor fd = getAssets().openFd((String)msg.obj + ".mp3");
                        for (int i = 0; i < sceneClasses.length; i++) {
                            if (sceneClasses[i] == (String)msg.obj) {
                                fd = getAssets().openFd(sceneClasses[i] + ".mp3");
                                break;
                            }
                        }
                        player = new MediaPlayer();
                        player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case VisionFunHandler.HANDLE_WHAT_MODEL_SETIMAGE:
                    selectImageView.setImageBitmap((Bitmap)msg.obj);
                    break;
                default:
                    txtOutput.setText("Handler execute error.\n");
            }
        }
    };

    // 通过resultCode进行相应操作
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    bitmap = BitmapFactory.decodeFile(pathToFile);
                    selectImageView.setImageBitmap(bitmap);
                    detectButton.setAlpha(1);
                    txtOutput.setAlpha(1);
                }
                break;

            case OPEN_ALBUM:
                if(resultCode == RESULT_OK){
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                        selectImageView.setImageBitmap(bitmap);
                        detectButton.setAlpha(1);
                        txtOutput.setAlpha(1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /** 释放资源*/
        VisionBase.destroy();

    }
}

