package cn.edu.hebut.iscs.kwsms.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.edu.hebut.iscs.kwsms.receiver.MessageReceiver;

/**
 * Created by Aleck_ on 2016/9/23.
 */
public class StartAutoReplyService extends Service {

    //接收回复短信使用
    private IntentFilter receiverFilter;
    private MessageReceiver messageReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {         //开启一个线程，启动接收短信广播
            @Override
            public void run() {
                //接收短信的广播
                receiverFilter=new IntentFilter();
                receiverFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
                messageReceiver=new MessageReceiver();
                registerReceiver(messageReceiver,receiverFilter);
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {           //服务停止时调用
        //Log.d("AutoReplyService","服务停止了，接收广播注销了");
        unregisterReceiver(messageReceiver);        //关闭接收短信的广播
        return super.stopService(name);
    }
}