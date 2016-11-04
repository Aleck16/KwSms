package cn.edu.hebut.iscs.kwsms.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.entity.SendStateInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.receiver.MessageReceiver;
import cn.edu.hebut.iscs.kwsms.util.ConstantValue;
import cn.edu.hebut.iscs.kwsms.util.DateTimeUtil;
import cn.edu.hebut.iscs.kwsms.util.PrefUtils;

/**
 * Created by Aleck_ on 2016/9/23.
 * AutoReplyService:自动回复用户名和密码服务
 */
public class AutoReplyService extends Service {

    private int mSendNum=0;

    private String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    private String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

    private PendingIntent sendPendingIntent;
    private PendingIntent backPendingIntent;

    public static int smsCount = 0;
    private ExpertInfo expertInfo;

    /*
     * 短信发送情况接受广播
     */
    BroadcastReceiver sentSmsBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    // 短信发送成功
                    // 自动回复短信发送成功
                    ExpertDBManager.getInstance(getApplicationContext()).updateAutoReplyNum(expertInfo.getTel(),3);   //3：表示正在自动回复中

                    break;
                default:
                    // 短信发送失败
                    // 状态码4：自动回复短信发送失败
                    ExpertDBManager.getInstance(getApplicationContext()).updateAutoReplyNum(expertInfo.getTel(),4);   //4：

                    break;
            }

            mSendNum++;
            if(mSendNum>= intent.getIntExtra("listlenght", 0)){     //如果当前短信全部发送完毕，设置标记为AUTO_SEND_SUCCESS为true
                PrefUtils.setBoolean(getApplicationContext(), ConstantValue.AUTO_SEND_SUCCESS,true);
            }
        }
    };

    /*
     * 短信接收情况接收广播
     */
    BroadcastReceiver backSmsBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 对方已接收到自动回复短信
            ExpertDBManager.getInstance(getApplicationContext()).updateAutoReplyNum(expertInfo.getTel(),1);   //1：已经自动回复成功。表示正在自动回复中
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*
		 * 注册广播
		 */

        PrefUtils.setBoolean(getApplicationContext(), ConstantValue.AUTO_SEND_SUCCESS,false);    //设置发送完毕的状态码为false。开始发送短信

        registerReceiver(sentSmsBr, new IntentFilter(SENT_SMS_ACTION));
        registerReceiver(backSmsBr, new IntentFilter(DELIVERED_SMS_ACTION));
		/*
		 * 发送短信
		 */
        for (int i = 0; i < intent.getIntExtra("listlenght", 0); i++) {
//            //短信每50条发送一次
//            if (i%50==0){
//                try {
//                    Thread.sleep(20000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }


            expertInfo = (ExpertInfo) intent
                    .getSerializableExtra("expertInfolist" + i);
            if (expertInfo != null) {

                setSentPendingIntent(expertInfo.getExpertCode());
                setBackPendingIntent(expertInfo.getExpertCode());

                //更新自动回复成功
                ExpertDBManager.getInstance(getApplicationContext()).updateAutoReplyNum(expertInfo.getTel(),3);   //3：表示正在自动回复中

//                SendStateInfo sendStateInfo = new SendStateInfo();
//                sendStateInfo.setExpertCode(expertInfo.getExpertCode());
//                sendStateInfo.setMsgId(smsCount + "");
//                sendStateInfo.setStatus("4");           //选择发送后，把状态都写成0；修改为4：接收失败
//                sendStateInfo.setTime(DateTimeUtil.longTimeToStrDate(
//                        System.currentTimeMillis(), DateTimeUtil.format_1));
//                ExpertDBManager.getInstance(AutoReplyService.this).saveSendStateInfo(
//                        AutoReplyService.this, sendStateInfo);
                sendSMS(expertInfo.getTel(), expertInfo.getMsgContent());
                smsCount++;

            }


        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sentSmsBr);
        unregisterReceiver(backSmsBr);
    }

    public void setSentPendingIntent(String expertCode) {
        // 处理返回的发送状态
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        sentIntent.putExtra("EXPERT_CODE", expertCode);
        sendPendingIntent = PendingIntent.getBroadcast(AutoReplyService.this,
                smsCount, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // register the Broadcast Receivers
    }

    public void setBackPendingIntent(String expertCode) {
        //  处理返回的接收状态
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        deliverIntent.putExtra("EXPERT_CODE", expertCode);
        backPendingIntent = PendingIntent.getBroadcast(AutoReplyService.this,
                smsCount, deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 调用短信接口发短信，含接收报告和发送报告
     *
     * @param phoneNumber
     * @param message
     */

    public void sendSMS(String phoneNumber, String message) {
        //  获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager
                .getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text,
                    sendPendingIntent, backPendingIntent);
        }
    }
}








//
//    private String address="";      //自动回复的号码
//    private String replaycontent="";    //自动回复的内容
//    private IntentFilter sendFilter;
//    private SendStatusReceiver sendStatusReceiver;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        address=intent.getStringExtra("address");       //得到回复的号码
//        replaycontent=intent.getStringExtra("replaycontent");   //得到回复的内容
//
//        sendFilter=new IntentFilter();
//        sendFilter.addAction("SENT_SMS_ACTION");
//        sendStatusReceiver=new SendStatusReceiver();
//        registerReceiver(sendStatusReceiver,sendFilter);
//
//
//        //自动回复发短信放到子线程里面
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Intent sendIntent=new Intent("SENT_SMS_ACTION");
//                PendingIntent pi=PendingIntent.getBroadcast(getApplicationContext(),0,sendIntent,0);
//
//                SmsManager smsManager= SmsManager.getDefault();
//                smsManager.sendTextMessage(address,null,replaycontent,pi,null);   //发送回复短信
//            }
//        }).start();
//
//
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    class SendStatusReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(getResultCode()== Activity.RESULT_OK){
//                //自动回复短信成功
//                Log.d("AutoReplyService","自动回复成功");
//                ExpertDBManager.getInstance(getApplicationContext()).updateAutoReplyNum(address,1);   //标记已经自动回复,状态码为1
//                }else{
//                //自动回复短信发送失败
//                Log.d("AutoReplyService","自动回复短信发送失败");
//            }
//            //注销自动回复广播
//            unregisterReceiver(sendStatusReceiver);
//            //注销此自动回复服务
//            stopSelf();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        //注销自动回复广播
//        unregisterReceiver(sendStatusReceiver);
//        //注销此自动回复服务
//        stopSelf();
//    }
//}
