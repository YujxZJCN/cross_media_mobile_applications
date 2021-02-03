package hiai.plx.huawei.nlu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.hiai.nlu.model.ResponseResult;
import com.huawei.hiai.nlu.sdk.NLUAPIService;
import com.huawei.hiai.nlu.sdk.NLUConstants;
import com.huawei.hiai.nlu.sdk.OnResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import hiai.plx.huawei.nlu_demo.R;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    private EditText input_text;
    private CheckBox ckb_wordseg;
    private CheckBox ckb_wordpos;
    private CheckBox ckb_entity;
    private CheckBox ckb_chatintention;
    private CheckBox ckb_assistantintention;
    private CheckBox ckb_keyword;
    private Button btn_start;

    private static final int ITME_CHECKBOX_WS = 0;
    private static final int ITME_CHECKBOX_WP = 1;
    private static final int ITME_CHECKBOX_EN = 2;
    private static final int ITME_CHECKBOX_CI = 3;
    private static final int ITME_CHECKBOX_AI = 4;
    private static final int ITME_CHECKBOX_KW = 5;
    private static final int ITME_CHECKBOX_NUM = 6;

    private StringBuilder getText;

    // 初始化代码，并绑定UI控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_text = findViewById(R.id.editText);

        final ArrayList<CheckBox> CheckBoxInst = new ArrayList<CheckBox>();
        ckb_wordseg = findViewById(R.id.WordSegment);
        CheckBoxInst.add(ckb_wordseg);
        ckb_wordpos = findViewById(R.id.WordPos);
        CheckBoxInst.add(ckb_wordpos);
        ckb_entity = findViewById(R.id.Entity);
        CheckBoxInst.add(ckb_entity);
        ckb_chatintention = findViewById(R.id.ChatIntention);
        CheckBoxInst.add(ckb_chatintention);
        ckb_assistantintention = findViewById(R.id.AssistantIntention);
        CheckBoxInst.add(ckb_assistantintention);
        ckb_keyword = findViewById(R.id.KeyWord);
        CheckBoxInst.add(ckb_keyword);

        btn_start = findViewById(R.id.start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder textPrint = new StringBuilder();
                //for (CheckBox checkbox : CheckBoxInst){
                for (int i = 0; i < ITME_CHECKBOX_NUM; i++) {
                    CheckBox checkbox = CheckBoxInst.get(i);
                    if (checkbox.isChecked()) {
                        getText = new StringBuilder();
                        semanticAnalysis(i);
                        textPrint.append(checkbox.getText().toString() + " ： " + getText +
                                "\n");
                    }
                }
                input_text.setText(textPrint.toString());
                input_text.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
        });
    }

    // 初始化NLU模块
    @Override
    protected void onStart() {
        super.onStart();
        NLUAPIService.getInstance().init(this, new OnResultListener<Integer>() {
            @Override
            public void onResult(Integer result) {
            }
        }, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NLUAPIService.getInstance().onDestroy();
    }

    // 语义分析
    private void semanticAnalysis(int type) {
        String bodyTxt = input_text.getText().toString();
        if (!bodyTxt.isEmpty()) {
            switch (type) {
                case ITME_CHECKBOX_WS:
                    wordSegmentProcess(bodyTxt);
                    break;
                case ITME_CHECKBOX_WP:
                    wordPosProcess(bodyTxt);
                    break;
                case ITME_CHECKBOX_EN:
                    entityProcess(bodyTxt);
                    break;
                case ITME_CHECKBOX_CI:
                    chatIntentionProcess(bodyTxt);
                    break;
                case ITME_CHECKBOX_AI:
                    assistantIntentionProcess(bodyTxt);
                    break;
                case ITME_CHECKBOX_KW:
                    keyWordsProcess(bodyTxt);
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(this, getResources().getText(R.string.input_text_is_null),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 字段分割
    private void wordSegmentProcess(String message) {
        try {
            String requestJson = "{text:'" + message + "',type:1}";
            ResponseResult respResult =
                    NLUAPIService.getInstance().getWordSegment(requestJson,
                            NLUConstants.REQUEST_TYPE_LOCAL);
            String result = respResult.getJsonRes();
            if (isEmpty(result)) {
                getText.append(getResources().getText(R.string.input_text_not_support).toString());
            } else {
                showWordSegmentResult(result);
            }
        } catch (Exception e) {
            Toast.makeText(this, getResources().getText(R.string.input_text_is_null),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 展示字段分割结果
    private void showWordSegmentResult(String result) throws JSONException {
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.has("words")) {
            JSONArray words = jsonResult.getJSONArray("words");
            getText.append(" | ");
            for (int i = 0; i < words.length(); i++) {
                String word = words.getString(i);
                getText.append(word);
                getText.append(" | ");
            }
        }
    }

    // 处理单词位置
    private void wordPosProcess(String message) {
        try {
            String requestJson = "{text:'" + message + "',type:1}";
            ResponseResult respResult =
                    NLUAPIService.getInstance().getWordPos(requestJson,
                            NLUConstants.REQUEST_TYPE_LOCAL);
            // 获取接口返回结果，参考接口文档返回使用
            String result = respResult.getJsonRes();
            if (isEmpty(result)) {
                getText.append(getResources().getText(R.string.input_text_not_support).toString());
            } else {
                showWordPosResult(result);
            }
        } catch (Exception e) {
            Toast.makeText(this, getResources().getText(R.string.input_text_is_null),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 展示效果
    private void showWordPosResult(String result) throws JSONException {
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.has("pos")) {
            JSONArray words = jsonResult.getJSONArray("pos");
            getText.append(" \n ");
            for (int i = 0; i < words.length(); i++) {
                JSONObject wordObj = words.getJSONObject(i);
                String word = wordObj.getString("word");
                String tag = data.tagNameMap.get(wordObj.getString("tag"));
                getText.append("(")
                        .append(tag)
                        .append(")")
                        .append(word)
                        .append(" ");
            }
        }
    }

    private void entityProcess(String message) {
        try {
            String requestJson = "{text:'" + message + "'}";
            ResponseResult respResult =
                    NLUAPIService.getInstance().getEntity(requestJson,
                            NLUConstants.REQUEST_TYPE_LOCAL);
            String result = respResult.getJsonRes();
            if (isEmpty(result)) {
                getText.append(getResources().getText(R.string.input_text_not_support).toString());
            } else {
                //getText.append((formatJson(result)));
                showEntityResult(result);
            }
        } catch (Exception e) {
            Toast.makeText(this, getResources().getText(R.string.input_text_is_null),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showEntityResult(String result) throws JSONException {
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.has("entity")) {
            getText.append("\n");
            JSONObject entityObj = jsonResult.getJSONObject("entity");
            for (Map.Entry<String, String> entry : data.entityNameMap.entrySet()) {
                String key = entry.getKey();
                if (entityObj.has(key)) {
                    getText.append(data.entityNameMap.get(key))
                            .append(": ");
                    JSONArray entityArr = entityObj.getJSONArray(key);
                    for (int i = 0; i < entityArr.length(); i++) {
                        JSONObject uintObj = entityArr.getJSONObject(i);
                        String entityText = uintObj.getString("oriText");
                        getText.append(entityText)
                                .append(" ");
                    }
                    getText.append("\n");
                }
            }
        }
    }

    // 关键词处理
    private void keyWordsProcess(String message) {
        try {
            // 构建可选参数
            String requestJson = "{body:'" + message + "',number:4}";
            ResponseResult respResult =
                    NLUAPIService.getInstance().getKeywords(requestJson,
                            NLUConstants.REQUEST_TYPE_LOCAL);
            if (null != respResult) { // 获取接口返回结果，参考接口文档返回使用
                String result = respResult.getJsonRes();
                if (isEmpty(result)) {
                    getText.append(getResources().getText(R.string.input_text_not_support).toString());
                } else {
                    showKeyWordsResult(result);
                    //getText.append((formatJson(result)));
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, getResources().getText(R.string.input_text_is_null),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 展示关键词处理结果
    private void showKeyWordsResult(String result) throws JSONException {
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.has("keywords")) {
            JSONArray words = jsonResult.getJSONArray("keywords");
            getText.append(" | ");
            for (int i = 0; i < words.length(); i++) {
                String word = words.getString(i);
                getText.append(word);
                getText.append(" | ");
            }
        }
    }

    // 聊天意图处理
    private void chatIntentionProcess(String message) {
        try {
            String requestJson = "{text:'" + message + "',isSender:1}";
            ResponseResult respResult =
                    NLUAPIService.getInstance().getChatIntention(requestJson,
                            NLUConstants.REQUEST_TYPE_LOCAL);
            if (null != respResult) {
                // 获取接口返回结果，参考接口文档返回使用
                String result = respResult.getJsonRes();
                if (isEmpty(result)) {
                    getText.append("\n 发送方:" +
                            getResources().getText(R.string.input_text_not_support).toString());
                } else {
                    getText.append("\n 发送方: 这句话包含的意图有：" +
                            getIntentionName(result));
                } }

            requestJson = "{text:'" + message + "',isSender:0}";
            respResult = NLUAPIService.getInstance().getChatIntention(requestJson,
                    NLUConstants.REQUEST_TYPE_LOCAL);
            if (null != respResult) {
                // 获取接口返回结果，参考接口文档返回使用
                String result = respResult.getJsonRes();
                if (isEmpty(result)) {
                    getText.append("\n 接收方:" +
                            getResources().getText(R.string.input_text_not_support).toString());
                } else {
                    getText.append("\n 接收方: 这句话包含的意图有：" +
                            getIntentionName(result));
                } }
        } catch (Exception e) {
            Toast.makeText(this, getResources().getText(R.string.input_text_is_null),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 获取聊天意图
    private String getIntentionName(String result) throws JSONException {
        // result
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.has("intentions")) {
            JSONArray intentionResults = jsonResult.getJSONArray("intentions");
            if (intentionResults != null && intentionResults.length() > 0) {
                JSONObject intentionObj = intentionResults.getJSONObject(0);
                if (intentionObj.has("name")) {
                    return intentionObj.getString("name");
                } } }
        return "";
    }

    // 辅助意图过程
    private void assistantIntentionProcess(String message) {
        try {
            // 构建可选参数
            String requestJson = "{text:'" + message + "'}";
            ResponseResult respResult =
                    NLUAPIService.getInstance().getAssistantIntention(
                            requestJson, NLUConstants.REQUEST_TYPE_LOCAL);
            if (null != respResult) {
                // 获取接口返回结果，参考接口文档返回使用
                String result = respResult.getJsonRes();
                if (isEmpty(result)) {
                    getText.append(getResources().getText(R.string.input_text_not_support).toString());
                } else {
                    String IntentionName = getIntentionName(result);
                    getText.append("\n 这句话包含的意图有：" + IntentionName);
                    IntentionProcess(IntentionName);
                } }
        } catch (Exception e) {
            Toast.makeText(this, getResources().getText(R.string.input_text_is_null),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void IntentionProcess(String Intention) {
        switch (Intention) {
            case "call":
                this.startActivity(new Intent(Intent.ACTION_DIAL));
                break;
            case "openBluetooth":
                this.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case "closeBluetooth":
                this.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            default:
                break;
        }
    }
}
