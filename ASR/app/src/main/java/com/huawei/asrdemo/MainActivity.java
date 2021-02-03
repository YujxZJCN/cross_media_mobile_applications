package com.huawei.asrdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.asrdemo.util.StoragePermission;
import com.huawei.hiai.asr.AsrConstants;
import com.huawei.hiai.asr.AsrError;
import com.huawei.hiai.asr.AsrListener;
import com.huawei.hiai.asr.AsrRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    // UI控件
    private Button startRecord;
    private Button stopListener;
    private Button cancelListener;
    private LinearLayout recordTestResultLL;
    private TextView showRecordTestResult;
    private TextView recordTestEndTest;

    private AsrRecognizer mAsrRecognizer;
    private String mResult;
    private int count = 0;
    private long startTime;
    private long endTime;
    private long waitTime;
    private MyAsrListener mMyAsrListener = new MyAsrListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        StoragePermission.getAllPermission(this);

        initView();

    }

    // 初始化界面，将代码和UI控件捆绑在一起
    private void initView() {

        // test record view
        startRecord = (Button) findViewById(R.id.start_record);
        startRecord.setOnClickListener(this);

        stopListener = (Button) findViewById(R.id.button_stop_listening);
        stopListener.setOnClickListener(this);

        cancelListener = (Button) findViewById(R.id.button_cacel_listening);
        cancelListener.setOnClickListener(this);

        showRecordTestResult = (TextView) findViewById(R.id.show_record_test_result);
        recordTestResultLL = (LinearLayout) findViewById(R.id.record_result_ll);
        recordTestEndTest = (TextView) findViewById(R.id.record_end_test);

    }

    // 点击事件
    @Override
    public void onClick(View v) {
        // 初始化默认测试文件
        switch (v.getId()) {
            case R.id.start_record:
                initEngine();
                startListening();
                break;
            case R.id.button_stop_listening:
                stopListening();
                break;
            case R.id.button_cacel_listening:
                cancelListening();
                break;
            default:
                break;
        }
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

    // 开始监听返回事件
    private void startListening() {
        if (count == 0) {
            startTime = getTimeMillis();
        }
        Intent intent = new Intent();
        intent.putExtra(AsrConstants.ASR_VAD_FRONT_WAIT_MS, 4000);
        intent.putExtra(AsrConstants.ASR_VAD_END_WAIT_MS, 3000);
        intent.putExtra(AsrConstants.ASR_TIMEOUT_THRESHOLD_MS, 20000);
        if (mAsrRecognizer != null) {
            mAsrRecognizer.startListening(intent);
        }
    }

    // 暂停监听
    private void stopListening() {
        Log.d(TAG, "stopListening() ");
        if (mAsrRecognizer != null) {
            mAsrRecognizer.stopListening();
        }
    }

    // 取消监听
    private void cancelListening() {
        Log.d(TAG, "cancelListening() ");
        if (mAsrRecognizer != null) {
            mAsrRecognizer.cancel();
            mAsrRecognizer.destroy();
            mAsrRecognizer = null;
        }
    }

    // 获取毫秒级别的时间
    public long getTimeMillis() {
        long time = System.currentTimeMillis();
        return time;
    }

    // 监听器
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
            endTime = getTimeMillis();
            waitTime = endTime - startTime;
            mResult = getOnResult(results, AsrConstants.RESULTS_RECOGNITION);
            stopListening();
            startRecord.setEnabled(true);
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
                showRecordTestResult.setText(sb.toString());
                Log.d(TAG, "getOnResult: " + sb.toString());
                recordTestResultLL.setVisibility(View.VISIBLE);
                recordTestEndTest.setText(waitTime + "毫秒");
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
