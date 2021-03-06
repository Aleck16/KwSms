package cn.edu.hebut.iscs.kwsms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.entity.ReplyStateInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.service.AutoReplyService;
import cn.edu.hebut.iscs.kwsms.service.AutoUpdateService;
import cn.edu.hebut.iscs.kwsms.util.ConstantValue;
import cn.edu.hebut.iscs.kwsms.util.PrefUtils;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import cn.edu.hebut.iscs.kwsms.view.MyProgressDialog;

/**
 * Created by Aleck_ on 2016/9/23.
 */
public class MessageReceiver extends BroadcastReceiver {

    private String address="";              //接收发短信方的号码
    private String fullMessage="";          //接收发短信方的短信内容

    private List<ExpertInfo> AutoSendlist;


    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle=intent.getExtras();
        Object[] pdus=(Object[])bundle.get("pdus"); //提取短信消息
        SmsMessage[] messages=new SmsMessage[pdus.length];
        for(int i=0;i<messages.length;i++){
            messages[i]=SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        address=messages[0].getOriginatingAddress().trim(); //获取发送方号码
        //处理电话号码：//手机号：+8615032763060 ；去掉前面：“+86”
        String tel="";
        switch (address.length()){      //取号码的最后11位
            case 11:
                tel=address;
                break;
            case 12:        //截取手机号，去掉“+86”
                tel=address.substring(1);
                break;
            case 13:        //截取手机号，去掉“+86”
                tel=address.substring(2);
                break;
            case 14:        //截取手机号，去掉“+86”
                tel=address.substring(3);
                break;
            default:
                tel=address;
                break;
        }

        if(ExpertDBManager.getInstance(context).isExpert(tel)){    //判断收到的号码是否为专家表里面，如果是就处理
            fullMessage="";
            for(SmsMessage message:messages){
                fullMessage+=message.getMessageBody();      //获取短信内容
            }
            fullMessage=fullMessage.trim();     //去掉短信前后空格
            //Log.d("MessageReceier",address+fullMessage);        //打印短信内容

            int status=3;       //默认为回复其他

            if("y".equals(fullMessage)||"Y".equals(fullMessage)||"Yes".equals(fullMessage)||"yes".equals(fullMessage)){       //如果短信内容是"y"或者"Y",则启动自动回复服务

                int flag = ExpertDBManager.getInstance(context).updateAutoReplyNum(tel,2);   //标记回复了y，等待自动回复用户名和密码,状态码为2
                status=1;       //标记回复y

                Log.d("flag","flag="+flag+tel);
                //启动自动回复服务
//            Intent intentAuto=new Intent(context, AutoUpdateService.class);

//            if(PrefUtils.getBoolean(context, ConstantValue.AUTO_SEND_SUCCESS,false)&&PrefUtils.getBoolean(context,ConstantValue.SEND_SUCCESS,false)){
//                context.startService(intentAuto);       //启动服务
//            }



                //  启动一个Service
//            Intent serviceIntent = new Intent(context, AutoReplyService.class);
//            serviceIntent.putExtra("address",address);      //address类型：手机号：+8615032763060
//            String autoReplyContent=ExpertDBManager.getInstance(context).getAutoReplyContent(tel);  //手机号：15032763060
//            if(!autoReplyContent.isEmpty()){        //如果自动回复内容不为空
//                serviceIntent.putExtra("replaycontent",autoReplyContent);       //回复的内容需要从数据库里面调用
//                context.startService(serviceIntent);
//            }
            }else if("n".equals(fullMessage)||"N".equals(fullMessage)||"No".equals(fullMessage)||"no".equals(fullMessage)){
                status=2;
            }else {     //其他状态码为3，回复其他内容，设置其他
                /**
                 * 此处需要判断一下，如果专家之前回复过Y，且用户名和密码已经自动发送成功，即YES_NO_OTHER=1，且AUTO_REPLY_NUM=1
                 * 此种情况主要针对：当我们自动回复了用户名和密码之后，庄家继续回复一条：“好的”，或“收到”等。
                 */
                ReplyStateInfo replyStateInfo = ExpertDBManager.getInstance(context).queryReplyInfo(tel);
                if(replyStateInfo.getYesNoOther().equals("1")){  //此专家已经回复了Y，且用户名和密码已经自动回复了，现在再回复其他的信息，状态码不发生改变
                    status=1;
                }else{
                    status=3;
                }
            }
            //更新数据库，用户已经回复
            int flag = ExpertDBManager.getInstance(context).updateReplyNum(tel,status);   //标记回复了y，等待自动回复用户名和密码,状态码为2
            if(flag==1){
                Log.d("","更新状态成功");
            }

        }


    }

    /**
     * @param context
     * 自动回复短信方法
     */
    private void AutoReplyList(final Context context){
        PrefUtils.setBoolean(context,ConstantValue.AUTO_SEND_SUCCESS,false);    //标记自动回复正在进行
        AutoSendlist = new ArrayList<ExpertInfo>();

        ExpertDBManager.getInstance(context)
                .queryNoSendList();     //获得没有发送的
        AutoSendlist = ExpertDBManager.getInstance(
                context).queryAutoReplyList();

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MyProgressDialog.startProgressDialog(
                        context,"正在发送短信，请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                if (AutoSendlist != null && AutoSendlist.size() > 0) {
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
                    ToastUtil.showToast(context, "自动回复用户名及密码完成！");
                    AutoSendlist.clear();
                } else {
                    ToastUtil.showToast(context,
                            "没有等待自动回复的短信！");
                }
            }
        }.execute();
    }
}
