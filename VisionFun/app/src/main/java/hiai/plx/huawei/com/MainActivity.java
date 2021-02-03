package hiai.plx.huawei.com;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hiai.plx.huawei.com.algorithm.VisionAlgorithm;
import hiai.plx.huawei.com.algorithm.VisionAlgorithmFactory;
import hiai.plx.huawei.com.auxiliary.VisionFunHandler;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1; //命令参数
    private static final int REQUEST_IMAGE_GALLERY = 2; //命令参数
    private static final String TAG = "VisionFun";

    //定义功能名称二维数组
    String spinnerContent[][] = {
            //firstSpinner(1) + secondSpinnerItems(...).
            {"人脸分析", "人脸检测", "五官特征检测", "人脸属性检测", "人脸分析", "美学评分"},
            {"分类检测", "图片分类标签", "场景检测"},
            {"超分辨率", "图片图像超分", "文字图像超分"},
            {"图像分割", "人像分割", "图像语义分割"},
            {"检测识别", "码检测", "OCR识别"},
            {"其他", "文档检测校正"}
    };

    // 屏幕小部件
    Spinner lstFirst;
    Spinner lstSecond;
    TextView txtOutput;
    Button btnCamera;
    Button btnGallery;
    Button btnRun;
    ImageView imgMain;

    private Object mWaitResult = new Object();
    private Uri imageUri;
    private Bitmap bitmapSelected;
    private boolean isWorking = false;
    private String runningAlg;

    //在onCreate函数中绑定这些控件。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fill widget value.
        lstFirst = findViewById(R.id.lstFirst);
        lstSecond = findViewById(R.id.lstSecond);
        txtOutput = findViewById(R.id.txtOutput);
        txtOutput.setMovementMethod(ScrollingMovementMethod.getInstance());
        imgMain = findViewById(R.id.imgMain);
        btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCameraClick();
            }
        });
        btnGallery = findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryClick();
            }
        });
        btnRun = findViewById(R.id.btnRun);
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRunClick();
            }
        });

        fillSpinnerContent();
        requestPermissions();

        /** 应用VisionBase静态类进行初始化，异步拿到服务的连接*/
        VisionBase.init(this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                /** 这个回调方法在服务连接成功时被调用；您可以执行探测器类的初始化，标记服务连接状态等*/
                Log.i(TAG, "onServiceConnect ");
            }
            @Override
            public void onServiceDisconnect() {
                /** 当服务断开时，调用此回调方法；您可以选择在此重新连接服务，或者处理异常*/
                Log.i(TAG, "onServiceDisconnect");
            }
        });

        mThread.start();

    }

    private void onCameraClick() {
        takePictureFromCamera();
    }
    private void onGalleryClick() {
        chooseImageFromStorage();
    }
    private void onRunClick() {
        if (isWorking) {
            txtOutput.setText(runningAlg + " is running!\n");
            return;
        }
        txtOutput.setText(lstSecond.getSelectedItem().toString() + " prepare running.....\n");
        synchronized (mWaitResult) {
            mWaitResult.notifyAll();
        }
    }

    // 通过拍照获取图片
    private void takePictureFromCamera() {
        File fileImage = new File(getExternalCacheDir(), "image.jpg");
        try {
            if (fileImage.exists()){
                fileImage.delete();
            }
            fileImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.huawei.hiai.tutorial.visionfun.fileProvider", fileImage);
        } else {
            imageUri = Uri.fromFile(fileImage);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // 从相册中选取图片
    private void chooseImageFromStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if(intent.resolveActivity(getPackageManager() )!= null){
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED)
            return;
        if (resultCode == RESULT_OK){
            try {
                if (requestCode == REQUEST_IMAGE_CAPTURE)
                    bitmapSelected =
                            BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                else if (requestCode == REQUEST_IMAGE_GALLERY)
                    bitmapSelected =
                            BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                imgMain.setImageBitmap(bitmapSelected);
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // 请求相机和存储权限
    private void requestPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_IMAGE_GALLERY);
                }
                permission = ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
                if(permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[] {Manifest.permission.CAMERA},
                            REQUEST_IMAGE_CAPTURE);
                } }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    //填充下拉列表信息
    private void fillSpinnerContent() {
        List firstContent = new ArrayList<String>();
        for (int i = 0; i < spinnerContent.length; i++) {
            firstContent.add(spinnerContent[i][0]);
        }
        ArrayAdapter<String> arrAdapter= new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, firstContent); //适配器
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //设置样式
        lstFirst.setAdapter(arrAdapter); //加载适配器
        lstFirst.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillSubSpinnerContent(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                txtOutput.setText("Nothing");
            }
        });
    }

    private void fillSubSpinnerContent(int index) {
        List secondContent = new ArrayList<String>();
        for (int i = 1; i < spinnerContent[index].length; i++) {
            secondContent.add(spinnerContent[index][i]);
        }
        ArrayAdapter<String> arrAdapter= new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, secondContent);
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lstSecond.setAdapter(arrAdapter);
    }
    //=======================================================================================
    // 执行线程
    //=======================================================================================
    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (mWaitResult) {
                        mWaitResult.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
                Log.d(TAG, "Running one computing instance");
                isWorking = true;
                runningAlg = lstSecond.getSelectedItem().toString();
                VisionAlgorithm alg = VisionAlgorithmFactory.createVisionAlgorithm(MainActivity.this,
                            runningAlg);
                if (null == alg){
                    isWorking = false;
                    continue; }
                alg.setHandler(mHander);
                try {
                    int result = alg.run(bitmapSelected);
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

    //=======================================================================================
    // Handler 消息处理
    //=======================================================================================
    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int status = msg.what;
            Log.d(TAG, "handleMessage status = " + status);
            switch (status) {
                case VisionFunHandler.HANDLE_WHAT_MODEL_SETTEXT:
                    txtOutput.append((String)msg.obj);
                    break;
                case VisionFunHandler.HANDLE_WHAT_MODEL_SETIMAGE:
                    imgMain.setImageBitmap((Bitmap)msg.obj);
                    break;
                default:
                    txtOutput.append("Handler execute error.\n");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /** 释放资源*/
        VisionBase.destroy();

    }
}

