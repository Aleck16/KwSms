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
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import cn.edu.hebut.iscs.kwsms.view.MyDialog;
import cn.edu.hebut.iscs.kwsms.view.MyProgressDialog;

/**
 * Created by lixueyang on 16-8-17.
 */
public class SendSmsResultActivity extends BaseTitleActivity {
    @BindView(R.id.textView_send_num)
    TextView textViewSendNum;
    @BindView(R.id.listView_send_sms)
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
    private List<ExpertInfo> notSendlist, sendSuccesslist, sendFalselist,
            acceptFalselist;
    // 要发送的专家信息
    private List<ExpertInfo> list;
    private ExpertAdapter successExpertAdapter;
    private SendExpertAdapter sendExpertAdapter;
    // 短信发送状态，配合底部按钮使用
    public int showStatus = 0;

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

        sendSuccesslist = new ArrayList<ExpertInfo>();
        sendFalselist = new ArrayList<ExpertInfo>();
        acceptFalselist = new ArrayList<ExpertInfo>();
        notSendlist = new ArrayList<ExpertInfo>();
        list = new ArrayList<ExpertInfo>();

        initList();
        sendExpertAdapter = new SendExpertAdapter(SendSmsResultActivity.this);
        sendExpertAdapter.addAll(notSendlist);
        successExpertAdapter = new ExpertAdapter(SendSmsResultActivity.this);
        successExpertAdapter.addAll(sendSuccesslist);
        list = sendExpertAdapter.getList();
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
                                    case 1:
                                        sendSMSAsyncTask();
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
     * 初始化个状态显示的数据
     */
    public void initList() {

        sendSuccesslist = ExpertDBManager.getInstance(
                SendSmsResultActivity.this).querySendResultList("1");
        acceptFalselist = ExpertDBManager.getInstance(
                SendSmsResultActivity.this).querySendResultList("2");
        sendFalselist = ExpertDBManager.getInstance(SendSmsResultActivity.this)
                .querySendResultList("3");
        notSendlist = ExpertDBManager.getInstance(SendSmsResultActivity.this)
                .queryNoSendList();
        notSendlist.addAll(ExpertDBManager.getInstance(
                SendSmsResultActivity.this).querySendResultList("0"));
    }

    /*
     * 发送短信
     */
    private void sendSMSAsyncTask() {
        list = sendExpertAdapter.getList();
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
                    initList();
                    switch (showStatus) {
                        case 0:
                            sendExpertAdapter.addAll(notSendlist);
                            textViewSendNum.setText("未发送人数为：" + notSendlist.size());
                            break;
                        case 2:
                            sendExpertAdapter.addAll(acceptFalselist);
                            textViewSendNum
                                    .setText("接收失败人数为：" + acceptFalselist.size());
                            break;
                        case 3:
                            sendExpertAdapter.addAll(sendFalselist);
                            textViewSendNum.setText("发送失败人数为：" + sendFalselist.size());
                            break;
                    }
                    sendExpertAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.showToast(SendSmsResultActivity.this,
                            "未选中短信，请选择你要发送的短信！");
                }
            }
        }.execute();
        sendExpertAdapter.getStateList().clear();
        sendExpertAdapter.clear();
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
                        sendExpertAdapter.clear();
                        sendExpertAdapter.addAll(acceptFalselist);
                        sendExpertAdapter.notifyDataSetChanged();
                        listViewSendSms.setAdapter(sendExpertAdapter);
                        textViewSendNum.setText("接收失败人数为：" + acceptFalselist.size());
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
