package hiai.plx.huawei.com.algorithm;

import android.graphics.Bitmap;
import android.os.Handler;
import org.json.JSONException;

public abstract class VisionAlgorithm {

    private Handler handler;

    public Handler getHandler() {
        return handler; }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public abstract int run(Bitmap bitmap) throws JSONException;

    public abstract String getResult();
}
