package cn.edu.hebut.iscs.kwsms.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.receiver.AutoUpdateReceiver;
import cn.edu.hebut.iscs.kwsms.util.ConstantValue;
import cn.edu.hebut.iscs.kwsms.util.PrefUtils;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import cn.edu.hebut.iscs.kwsms.view.MyProgressDialog;

/**
 * Created by Aleck_ on 2016/9/1.
 */
public class AutoUpdateService extends Service {

    private List<ExpertInfo> AutoSendlist;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //如果当前没有短信栈正在发送的话，就执行自动发送短信方法
                if(PrefUtils.getBoolean(getApplicationContext(), ConstantValue.AUTO_SEND_SUCCESS,false)&&PrefUtils.getBoolean(getApplicationContext(),ConstantValue.SEND_SUCCESS,false)){
                    AutoReplyList(getApplicationContext()); //自动回复短信方法
                }
            }
        }).start();



        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=1*60*1000;//这是8小时的毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi= PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
//                case 1:
//                    MyProgressDialog.startProgressDialog(
//                            getApplicationContext(),"正在发送短信，请稍后...");
//                    break;
//                case 2:
//                    ToastUtil.showToast(getApplicationContext(), "自动回复用户名及密码完成！");
//                    break;
//                case 3:
//                    ToastUtil.showToast(getApplicationContext(),
//                            "没有等待自动回复的短信！");
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * @param context
     * 自动回复短信方法
     */
    private void AutoReplyList(final Context context){

        AutoSendlist = new ArrayList<ExpertInfo>();

        AutoSendlist = ExpertDBManager.getInstance(
                context).queryAutoReplyList();

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);

//                MyProgressDialog.startProgressDialog(
//                        getApplicationContext(),"正在发送短信，请稍后...");


            }

            @Override
            protected Boolean doInBackground(Void... params) {

                if (AutoSendlist != null && AutoSendlist.size() > 0) {
                    PrefUtils.setBoolean(context, ConstantValue.AUTO_SEND_SUCCESS,false);    //标记自动回复正在进行
                    // 跳转到发送短信服务service
                    Intent intent = new Intent(context,
                            AutoReplyService.class);
                    Bundle budle = new Bundle();
                    int i = 0;
                    budle.putInt("listlenght", AutoSendlist.size());
                    for (ExpertInfo expertInfo : AutoSendlist) {
                        budle.putSerializable("expertInfolist" + i, expertInfo);
                        i++;
                    }

                    intent.putExtras(budle);
                    intent.setAction("cn.edu.hebut.iscs.sms_service");
                    context.startService(intent);
                    AutoSendlist.clear();       //清空上一次的list，继续封装数据发送
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                MyProgressDialog.stopProgressDialog();
                if (result) {
                    Message message = new Message();
                    message.what = 2;
                    mHandler.sendMessage(message);
//                    ToastUtil.showToast(context, "自动回复用户名及密码完成！");
                    AutoSendlist.clear();
                } else {
                    Message message = new Message();
                    message.what = 3;
                    mHandler.sendMessage(message);
//                    ToastUtil.showToast(context,
//                            "没有等待自动回复的短信！");
                }
            }
        }.execute();
    }

//    /**
//     * 更新天气信息。
//     */
//    private void updateWeather(){
//        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
//        String weatherCode=prefs.getString("weather_code","");
//        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
//        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                Utility.handleWeatherResponse(AutoUpdateService.this,response);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
}
