package cn.edu.hebut.iscs.kwsms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.hebut.iscs.kwsms.adapter.ExpertAdapter;
import cn.edu.hebut.iscs.kwsms.adapter.SendExpertAdapter;
import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.service.SmsService;
import cn.edu.hebut.iscs.kwsms.util.ConstantValue;
import cn.edu.hebut.iscs.kwsms.util.PrefUtils;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import cn.edu.hebut.iscs.kwsms.view.MyDialog;
import cn.edu.hebut.iscs.kwsms.view.MyProgressDialog;

/**
 * Created by lixueyang on 16-8-17.
 */
public class SendSmsResultActivity extends BaseTitleActivity {
    @BindView(R.id.textView_send_num)
    TextView textViewSendNum;
    @BindView(R.id.listView_send_sms)   //未发送的ListView
    ListView listViewSendSms;
    @BindView(R.id.rb_not_send)
    RadioButton rbNotSend;
    @BindView(R.id.rb_send_success)
    RadioButton rbSendSuccess;
    @BindView(R.id.rb_accept_false)
    RadioButton rbAcceptFalse;
    @BindView(R.id.rb_send_false)
    RadioButton rbSendFalse;
    @BindView(R.id.radio_group_send)
    RadioGroup radioGroupSend;

    // 不同发送状态的专家信息
    /**
     * notSendlist：还未发送, sendSuccesslist：发送成功, sendFalselist：发送失败,
     acceptFalselist：接收失败;
     */
    private List<ExpertInfo> notSendlist, sendSuccesslist, sendFalselist,
            acceptFalselist;
    private List<ExpertInfo> list;          // 选中发送中的（要发送的专家信息）
    private List<ExpertInfo> newlist;          // 选中发送中的（要发送的专家信息）
    private ExpertAdapter successExpertAdapter;         //发送成功栏目显示的内容
    private SendExpertAdapter sendExpertAdapter;         //还未发送栏目显示的内容

    private SendExpertAdapter sendExpertAdapterShow;    //显示在未发送栏目里面的列表：等于数据库里面未发送的减去发送中的。

    private SendExpertAdapter sendReceiveFailExpertAdapter; //单独建一个接收失败的适配器
    // 短信发送状态，配合底部按钮使用

    //当前显示的页面
    public int showStatus = 0;  //0:未发送页面；1：发送成功页面；2：接收失败页面；3：发送失败页面； 默认为未发送页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSetContentView(R.layout.activity_send_sms_result);
        ButterKnife.bind(this);
        initUIHeader();
        initUI();
        initRadioGroup();
    }


    private void initUI() {

        PrefUtils.setBoolean(getApplicationContext(), ConstantValue.SEND_SUCCESS,true); //设置正在发送中的短信是否完成状态码，默认为true（完成）

        sendSuccesslist = new ArrayList<ExpertInfo>();
        sendFalselist = new ArrayList<ExpertInfo>();
        acceptFalselist = new ArrayList<ExpertInfo>();
        notSendlist = new ArrayList<ExpertInfo>();
        list = new ArrayList<ExpertInfo>();

        initList();
        sendExpertAdapter = new SendExpertAdapter(SendSmsResultActivity.this);
        sendExpertAdapter.clear();
        sendExpertAdapter.addAll(notSendlist);          //添加还未发送列表到sendExpertAdapter中


        sendReceiveFailExpertAdapter = new SendExpertAdapter(SendSmsResultActivity.this);
        sendReceiveFailExpertAdapter.addAll(acceptFalselist);   //添加接收失败列表到sendReceiveFailExpertAdapter中

        successExpertAdapter = new ExpertAdapter(SendSmsResultActivity.this);
        successExpertAdapter.addAll(sendSuccesslist);
        list.clear();   //清除之前的
//        list = sendExpertAdapter.getList();     //未发送的和发送失败的
        //将还未发送列表显示到界面中
        listViewSendSms.setAdapter(sendExpertAdapter);
        textViewSendNum.setText("未发送人数为：" + notSendlist.size());
    }

    private void initUIHeader() {
        setTitleBarText("短信发送情况");
        getTitleTextViewRight().setText("发送");
        getTitleTextViewRight().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final MyDialog dialog = new MyDialog(
                        SendSmsResultActivity.this, R.style.YesNoDialog,
                        new MyDialog.OnCustomDialogListener() {

                            @Override
                            public void back(int type) {
                                switch (type) {
                                    case 0:
                                        break;
                                    case 1:         //发送按钮

                                        if(PrefUtils.getBoolean(getApplicationContext(), ConstantValue.AUTO_SEND_SUCCESS,false)&&PrefUtils.getBoolean(getApplicationContext(),ConstantValue.SEND_SUCCESS,false)){
                                            sendSMSAsyncTask();     //调用发送短信事件
                                        }else if(PrefUtils.getBoolean(getApplicationContext(),ConstantValue.SEND_SUCCESS,false)){
                                            ToastUtil.showToast(SendSmsResultActivity.this,"自动回复进行中...请等待发送完后再继续发送！");
                                        }else{
                                            ToastUtil.showToast(SendSmsResultActivity.this,"短信正在发送，请等待发送完后再继续发送！");
                                        }


                                        //更新显示内容，将刚刚选中的移除
//                                        for (ExpertInfo expertInfo : list) {
//                                            ExpertDBManager.getInstance(SendSmsResultActivity.this)
//                                                    .updateSendStateInfo(
//                                                            expertInfo.getExpertCode(), "3");   //将发送中的短信放到发送失败里面，下次缓冲就直接发送了。将状态码改为发送中，发送失败状态码为：3
//                                            notSendlist.remove(expertInfo); //移除正在发送中的
//                                            sendExpertAdapter.remove(expertInfo);
//                                        }
//                                        sendExpertAdapter.notifyDataSetChanged();
//                                        listViewSendSms.setAdapter(sendExpertAdapter);
//                                        textViewSendNum.setText("未发送人数为：" + notSendlist.size());

                                    default:
                                        break;
                                }
                            }
                        }, "温馨提示", "请问您是否发送短信？", "发送", "取消");
                dialog.show();

            }
        });

    }

    /*
     * 初始化各个状态显示的数据
     */
    public void initList() {

        sendSuccesslist = ExpertDBManager.getInstance(
                SendSmsResultActivity.this).querySendResultList("1");
        acceptFalselist = ExpertDBManager.getInstance(
                SendSmsResultActivity.this).querySendResultList("2");
        sendFalselist = ExpertDBManager.getInstance(SendSmsResultActivity.this)
                .querySendResultList("3");
        notSendlist = ExpertDBManager.getInstance(SendSmsResultActivity.this)
                .queryNoSendList();     //获得没有发送的
//        notSendlist.addAll(ExpertDBManager.getInstance(
//                SendSmsResultActivity.this).querySendResultList("0"));      //获得发送失败的,状态号为0
    }

    /*
     * 发送短信
     */
    private void sendSMSAsyncTask() {
//        sendExpertAdapter.setList();
        list.clear();
        if(showStatus==2){      //接收失败页面
            newlist = sendReceiveFailExpertAdapter.getList();      //绑定接收失败的数据
            for(ExpertInfo expertInfo:newlist){
                list.add(expertInfo);
            }
        }else{                  //为还未发送页面
            newlist = sendExpertAdapter.getList();                 //绑定为发送和发送失败的数据
            for(ExpertInfo expertInfo:newlist){
                list.add(expertInfo);
            }
        }
        sendReceiveFailExpertAdapter.clearList();
        sendReceiveFailExpertAdapter.clear();
        sendReceiveFailExpertAdapter.addAll(acceptFalselist);
        sendExpertAdapter.clearList();      //清空内部List
        sendExpertAdapter.clear();
        sendExpertAdapter.addAll(notSendlist);          //添加还未发送列表到sendExpertAdapter中
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MyProgressDialog.startProgressDialog(
                        SendSmsResultActivity.this, "正在发送短信，请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                if (list != null && list.size() > 0) {
                    // 跳转到发送短信服务service
                    Intent intent = new Intent(SendSmsResultActivity.this,
                            SmsService.class);
                    Bundle budle = new Bundle();
                    int i = 0;
                    budle.putInt("listlenght", list.size());
                    for (ExpertInfo expertInfo : list) {
                        budle.putSerializable("expertInfolist" + i, expertInfo);
                        i++;
                    }

                    intent.putExtras(budle);
                    intent.setAction("cn.edu.hebut.iscs.sms_service");
                    startService(intent);
                    list.clear();       //清空上一次的list，继续封装数据发送
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
                    ToastUtil.showToast(SendSmsResultActivity.this, "发送完成！");
                    list.clear();
                    sendExpertAdapter.getStateList().clear();
                    sendExpertAdapter.clear();
                    sendReceiveFailExpertAdapter.getStateList().clear();
                    sendReceiveFailExpertAdapter.clear();
                    initList();
                    switch (showStatus) {
                        case 0:
                            sendExpertAdapter.addAll(notSendlist);
                            textViewSendNum.setText("未发送人数为：" + notSendlist.size());
                            break;
                        case 2:
                            //sendExpertAdapter.addAll(acceptFalselist);
                            sendReceiveFailExpertAdapter.addAll(acceptFalselist);       //
                            textViewSendNum
                                    .setText("接收失败人数为：" + acceptFalselist.size());
                            break;
                        case 3:
                            sendExpertAdapter.addAll(sendFalselist);
                            textViewSendNum.setText("发送失败人数为：" + sendFalselist.size());
                            break;
                    }
                    sendExpertAdapter.notifyDataSetChanged();
                    sendReceiveFailExpertAdapter.notifyDataSetChanged();    //改变适配器绑定的内容
                } else {
                    ToastUtil.showToast(SendSmsResultActivity.this,
                            "未选中短信，请选择你要发送的短信！");
                }
            }
        }.execute();
        sendExpertAdapter.getStateList().clear();
        sendExpertAdapter.clear();
        sendReceiveFailExpertAdapter.getStateList().clear();
        sendReceiveFailExpertAdapter.clear();
    }

    /*
     * 底部按钮的点击事件
     */
    public void initRadioGroup() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_send);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                switch (arg1) {
                    case R.id.rb_send_success:
                        getTitleTextViewRight().setVisibility(View.GONE);
                        sendSuccesslist = ExpertDBManager.getInstance(
                                SendSmsResultActivity.this)
                                .querySendResultList("1");
                        showStatus = 1;
                        successExpertAdapter.clear();
                        successExpertAdapter.addAll(sendSuccesslist);
                        successExpertAdapter.notifyDataSetChanged();
                        listViewSendSms.setAdapter(successExpertAdapter);
                        textViewSendNum.setText("发送成功人数为：" + sendSuccesslist.size());
                        break;
                    case R.id.rb_accept_false:
                        getTitleTextViewRight().setVisibility(View.VISIBLE);
                        acceptFalselist = ExpertDBManager.getInstance(
                                SendSmsResultActivity.this)
                                .querySendResultList("2");
                        showStatus = 2;
                        sendReceiveFailExpertAdapter.clear();
                        sendReceiveFailExpertAdapter.addAll(acceptFalselist);
                        sendReceiveFailExpertAdapter.notifyDataSetChanged();
                        listViewSendSms.setAdapter(sendReceiveFailExpertAdapter);
                        textViewSendNum.setText("接收失败人数为：" + acceptFalselist.size());



//                        getTitleTextViewRight().setVisibility(View.GONE);   //发送按钮隐藏
//                        acceptFalselist = ExpertDBManager.getInstance(
//                                SendSmsResultActivity.this)
//                                .querySendResultList("2");
//                        showStatus = 2;
//                        successExpertAdapter.clear();
//                        successExpertAdapter.addAll(acceptFalselist);
//                        successExpertAdapter.notifyDataSetChanged();
//                        listViewSendSms.setAdapter(successExpertAdapter);  //接收失败的ListView绑定数据
//                        textViewSendNum.setText("接收失败人数为：" + acceptFalselist.size());
                        break;
                    case R.id.rb_send_false:
                        getTitleTextViewRight().setVisibility(View.VISIBLE);
                        sendFalselist = ExpertDBManager.getInstance(
                                SendSmsResultActivity.this)
                                .querySendResultList("3");
                        showStatus = 3;
                        sendExpertAdapter.clear();
                        sendExpertAdapter.addAll(sendFalselist);
                        sendExpertAdapter.notifyDataSetChanged();
                        listViewSendSms.setAdapter(sendExpertAdapter);
                        textViewSendNum.setText("发送失败人数为：" + sendFalselist.size());
                        break;
                    case R.id.rb_not_send:
                        getTitleTextViewRight().setVisibility(View.VISIBLE);
                        showStatus = 0;
                        notSendlist = ExpertDBManager.getInstance(
                                SendSmsResultActivity.this).queryNoSendList();
                        notSendlist.addAll(ExpertDBManager.getInstance(
                                SendSmsResultActivity.this)
                                .querySendResultList("0"));
                        sendExpertAdapter.clear();
                        sendExpertAdapter.addAll(notSendlist);
                        sendExpertAdapter.notifyDataSetChanged();
                        listViewSendSms.setAdapter(sendExpertAdapter);
                        textViewSendNum.setText("未发送人数为：" + notSendlist.size());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {      //重新启动活动时调用
        super.onStart();
        if(showStatus==1){
            getTitleTextViewRight().setVisibility(View.GONE);
            sendSuccesslist = ExpertDBManager.getInstance(
                    SendSmsResultActivity.this)
                    .querySendResultList("1");
            successExpertAdapter.clear();
            successExpertAdapter.addAll(sendSuccesslist);
            successExpertAdapter.notifyDataSetChanged();
            listViewSendSms.setAdapter(successExpertAdapter);
            textViewSendNum.setText("发送成功人数为：" + sendSuccesslist.size());
        }

    }
}
