package cn.edu.hebut.iscs.kwsms.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.entity.SendStateInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.util.DateTimeUtil;

public class SmsService extends Service {

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
                    ExpertDBManager.getInstance(SmsService.this)
                            .updateSendStateInfo(
                                    intent.getStringExtra("EXPERT_CODE"), "2");
                    break;
                default:
                    // 短信发送失败
                    ExpertDBManager.getInstance(SmsService.this)
                            .updateSendStateInfo(
                                    intent.getStringExtra("EXPERT_CODE"), "3");
                    break;
            }
        }
    };

    /*
     * 短信接收情况接收广播
     */
    BroadcastReceiver backSmsBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 对方已接受短信
            ExpertDBManager.getInstance(context).updateSendStateInfo(
                    intent.getStringExtra("EXPERT_CODE"), "1");
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
                SendStateInfo sendStateInfo = new SendStateInfo();
                sendStateInfo.setExpertCode(expertInfo.getExpertCode());
                sendStateInfo.setMsgId(smsCount + "");
                sendStateInfo.setStatus("0");
                sendStateInfo.setTime(DateTimeUtil.longTimeToStrDate(
                        System.currentTimeMillis(), DateTimeUtil.format_1));
                ExpertDBManager.getInstance(SmsService.this).saveSendStateInfo(
                        SmsService.this, sendStateInfo);
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
        sendPendingIntent = PendingIntent.getBroadcast(SmsService.this,
                smsCount, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // register the Broadcast Receivers
    }

    public void setBackPendingIntent(String expertCode) {
        //  处理返回的接收状态
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        deliverIntent.putExtra("EXPERT_CODE", expertCode);
        backPendingIntent = PendingIntent.getBroadcast(SmsService.this,
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
