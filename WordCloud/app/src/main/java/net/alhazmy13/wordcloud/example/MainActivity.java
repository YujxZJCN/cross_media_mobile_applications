package net.alhazmy13.wordcloud.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hiai.nlu.model.ResponseResult;
import com.huawei.hiai.nlu.sdk.NLUAPIService;
import com.huawei.hiai.nlu.sdk.NLUConstants;
import com.huawei.hiai.nlu.sdk.OnResultListener;

import net.alhazmy13.example.R;
import net.alhazmy13.wordcloud.ColorTemplate;
import net.alhazmy13.wordcloud.WordCloud;
import net.alhazmy13.wordcloud.WordCloudView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    // UI控件
    private EditText input_text;
    private TextView key_words;
    private Button btn_start;
    private WordCloudView wordCloud;

    private StringBuilder getText;

    List<WordCloud> list ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 关联代码和UI控件
        wordCloud = findViewById(R.id.wordCloud);
        key_words = findViewById(R.id.keywords);
        input_text = findViewById(R.id.editText);
        btn_start = findViewById(R.id.button_start);

        // 为btn_start按钮添加点击事件
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bodyTxt = input_text.getText().toString();
                if (!bodyTxt.isEmpty()){

                    getText = new StringBuilder();

                    keyWordsProcess(bodyTxt);

                    key_words.setText("关键词："+getText);
                    key_words.setVisibility(View.VISIBLE);

                    generateRandomText();

                    // 设置wordCloud的属性
                    wordCloud.setDataSet(list);
                    wordCloud.setSize(400,400);
                    wordCloud.setColors(ColorTemplate.JOYFUL_COLORS);
                    wordCloud.notifyDataSetChanged();
                    wordCloud.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(MainActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 随机生成文本
    private void generateRandomText() {
        String[] data = getText.toString().split(" ");
        list = new ArrayList<>();
        Random random = new Random();
        for (String s : data) {
            list.add(new WordCloud(s,random.nextInt(50)));
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

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

    // 关键词功能
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
                    getText.append("很抱歉，文字功能未识别");
                } else {
                    showKeyWordsResult(result);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "输入内容不能为空",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showKeyWordsResult(String result) throws JSONException {
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.has("keywords")) {
            JSONArray words = jsonResult.getJSONArray("keywords");
            for (int i = 0; i < words.length(); i++) {
                String word = words.getString(i);
                getText.append(word);
                getText.append(" ");
            }
        }
    }

}


