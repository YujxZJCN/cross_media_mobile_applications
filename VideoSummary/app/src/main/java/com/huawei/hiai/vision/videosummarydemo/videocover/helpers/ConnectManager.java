package com.huawei.hiai.vision.videosummarydemo.videocover.helpers;

import com.huawei.hiai.vision.common.ConnectionCallback;

public class ConnectManager {
    private static ConnectManager mInstance = null;
    private Object mWaitConnect = new Object();
    private boolean isConnected = false;

    protected ConnectManager() {
    }

    public static ConnectManager getInstance() {
        if (mInstance == null) {
            mInstance = new ConnectManager();
        }
        return mInstance;
    }

    public ConnectionCallback getmConnectionCallback() {
        return mConnectionCallback;
    }

    private ConnectionCallback mConnectionCallback = new ConnectionCallback() {
        @Override
        public void onServiceConnect() {
            synchronized (mWaitConnect) {
                setConnected(true);
                mWaitConnect.notifyAll();
            }
        }

        @Override
        public void onServiceDisconnect() {
            synchronized (mWaitConnect) {
                setConnected(false);
                mWaitConnect.notifyAll();
            }
        }
    };

    public synchronized boolean isConnected() {
        return isConnected;
    }

    public synchronized void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void waitConnect() {
        try {
            synchronized (mWaitConnect) {
                if (!isConnected) {
                    mWaitConnect.wait();
                }
            }
        } catch (InterruptedException e) {
        }
    }
}