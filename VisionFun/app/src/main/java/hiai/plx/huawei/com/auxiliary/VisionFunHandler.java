package hiai.plx.huawei.com.auxiliary;

import android.os.Handler;
import android.os.Message;

public class VisionFunHandler {

    public static final int HANDLE_WHAT_MODEL_SETTEXT = 1;
    public final static int HANDLE_WHAT_MODEL_SETIMAGE = 2;
    public static void sendMsgToHandler(Handler handler, int status, Object
            content) {
        Message msg = new Message();
        msg.what = status;
        msg.obj = content;
        handler.sendMessage(msg);
    }

}
