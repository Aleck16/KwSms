package cn.edu.hebut.iscs.kwsms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.hebut.iscs.kwsms.adapter.ReplyAdapter;
import cn.edu.hebut.iscs.kwsms.adapter.SendExpertAdapter;
import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.entity.ReplyStateInfo;
import cn.edu.hebut.iscs.kwsms.entity.SmsInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.service.SmsService;
import cn.edu.hebut.iscs.kwsms.util.ConstantValue;
import cn.edu.hebut.iscs.kwsms.util.PrefUtils;
import cn.edu.hebut.iscs.kwsms.util.SmsUtil;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import cn.edu.hebut.iscs.kwsms.view.MyBasicListDialog;
import cn.edu.hebut.iscs.kwsms.view.MyDialog;
import cn.edu.hebut.iscs.kwsms.view.MyEditTextDialog;
import cn.edu.hebut.iscs.kwsms.view.MyProgressDialog;
import cn.edu.hebut.iscs.kwsms.view.YesNoDailog;

/**
 * Created by lixueyang on 16-8-22.
 */
public class ShowReplySmsActivity extends BaseTitleActivity {
    @BindView(R.id.textView_num)
    TextView textViewNum;
    @BindView(R.id.listView_reply_sms)
    ListView listViewReplySms;
    @BindView(R.id.listView_no_reply_sms)
    ListView listViewNoReplySms;
    @BindView(R.id.rb_reply_unknown)
    RadioButton rbReplyUnknown;
    @BindView(R.id.rb_no_reply)
    RadioButton rbNoReply;
    @BindView(R.id.rb_reply_other)
    RadioButton rbReplyOther;
    @BindView(R.id.rb_reply_yes)
    RadioButton rbReplyYes;
    @BindView(R.id.rb_reply_no)
    RadioButton rbReplyNo;
    @BindView(R.id.radio_group_reply)
    RadioGroup radioGroupReply;


    private List<SmsInfo> infos;
    private List<ExpertInfo> noReplylist;
    private List<ReplyStateInfo> replyUnknownlist, replyOtherlist, replyYlist,
            replyNlist, allReplyList;
    // 用于显示回复短信
    private ReplyAdapter replyAdapter;
    // 用于显示未回复专家的专家信息
    private SendExpertAdapter sendExpertAdapter;

    // 要发送的专家信息
    private List<ExpertInfo> list;
    // 要发送的专家信息
    private List<ExpertInfo> newlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSetContentView(R.layout.activity_show_reply_sms);
        ButterKnife.bind(this);
        initUIHeader();
        initUI();
        querySms();
        initRadioGroup();
    }

    /*
     * 初始化标题
	 */
    private void initUIHeader() {
        setTitleBarText("回复情况");
        getTitleTextViewRight().setText("发送");
        getTitleTextViewRight().setVisibility(View.GONE);
        // 给未回复的专家发送短信
        getTitleTextViewRight().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final MyDialog dialog = new MyDialog(ShowReplySmsActivity.this,
                        R.style.YesNoDialog,
                        new MyDialog.OnCustomDialogListener() {

                            @Override
                            public void back(int type) {
                                switch (type) {
                                    case 0:
                                        break;
                                    case 1:
                                        //发送之前判断一下，当前是否正在发送短信或者正在回复短息，以防止应用停止运行死机状态
                                        if(PrefUtils.getBoolean(getApplicationContext(), ConstantValue.AUTO_SEND_SUCCESS,false)&&PrefUtils.getBoolean(getApplicationContext(),ConstantValue.SEND_SUCCESS,false)){
                                            sendSMSAsyncTask();     //调用发送短信事件
                                        }else if(PrefUtils.getBoolean(getApplicationContext(),ConstantValue.SEND_SUCCESS,false)){
                                            ToastUtil.showToast(ShowReplySmsActivity.this,"自动回复进行中...请等待发送完后再继续发送！");
                                        }else{
                                            ToastUtil.showToast(ShowReplySmsActivity.this,"短信正在发送中，请等待发送完后再继续发送！");
                                        }
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
     * 初始化各种不同回复短信的情况，以listview显示
     */
    public void initUI() {

        noReplylist = new ArrayList<ExpertInfo>();
        list = new ArrayList<ExpertInfo>();

        allReplyList = new ArrayList<ReplyStateInfo>();
        replyUnknownlist = new ArrayList<ReplyStateInfo>();
        replyOtherlist = new ArrayList<ReplyStateInfo>();
        replyYlist = new ArrayList<ReplyStateInfo>();
        replyNlist = new ArrayList<ReplyStateInfo>();

        replyAdapter = new ReplyAdapter(ShowReplySmsActivity.this);
        listViewReplySms.setAdapter(replyAdapter);
        listViewReplySms.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                switch (replyAdapter.getItem(arg2).getIsTelValid()) {

                    case "1":       //回复为有效
                        switch (replyAdapter.getItem(arg2).getYesNoOther()) {
                            case "1":       //回复为Yes
                                //将状态码设置为需要自动回复状态：AUTO_REPLY_NUM=2,表示等待自动回复用户名和密码


                                final YesNoDailog dialogY = new YesNoDailog(
                                        ShowReplySmsActivity.this, R.style.YesNoDialog,
                                        new YesNoDailog.OnCustomDialogListener() {

                                            @Override
                                            public void back(int type) {
                                                switch (type) {
                                                    case 0:
                                                        break;
                                                    case 1:     //确认发送用户名和密码，即将AUTO_REPLY_NUM设置为2，等待自动回复

                                                        /**
                                                         * 修改：将确认为回复Y，之前执行自动回复短信
                                                         */

                                                        String address=replyAdapter.getItem(arg2).getTel();     //库里面调出来的11位号码

                                                        //更新数据库，用户回复为Y
//                                                        int flag = ExpertDBManager.getInstance(ShowReplySmsActivity.this).updateReplyNum(address,1);   //回复内容状态码设置为1：表示回复为Y

                                                        //更新状态码为等待发送自动回复的用户名和密码
                                                        int flag = ExpertDBManager.getInstance(ShowReplySmsActivity.this).updateAutoReplyNum(address,2);   //标记回复了y，等待自动回复用户名和密码,状态码为2










//                                                        //确认为Y修改为发送自动回复内容
//                                                        String address=replyAdapter.getItem(arg2).getTel();     //库里面调出来的11位号码
//                                                        Intent serviceIntent = new Intent(getApplicationContext(),AutoReplyService.class);
//                                                        serviceIntent.putExtra("address",address);      //address类型：手机号：+8615032763060
//                                                        String autoReplyContent=ExpertDBManager.getInstance(getApplicationContext()).getAutoReplyContent(address);  //手机号：15032763060
//                                                        if(!autoReplyContent.isEmpty()){        //如果自动回复内容不为空
//                                                            serviceIntent.putExtra("replaycontent",autoReplyContent);       //回复的内容需要从数据库里面调用
//                                                            getApplicationContext().startService(serviceIntent);
//                                                        }
////                                                        ExpertDBManager.getInstance(
////                                                                ShowReplySmsActivity.this)
////                                                                .updateReplyYesNo(
////                                                                        "1",
////                                                                        replyAdapter.getItem(
////                                                                                arg2)
////                                                                                .getTel(),
////                                                                        null);
////                                                        initAdapter();
////                                                        replyAdapter.clear();
////                                                        replyAdapter.addAll(replyOtherlist);
////                                                        replyAdapter.notifyDataSetChanged();
                                                        break;
                                                    case 2:         //确认回复类型为No

                                                        address=replyAdapter.getItem(arg2).getTel();     //库里面调出来的11位号码

                                                        //更新数据库，用户回复为Y
                                                        flag = ExpertDBManager.getInstance(ShowReplySmsActivity.this).updateReplyNum(address,2);   //回复内容状态码设置为2：表示回复为N


//                                                        ExpertDBManager.getInstance(
//                                                                ShowReplySmsActivity.this)
//                                                                .updateReplyYesNo(
//                                                                        "2",
//                                                                        replyAdapter.getItem(
//                                                                                arg2)
//                                                                                .getTel(),
//                                                                        null);
//                                                        initAdapter();
//                                                        replyAdapter.clear();
//                                                        replyAdapter.addAll(replyOtherlist);
//                                                        replyAdapter.notifyDataSetChanged();
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }, "Y表示重新发送用户名和密码，N表示确认为回复N", replyAdapter.getItem(arg2)
                                        .getReplyContent());
                                dialogY.show();























                                break;
                            case "2":       //回复为no
                                break;
                            case "3":       //回复为其他
                                final YesNoDailog dialog = new YesNoDailog(
                                        ShowReplySmsActivity.this, R.style.YesNoDialog,
                                        new YesNoDailog.OnCustomDialogListener() {

                                            @Override
                                            public void back(int type) {
                                                switch (type) {
                                                    case 0:
                                                        break;
                                                    case 1:     //回复其他里面，确认回复类型为YES

                                                        /**
                                                         * 修改：将确认为回复Y，之前执行自动回复短信
                                                         */

                                                        String address=replyAdapter.getItem(arg2).getTel();     //库里面调出来的11位号码

                                                        //更新数据库，用户回复为Y
                                                        int flag = ExpertDBManager.getInstance(ShowReplySmsActivity.this).updateReplyNum(address,1);   //回复内容状态码设置为1：表示回复为Y

                                                        //更新状态码为等待发送自动回复的用户名和密码
                                                        flag = ExpertDBManager.getInstance(ShowReplySmsActivity.this).updateAutoReplyNum(address,2);   //标记回复了y，等待自动回复用户名和密码,状态码为2

                                                        //更新列表
                                                        initAdapter();
                                                        replyAdapter.clear();
                                                        replyAdapter.addAll(replyOtherlist);
                                                        replyAdapter.notifyDataSetChanged();


//                                                        /**
//                                                         * 修改：将确认为回复Y，之前执行自动回复短信
//                                                         */
//                                                        //确认为Y修改为发送自动回复内容
//                                                        String address=replyAdapter.getItem(arg2).getTel();     //库里面调出来的11位号码
//                                                        Intent serviceIntent = new Intent(getApplicationContext(),AutoReplyService.class);
//                                                        serviceIntent.putExtra("address",address);      //address类型：手机号：+8615032763060
//                                                        String autoReplyContent=ExpertDBManager.getInstance(getApplicationContext()).getAutoReplyContent(address);  //手机号：15032763060
//                                                        if(!autoReplyContent.isEmpty()){        //如果自动回复内容不为空
//                                                            serviceIntent.putExtra("replaycontent",autoReplyContent);       //回复的内容需要从数据库里面调用
//                                                            getApplicationContext().startService(serviceIntent);
//                                                        }
//                                                        ExpertDBManager.getInstance(
//                                                                ShowReplySmsActivity.this)
//                                                                .updateReplyYesNo(
//                                                                        "1",
//                                                                        replyAdapter.getItem(
//                                                                                arg2)
//                                                                                .getTel(),
//                                                                        null);
//                                                        initAdapter();
//                                                        replyAdapter.clear();
//                                                        replyAdapter.addAll(replyOtherlist);
//                                                        replyAdapter.notifyDataSetChanged();
                                                        break;
                                                    case 2:         //确认回复类型为No
                                                        //修改状态码为2
                                                        ExpertDBManager.getInstance(
                                                                ShowReplySmsActivity.this)
                                                                .updateReplyYesNo(
                                                                        "2",
                                                                        replyAdapter.getItem(
                                                                                arg2)
                                                                                .getTel(),
                                                                        null);
                                                        //更新列表
                                                        initAdapter();
                                                        replyAdapter.clear();
                                                        replyAdapter.addAll(replyOtherlist);
                                                        replyAdapter.notifyDataSetChanged();
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }, "请确认回复类型", replyAdapter.getItem(arg2)
                                        .getReplyContent());
                                dialog.show();

                                break;
                        }
                        break;
                    case "2":           //回复为无效
                        final MyEditTextDialog dialog = new MyEditTextDialog(
                                ShowReplySmsActivity.this, R.style.YesNoDialog,
                                new MyEditTextDialog.OnCustomDialogListener() {

                                    @Override
                                    public void back(int type, EditText editText) {
                                        if (editText.getText().toString().length() > 0) {
                                            final List<ExpertInfo> expertInfoList = ExpertDBManager
                                                    .getInstance(
                                                            ShowReplySmsActivity.this)
                                                    .loadExpertInfo(
                                                            editText.getText()
                                                                    .toString());
                                            final List<String> showExpert = new ArrayList<>();
                                            String title;
                                            if (expertInfoList.size() > 0) {
                                                for (ExpertInfo expertInfo : expertInfoList) {
                                                    showExpert.add("姓名："
                                                            + expertInfo.getName()
                                                            + "  ,编号："
                                                            + expertInfo
                                                            .getExpertCode());
                                                }
                                                showExpert.add("取消");
                                                title = "请选择专家";
                                            } else {
                                                showExpert.add("取消");
                                                title = "没有该专家，请重新输入！";
                                            }
                                            switch (type) {
                                                case 0:
                                                    break;
                                                case 1:
                                                    MyBasicListDialog dialog = new MyBasicListDialog(
                                                            ShowReplySmsActivity.this,
                                                            R.style.YesNoDialog,
                                                            new MyBasicListDialog.OnCustomDialogListener() {

                                                                @Override
                                                                public void back(
                                                                        int type) {
                                                                    ExpertDBManager
                                                                            .getInstance(
                                                                                    ShowReplySmsActivity.this)
                                                                            .updateReplyYesNo(
                                                                                    "1",
                                                                                    replyAdapter.getItem(
                                                                                            arg2)
                                                                                            .getTel(),
                                                                                    expertInfoList
                                                                                            .get(type));
                                                                    initAdapter();
                                                                    replyAdapter.clear();
                                                                    replyAdapter.addAll(replyUnknownlist);
                                                                    replyAdapter.notifyDataSetChanged();

                                                                }
                                                            }, showExpert, title);
                                                    dialog.show();

                                                    break;
                                                case 2:
                                                    MyBasicListDialog dialogtwo = new MyBasicListDialog(
                                                            ShowReplySmsActivity.this,
                                                            R.style.YesNoDialog,
                                                            new MyBasicListDialog.OnCustomDialogListener() {

                                                                @Override
                                                                public void back(
                                                                        int type) {
                                                                    ExpertDBManager
                                                                            .getInstance(
                                                                                    ShowReplySmsActivity.this)
                                                                            .updateReplyYesNo(
                                                                                    "2",
                                                                                    replyAdapter.getItem(
                                                                                            arg2)
                                                                                            .getTel(),
                                                                                    expertInfoList
                                                                                            .get(type));
                                                                    initAdapter();
                                                                    replyAdapter.clear();
                                                                    replyAdapter.addAll(replyUnknownlist);
                                                                    replyAdapter.notifyDataSetChanged();
                                                                }
                                                            }, showExpert, title);
                                                    dialogtwo.show();

                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                }, replyAdapter.getItem(arg2).getReplyContent(), replyAdapter
                                .getItem(arg2).getTel());
                        dialog.show();
                        break;
                }
            }
        });
        sendExpertAdapter = new SendExpertAdapter(ShowReplySmsActivity.this);
        listViewNoReplySms.setAdapter(sendExpertAdapter);

        //未回复列表添加点击事件
        listViewNoReplySms.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                final YesNoDailog dialog = new YesNoDailog(
                        ShowReplySmsActivity.this, R.style.YesNoDialog,
                        new YesNoDailog.OnCustomDialogListener() {

                            @Override
                            public void back(int type) {
                                switch (type) {
                                    case 0:
                                        break;
                                    case 1:     //将该专家确认为回复Y,并发送用户名和密码

                                        String address=sendExpertAdapter
                                                .getItem(arg2).getTel();
                                        //将状态码设置为1，表示回复为Y
                                        ExpertDBManager.getInstance(
                                                ShowReplySmsActivity.this).updateReplyNum(address,1);

                                        //更新状态码为等待发送自动回复的用户名和密码
                                        int flag = ExpertDBManager.getInstance(ShowReplySmsActivity.this).updateAutoReplyNum(address,2);   //标记回复了y，等待自动回复用户名和密码,状态码为2


//                                        ReplyStateInfo replyStateInfo = new ReplyStateInfo();
//                                        replyStateInfo.setTel(sendExpertAdapter
//                                                .getItem(arg2).getTel());
//                                        replyStateInfo.setReplyTime(String.valueOf(System
//                                                .currentTimeMillis()));
//                                        replyStateInfo.setExpertCode(sendExpertAdapter
//                                                .getItem(arg2).getExpertCode());
//                                        replyStateInfo.setExpertName(sendExpertAdapter
//                                                .getItem(arg2).getName());
//                                        replyStateInfo.setIsTelValid("1");
//                                        replyStateInfo.setYesNoOther("1");
//
//                                        ExpertDBManager.getInstance(
//                                                ShowReplySmsActivity.this)
//                                                .saveReplyStateInfo(
//                                                        ShowReplySmsActivity.this,
//                                                        replyStateInfo);

                                        //更新列表显示数据
                                        initAdapter();
                                        noReplylist = ExpertDBManager.getInstance(
                                                ShowReplySmsActivity.this)
                                                .queryNoReplyList();
                                        sendExpertAdapter.clear();
                                        sendExpertAdapter.addAll(noReplylist);
                                        sendExpertAdapter.notifyDataSetChanged();
                                        textViewNum.setText("未回复的人数为："
                                                + noReplylist.size());
                                        break;
                                    case 2:

                                        address=sendExpertAdapter
                                                .getItem(arg2).getTel();
                                        //将状态码设置为2，表示回复为N
                                        ExpertDBManager.getInstance(
                                                ShowReplySmsActivity.this).updateReplyNum(address,2);


//                                        ReplyStateInfo replyStateInfo1 = new ReplyStateInfo();
//                                        replyStateInfo1.setTel(sendExpertAdapter
//                                                .getItem(arg2).getTel());
//                                        replyStateInfo1.setReplyTime(DateTimeUtil
//                                                .longTimeToStrDate(
//                                                        System.currentTimeMillis(),
//                                                        DateTimeUtil.format_1));
//                                        replyStateInfo1.setReplyTime(String.valueOf(System
//                                                .currentTimeMillis()));
//                                        replyStateInfo1.setExpertCode(sendExpertAdapter
//                                                .getItem(arg2).getExpertCode());
//                                        replyStateInfo1
//                                                .setExpertName(sendExpertAdapter
//                                                        .getItem(arg2).getName());
//                                        replyStateInfo1.setIsTelValid("1");
//                                        replyStateInfo1.setYesNoOther("2");
//
//                                        ExpertDBManager.getInstance(
//                                                ShowReplySmsActivity.this)
//                                                .saveReplyStateInfo(
//                                                        ShowReplySmsActivity.this,
//                                                        replyStateInfo1);
                                        //更新列表显示数据
                                        initAdapter();
                                        noReplylist = ExpertDBManager.getInstance(
                                                ShowReplySmsActivity.this)
                                                .queryNoReplyList();
                                        sendExpertAdapter.clear();
                                        sendExpertAdapter.addAll(noReplylist);
                                        sendExpertAdapter.notifyDataSetChanged();
                                        textViewNum.setText("未回复的人数为："
                                                + noReplylist.size());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }, "请确认回复类型", sendExpertAdapter
                        .getItem(arg2).getName() + "已电话回复(选择Y：将确认为回复Y，且发送用户名和密码)");
                dialog.show();
            }
        });
    }

    /*
     * 发送短信
     */
    private void sendSMSAsyncTask() {

        list.clear();
        newlist = sendExpertAdapter.getList();;      //绑定接收失败的数据
        for(ExpertInfo expertInfo:newlist){
            list.add(expertInfo);
        }

        sendExpertAdapter.getStateList().clear();
        initAdapter();
        listViewNoReplySms.setAdapter(sendExpertAdapter);
        sendExpertAdapter.clear();
        sendExpertAdapter.addAll(noReplylist);
        sendExpertAdapter.notifyDataSetChanged();
        textViewNum.setText("未回复的人数为：" + noReplylist.size());


        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MyProgressDialog.startProgressDialog(ShowReplySmsActivity.this,
                        "正在发送短信，请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                if (list != null && list.size() > 0) {

                    // 跳转到发送短信服务service
                    Intent intent = new Intent(ShowReplySmsActivity.this,
                            SmsService.class);
                    intent.setAction("cn.edu.hebut.iscs.sms_service");
                    stopService(intent);
                    Bundle budle = new Bundle();
                    int i = 0;
                    for (ExpertInfo expertInfo : list) {
                        budle.putSerializable("expertInfolist" + i, expertInfo);
                        i++;
                    }
                    budle.putInt("listlenght", list.size());
                    intent.putExtras(budle);
                    startService(intent);
                    list.clear();
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
                    ToastUtil.showToast(ShowReplySmsActivity.this, "发送完成！");
                    list.clear();
                    sendExpertAdapter.getStateList().clear();
                    initAdapter();
                    listViewNoReplySms.setAdapter(sendExpertAdapter);
                    sendExpertAdapter.clear();
                    sendExpertAdapter.addAll(noReplylist);
                    sendExpertAdapter.notifyDataSetChanged();
                    textViewNum.setText("未回复的人数为：" + noReplylist.size());
                } else {
                    ToastUtil.showToast(ShowReplySmsActivity.this,
                            "数据库中没有数据，请再导入数据！");
                }
            }
        }.execute();
    }

    /**
     * 读取回复短信列表
     */
    public void querySms() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MyProgressDialog.startProgressDialog(ShowReplySmsActivity.this,
                        "正在读取回复的短信列表，请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                saveSms();      //将读取到的回复信息保存到回复表里面
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                MyProgressDialog.stopProgressDialog();
                if (result) {
                    ToastUtil.showToast(ShowReplySmsActivity.this, "读取完成！");
                    initAdapter();
                    replyAdapter.clear();
                    replyAdapter.addAll(replyUnknownlist);
                    replyAdapter.notifyDataSetChanged();
                    textViewNum.setText("无法匹配人数为：" + replyUnknownlist.size());
                } else {
                    ToastUtil.showToast(ShowReplySmsActivity.this,
                            "读取未完成！请重新读取！");
                    finish();
                }
            }

        }.execute();
    }

    public void saveSms() {
        SmsUtil smsUtil = new SmsUtil();
        //读取所有的接收到的短信信息
        infos = smsUtil.getSmsInfo(ShowReplySmsActivity.this, ExpertDBManager
                .getInstance(ShowReplySmsActivity.this).queryReplyLastTime());
        Log.d("infos","infos的大小"+infos.size());

        for (SmsInfo smsInfo : infos) {     //遍历每一个号码发来的短信
            //当前号码，在数据库里面是否已经存在，存在则返回次短信信息对象
            ReplyStateInfo theReplyStateInfo = ExpertDBManager.getInstance(
                    ShowReplySmsActivity.this).loadReplyStateInfo(
                    smsInfo.getPhoneNumber());
             //判断该电话用户是否已回复
            /**
             * 在这里修改：
             * 导入专家信息时，顺便将回复表里面已经导入对应的
             * EXPERT_CODE,NAME,TEL,IS_TEL_VALID即（专家编号，专家名字，电话号码，电话是否有效设置为1）这些字段
             * 如果当前回复的号码在专家库里面，则直接更新内容。否则：执行插入语句
             */


            //回复表里面有此电话号码：
            //则，更新REPLY_CONTENT,REPLY_TIME,YES_NO_OTHER(就是前三列和IS_TEL_VALID,和最后一列(AUTO_REPLY_NUM)不更新)
            if (theReplyStateInfo != null) {        //回复表里存在此号码

                String YesNoOther="0";
                //判断当前号码回复的内容
                switch (smsInfo.getSmsbody().trim()) {
                    // 回复y
                    case "Y":
                    case "y":
                    case "yes":
                    case "YES":
                    case "Yes":
                        YesNoOther="1";
                        break;
                    // 回复n
                    case "N":
                    case "n":
                    case "no":
                    case "NO":
                    case "No":
                        YesNoOther="2";
                        break;
                    // 回复其他
                    default:
                        YesNoOther="3";
                        break;
                }
                ExpertDBManager.getInstance(ShowReplySmsActivity.this)
                        .updateExpertReplyResult(smsInfo.getSmsbody(),
                                smsInfo.getDate(),YesNoOther, theReplyStateInfo);
            }else{
                //否则，回复表里没有此电话号码，则判断为非专家号码回复，则直接插入，
                //字段和值为：TEL,REPLY_CONTENT,REPLY_TIME,YES_NO_OTHER(0),IS_TEL_VALID(2)

                ReplyStateInfo replyStateInfo = new ReplyStateInfo();
                replyStateInfo.setTel(smsInfo.getPhoneNumber());
                replyStateInfo.setReplyTime(smsInfo.getDate());
                replyStateInfo.setReplyContent(smsInfo.getSmsbody());
                replyStateInfo.setIsTelValid("2");      //设置是否有效字段为2，表示无效回复
                replyStateInfo.setYesNoOther("0");      //设置回复内容属性为0：表示不是专家回复的号码，所以不在yer和No和other范围类，设置为0

                ExpertDBManager.getInstance(ShowReplySmsActivity.this).
                        saveReplyStateInfo(ShowReplySmsActivity.this,replyStateInfo);
            }





//            if (theReplyStateInfo == null) {        //回复表里面没有此电话号码，那就执行插入语句，将此信息插入到回复表里面
//                //将此电话号码与专家表里面的号码匹配，如果存在则返回此电话号码的专家信息expertInfo
//                ExpertInfo expertInfo = ExpertDBManager.getInstance(
//                        ShowReplySmsActivity.this).matchExpertInfo(
//                        smsInfo.getPhoneNumber());
//
//                ReplyStateInfo replyStateInfo = new ReplyStateInfo();
//                replyStateInfo.setTel(smsInfo.getPhoneNumber());
//                replyStateInfo.setReplyTime(smsInfo.getDate());
//                replyStateInfo.setReplyContent(smsInfo.getSmsbody());
//                // 判断电话是否为专家电话
//                if (expertInfo == null) {       //当前号码不为专家表里的号码
//                    // 无法匹配的
//                    replyStateInfo.setIsTelValid("2");      //设置是否有效字段为2，表示无效回复
//                    replyStateInfo.setYesNoOther("0");      //设置回复内容属性为0：表示不是专家回复的号码，所以不在yer和No和other范围类，设置为0
//
//                } else {                //当前号码是专家表里的号码
//                    replyStateInfo.setExpertCode(expertInfo.getExpertCode());
//                    replyStateInfo.setExpertName(expertInfo.getName());
//                    replyStateInfo.setIsTelValid("1");
//                    //判断当前号码回复的内容
//                    switch (smsInfo.getSmsbody().trim()) {
//                        // 回复y
//                        case "Y":
//                            replyStateInfo.setYesNoOther("1");
//                            break;
//                        case "y":
//                            replyStateInfo.setYesNoOther("1");
//                            break;
//                        case "yes":
//                            replyStateInfo.setYesNoOther("1");
//                            break;
//                        case "YES":
//                            replyStateInfo.setYesNoOther("1");
//                            break;
//                        case "Yes":
//                            replyStateInfo.setYesNoOther("1");
//                            break;
//                        // 回复n
//                        case "N":
//                            replyStateInfo.setYesNoOther("2");
//                            break;
//                        case "n":
//                            replyStateInfo.setYesNoOther("2");
//                            break;
//                        case "no":
//                            replyStateInfo.setYesNoOther("2");
//                            break;
//                        case "NO":
//                            replyStateInfo.setYesNoOther("2");
//                            break;
//                        case "No":
//                            replyStateInfo.setYesNoOther("2");
//                            break;
//                        // 回复其他
//                        default:
//                            replyStateInfo.setYesNoOther("3");
//                            break;
//                    }
//                }
//                ExpertDBManager.getInstance(ShowReplySmsActivity.this)
//                        .saveReplyStateInfo(ShowReplySmsActivity.this,
//                                replyStateInfo);
//            } else {
//                ExpertDBManager.getInstance(ShowReplySmsActivity.this)
//                        .updateMoreReplyResult(smsInfo.getSmsbody(),
//                                smsInfo.getDate(), theReplyStateInfo);
//            }


        }

    }

    public void initAdapter() {
        noReplylist.clear();
        replyUnknownlist.clear();
        replyYlist.clear();
        replyNlist.clear();
        replyOtherlist.clear();
        allReplyList = ExpertDBManager.getInstance(ShowReplySmsActivity.this)
                .loadReplyStateInfoList();
        for (ReplyStateInfo replyStateInfo : allReplyList) {
            switch (replyStateInfo.getYesNoOther()) {
                case "0":       //无法匹配的回复
                    replyUnknownlist.add(replyStateInfo);
                    break;
                case "1":       //回复Yes
                    replyYlist.add(replyStateInfo);
                    break;
                case "2":       //回复no
                    replyNlist.add(replyStateInfo);
                    break;
                case "3":       //回复其他
                    replyOtherlist.add(replyStateInfo);
                    break;
            }
        }
        noReplylist = ExpertDBManager.getInstance(ShowReplySmsActivity.this)
                .queryNoReplyList();
    }

    public void initRadioGroup() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_reply);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                switch (arg1) {
                    case R.id.rb_no_reply:
                        getTitleTextViewRight().setVisibility(View.VISIBLE);
                        listViewNoReplySms.setVisibility(View.VISIBLE);
                        listViewReplySms.setVisibility(View.GONE);
                        noReplylist = ExpertDBManager.getInstance(
                                ShowReplySmsActivity.this).queryNoReplyList();
                        listViewNoReplySms.setAdapter(sendExpertAdapter);
                        sendExpertAdapter.clear();
                        sendExpertAdapter.addAll(noReplylist);
                        sendExpertAdapter.notifyDataSetChanged();
                        textViewNum.setText("未回复的人数为：" + noReplylist.size());
                        break;
                    case R.id.rb_reply_unknown:
                        initAdapter();
                        getTitleTextViewRight().setVisibility(View.GONE);
                        listViewReplySms.setVisibility(View.VISIBLE);
                        listViewNoReplySms.setVisibility(View.GONE);
                        listViewReplySms.setAdapter(replyAdapter);
                        replyAdapter.clear();
                        replyAdapter.addAll(replyUnknownlist);
                        replyAdapter.notifyDataSetChanged();
                        textViewNum.setText("无法匹配人数为：" + replyUnknownlist.size());
                        break;
                    case R.id.rb_reply_other:
                        initAdapter();
                        getTitleTextViewRight().setVisibility(View.GONE);
                        listViewReplySms.setVisibility(View.VISIBLE);
                        listViewNoReplySms.setVisibility(View.GONE);
                        listViewReplySms.setAdapter(replyAdapter);
                        replyAdapter.clear();
                        replyAdapter.addAll(replyOtherlist);
                        replyAdapter.notifyDataSetChanged();
                        textViewNum.setText("其他回复人数为：" + replyOtherlist.size());
                        break;
                    case R.id.rb_reply_yes:
                        initAdapter();
                        getTitleTextViewRight().setVisibility(View.GONE);
                        listViewReplySms.setVisibility(View.VISIBLE);
                        listViewNoReplySms.setVisibility(View.GONE);
                        listViewReplySms.setAdapter(replyAdapter);
                        replyAdapter.clear();
                        replyAdapter.addAll(replyYlist);
                        replyAdapter.notifyDataSetChanged();
                        textViewNum.setText("回复Y人数为：" + replyYlist.size());
                        break;
                    case R.id.rb_reply_no:
                        initAdapter();
                        getTitleTextViewRight().setVisibility(View.GONE);
                        listViewReplySms.setVisibility(View.VISIBLE);
                        listViewNoReplySms.setVisibility(View.GONE);
                        listViewReplySms.setAdapter(replyAdapter);
                        replyAdapter.clear();
                        replyAdapter.addAll(replyNlist);
                        replyAdapter.notifyDataSetChanged();
                        textViewNum.setText("回复N人数为：" + replyNlist.size());
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
