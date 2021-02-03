package hiai.plx.huawei.com;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hiai.vision.face.FaceComparator;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.face.FaceCompareResult;

import org.json.JSONObject;

import java.io.FileNotFoundException;

public class CompareActivity extends AppCompatActivity {
    TextView txtCompare;
    Button getImgButton1;
    Button getImgButton2;
    Button compareRun;
    Button retButton;
    ImageView compareImage1;
    ImageView compareImage2;
    private static final int REQUEST_IMAGE_IN_VIEW1 = 1; //命令参数
    private static final int REQUEST_IMAGE_IN_VIEW2 = 2; //命令参数
    FaceComparator faceComparator = new FaceComparator(CompareActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare); //设置该 Activity 中要显示的内容视图
        txtCompare = (TextView) findViewById(R.id.textCompareView);
        getImgButton1 = (Button) findViewById(R.id.getImage1);
        getImgButton2 = (Button) findViewById(R.id.getImage2);
        compareRun = (Button) findViewById(R.id.runCompare);
        retButton = (Button) findViewById(R.id.returnButton);
        compareImage1 = (ImageView) findViewById(R.id.imageCompare1);
        compareImage2 = (ImageView) findViewById(R.id.imageCompare2);
        getImgButton1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                chooseImageFromStorage(REQUEST_IMAGE_IN_VIEW1);
            }
        });
        getImgButton2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                chooseImageFromStorage(REQUEST_IMAGE_IN_VIEW2);
            }
        });
        retButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        compareRun.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThread compareThread = new MyThread();
                compareThread.start();
            }
        });
    }

    private void chooseImageFromStorage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
            return;
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == REQUEST_IMAGE_IN_VIEW1)
                    compareImage1.setImageBitmap(BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(data.getData())));
                else if (requestCode == REQUEST_IMAGE_IN_VIEW2)
                    compareImage2.setImageBitmap(BitmapFactory.decodeStream(
                            getContentResolver().openInputStream(data.getData())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            Frame frame1 = new Frame();
            Frame frame2 = new Frame();
            frame1.setBitmap(((BitmapDrawable)
                    (compareImage1).getDrawable()).getBitmap());
            frame2.setBitmap(((BitmapDrawable)
                    (compareImage2).getDrawable()).getBitmap());
            StringBuilder txtPrint = new StringBuilder();
            try {
                JSONObject jsonObject = faceComparator.faceCompare(frame1, frame2,
                        null);
                FaceCompareResult result =
                        faceComparator.convertResult(jsonObject);
                int retCode = Integer.valueOf(jsonObject.getString("resultCode"));
                if (0 != retCode) {
                    txtPrint.append("发生错误，错误码是：" + retCode + "\n");
                } else {
                    txtPrint.append("人脸识别成功 ^_^ \n");
                    txtPrint.append(" 人脸相似度：" + result.getSocre() + "\n");
                    if (result.isSamePerson())
                        txtPrint.append(" 对比结果为同一个人\n");
                    else
                        txtPrint.append(" 对比结果不为同一人\n");
                }
            } catch (Exception e) {
                txtPrint.append(e.getMessage().toString());
            }
            txtCompare.setText(txtPrint.toString());
        }
    }
}