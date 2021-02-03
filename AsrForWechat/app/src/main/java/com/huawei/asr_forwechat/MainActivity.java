package com.huawei.asr_forwechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.asr_forwechat.util.StoragePermission;
import com.huawei.hiai.asr.AsrConstants;
import com.huawei.hiai.asr.AsrError;
import com.huawei.hiai.asr.AsrListener;
import com.huawei.hiai.asr.AsrRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ImageButton start_talking;
    private ImageView mood_icon;
    private TextView showRecordResult;

    private AsrRecognizer mAsrRecognizer;
    private String mResult;
    private int count = 0;
    private MyAsrListener mMyAsrListener = new MyAsrListener();

    private String[][] mood_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        StoragePermission.getAllPermission(this);

        initView();

    }

    // 初始化试图，将代码与UI控件绑定
    private void initView() {

        String[] angry_list = new String[]{"气","怒","骂","坏"};
        String[] haha_list = new String[]{"哈","笑","激动","兴奋","爽"};
        String[] happy_list = new String[]{"开心","快乐","愉快"};
        String[] sad_list = new String[]{"哭","惨","悲伤","伤心"};
        String[] shy_list = new String[]{"害羞","不好意思","面子"};
        String[] xianqi_list = new String[]{"嫌弃","走开","恶心"};

        mood_list = new  String[][]{angry_list,haha_list,happy_list,sad_list,shy_list,xianqi_list};

        // 为start_talking按钮添加点击事件
        start_talking = (ImageButton)findViewById(R.id.start_talking);
        start_talking.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    start_talking.setImageDrawable(getResources().getDrawable(R.drawable.record_icon2));
                    showRecordResult.setText("您正在讲话……");
                    initEngine();
                    startListening();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    stopListening();
                    cancelListening();
                    start_talking.setImageDrawable(getResources().getDrawable(R.drawable.record_icon));
                    String result_text = showRecordResult.getText().toString();
                    int mood_index = -1;
                    for(int i=0;i<mood_list.length;i++){
                        String[] moods = mood_list[i];
                        for(int m=0;m<moods.length;m++){
                            String mood = moods[m];
                            if(result_text.indexOf(mood)!= -1){
                                mood_index = i;
                                break;
                            }
                        }
                    }
                    if (mood_index != -1)
                    {
                        int icon_id = getResources().getIdentifier("mood_"+String.valueOf(mood_index+1),"drawable",getPackageName());
                        Toast toast = Toast.makeText(MainActivity.this,"icon_id:"+String.valueOf(icon_id),Toast.LENGTH_LONG);
                        toast.show();
                        mood_icon.setImageDrawable(getResources().getDrawable(icon_id));
                    }
                    else
                    {
                        Toast toast = Toast.makeText(MainActivity.this,"识别不到情绪",Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                return true;
            }
        });

        mood_icon = (ImageView)findViewById(R.id.mood_image);

        showRecordResult = (TextView) findViewById(R.id.show_record_result);

    }

    // 初始化ASR引擎
    private void initEngine() {
        Log.d(TAG, "initEngine() ");
        mAsrRecognizer = AsrRecognizer.createAsrRecognizer(this);
        Intent initIntent = new Intent();
        initIntent.putExtra(AsrConstants.ASR_AUDIO_SRC_TYPE, AsrConstants.ASR_SRC_TYPE_RECORD);
        if (mAsrRecognizer != null) {
            mAsrRecognizer.init(initIntent, mMyAsrListener);
        }
    }

    // 开始监听事件
    private void startListening() {
        Intent intent = new Intent();
        intent.putExtra(AsrConstants.ASR_VAD_FRONT_WAIT_MS, 4000);
        intent.putExtra(AsrConstants.ASR_VAD_END_WAIT_MS, 3000);
        intent.putExtra(AsrConstants.ASR_TIMEOUT_THRESHOLD_MS, 20000);
        if (mAsrRecognizer != null) {
            mAsrRecognizer.startListening(intent);
        }
    }

    // 停止监听事件
    private void stopListening() {
        Log.d(TAG, "stopListening() ");
        if (mAsrRecognizer != null) {
            mAsrRecognizer.stopListening();
        }
    }

    // 取消监听事件
    private void cancelListening() {
        Log.d(TAG, "cancelListening() ");
        if (mAsrRecognizer != null) {
            mAsrRecognizer.cancel();
            mAsrRecognizer.destroy();
            mAsrRecognizer = null;
        }
    }

    // 获取毫秒级时间
    public long getTimeMillis() {
        long time = System.currentTimeMillis();
        return time;
    }

    // ASR监听类
    private class MyAsrListener implements AsrListener {
        @Override
        public void onInit(Bundle params) {
            Log.d(TAG, "onInit() called with: params = [" + params + "]");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech() called");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged() called with: rmsdB = [" + rmsdB + "]");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.i(TAG, "onBufferReceived() called with: buffer = [" + buffer + "]");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech: ");

        }

        @Override
        public void onError(int error) {
            Log.d(TAG, "onError() called with: error = [" + error + "]");
            if (error == AsrError.ERROR_SERVER_INSUFFICIENT_PERMISSIONS) {
                if (mAsrRecognizer != null) {
                    mAsrRecognizer.startPermissionRequestForEngine();
                }
            }
        }

        @Override
        public void onResults(Bundle results) {
            Log.d(TAG, "onResults() called with: results = [" + results + "]");
            mResult = getOnResult(results, AsrConstants.RESULTS_RECOGNITION);
            stopListening();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults() called with: partialResults = [" + partialResults + "]");
            getOnResult(partialResults, AsrConstants.RESULTS_PARTIAL);
        }

        @Override
        public void onEnd() {

        }

        private String getOnResult(Bundle partialResults, String key) {
            Log.d(TAG, "getOnResult() called with: getOnResult = [" + partialResults + "]");
            String json = partialResults.getString(key);
            final StringBuffer sb = new StringBuffer();
            try {
                JSONObject result = new JSONObject(json);
                JSONArray items = result.getJSONArray("result");
                for (int i = 0; i < items.length(); i++) {
                    String word = items.getJSONObject(i).getString("word");
                    double confidences = items.getJSONObject(i).getDouble("confidence");
                    sb.append(word);
                    Log.d(TAG, "asr_engine: result str " + word);
                    Log.d(TAG, "asr_engine: confidence " + String.valueOf(confidences));
                }
                Log.d(TAG, "getOnResult: " + sb.toString());

                showRecordResult.setText(sb.toString());
                Log.d(TAG, "getOnResult: " + sb.toString());
            } catch (JSONException exp) {
                Log.w(TAG, "JSONException: " + exp.toString());
            }
            return sb.toString();
        }


        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent() called with: eventType = [" + eventType + "], params = [" + params + "]");
        }

        @Override
        public void onLexiconUpdated(String s, int i) {

        }
    }
}
