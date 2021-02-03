package hiai.plx.huawei.com.algorithm;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hiai.vision.barcode.BarcodeDetector;
import com.huawei.hiai.vision.visionkit.barcode.Barcode;
import com.huawei.hiai.vision.visionkit.common.Frame;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BarcodeDetectorAlgorithm extends VisionAlgorithm {

    BarcodeDetector barcodeDetector;
    List<Barcode> barcodeDetectorResult;
    public BarcodeDetectorAlgorithm(Context context) {
        barcodeDetector = new BarcodeDetector(context);
    }

    @Override
    public int run(Bitmap bitmap) throws JSONException {
        Frame frame = new Frame();
        frame.setBitmap(bitmap);
        JSONObject jsonRes = barcodeDetector.detect(frame, null);//获取码检测结果
        barcodeDetectorResult = barcodeDetector.convertResult(jsonRes);
        barcodeDetector.release();
        return Integer.valueOf(jsonRes.getString("resultCode"));
    }

    @Override
    public String getResult() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("码检测执行成功^_^\n");
        sbuf.append("码信息如下：\n");
        for (Barcode item: barcodeDetectorResult) {
            sbuf.append(" " + item.toString() + "\n");
        }
        return sbuf.toString();
    }
}
