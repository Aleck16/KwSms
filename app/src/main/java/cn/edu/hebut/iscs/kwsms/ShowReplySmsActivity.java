package cn.edu.hebut.iscs.kwsms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import cn.edu.hebut.iscs.kwsms.util.DateTimeUtil;
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

                    case "1":
                        switch (replyAdapter.getItem(arg2).getYesNoOther()) {
                            case "1":
                                break;
                            case "2":
                                break;
                            case "3":
                                final YesNoDailog dialog = new YesNoDailog(
                                        ShowReplySmsActivity.this, R.style.YesNoDialog,
                                        new YesNoDailog.OnCustomDialogListener() {

                                            @Override
                                            public void back(int type) {
                                                switch (type) {
                                                    case 0:
                                                        break;
                                                    case 1:
                                                        ExpertDBManager.getInstance(
                                                                ShowReplySmsActivity.this)
                                                                .updateReplyYesNo(
                                                                        "1",
                                                                        replyAdapter.getItem(
                                                                                arg2)
                                                                                .getTel(),
                                                                        null);
                                                        initAdapter();
                                                        replyAdapter.clear();
                                                        replyAdapter.addAll(replyOtherlist);
                                                        replyAdapter.notifyDataSetChanged();
                                                        break;
                                                    case 2:
                                                        ExpertDBManager.getInstance(
                                                                ShowReplySmsActivity.this)
                                                                .updateReplyYesNo(
                                                                        "2",
                                                                        replyAdapter.getItem(
                                                                                arg2)
                                                                                .getTel(),
                                                                        null);
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
                    case "2":
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
                                    case 1:

                                        ReplyStateInfo replyStateInfo = new ReplyStateInfo();
                                        replyStateInfo.setTel(sendExpertAdapter
                                                .getItem(arg2).getTel());
                                        replyStateInfo.setReplyTime(String.valueOf(System
                                                .currentTimeMillis()));
                                        replyStateInfo.setExpertCode(sendExpertAdapter
                                                .getItem(arg2).getExpertCode());
                                        replyStateInfo.setExpertName(sendExpertAdapter
                                                .getItem(arg2).getName());
                                        replyStateInfo.setIsTelValid("1");
                                        replyStateInfo.setYesNoOther("1");

                                        ExpertDBManager.getInstance(
                                                ShowReplySmsActivity.this)
                                                .saveReplyStateInfo(
                                                        ShowReplySmsActivity.this,
                                                        replyStateInfo);

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
                                        ReplyStateInfo replyStateInfo1 = new ReplyStateInfo();
                                        replyStateInfo1.setTel(sendExpertAdapter
                                                .getItem(arg2).getTel());
                                        replyStateInfo1.setReplyTime(DateTimeUtil
                                                .longTimeToStrDate(
                                                        System.currentTimeMillis(),
                                                        DateTimeUtil.format_1));
                                        replyStateInfo1.setReplyTime(String.valueOf(System
                                                .currentTimeMillis()));
                                        replyStateInfo1.setExpertCode(sendExpertAdapter
                                                .getItem(arg2).getExpertCode());
                                        replyStateInfo1
                                                .setExpertName(sendExpertAdapter
                                                        .getItem(arg2).getName());
                                        replyStateInfo1.setIsTelValid("1");
                                        replyStateInfo1.setYesNoOther("2");

                                        ExpertDBManager.getInstance(
                                                ShowReplySmsActivity.this)
                                                .saveReplyStateInfo(
                                                        ShowReplySmsActivity.this,
                                                        replyStateInfo1);
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
                        .getItem(arg2).getName() + "已电话回复");
                dialog.show();
            }
        });
    }

    /*
     * 发送短信
     */
    private void sendSMSAsyncTask() {
        list=sendExpertAdapter.getList();
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
                saveSms();
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
        infos = smsUtil.getSmsInfo(ShowReplySmsActivity.this, ExpertDBManager
                .getInstance(ShowReplySmsActivity.this).queryReplyLastTime());

        for (SmsInfo smsInfo : infos) {
            ReplyStateInfo theReplyStateInfo = ExpertDBManager.getInstance(
                    ShowReplySmsActivity.this).loadReplyStateInfo(
                    smsInfo.getPhoneNumber());
            // 判断该电话用户是否已回复
            if (theReplyStateInfo == null) {
                ExpertInfo expertInfo = ExpertDBManager.getInstance(
                        ShowReplySmsActivity.this).matchExpertInfo(
                        smsInfo.getPhoneNumber());

                ReplyStateInfo replyStateInfo = new ReplyStateInfo();
                replyStateInfo.setTel(smsInfo.getPhoneNumber());
                replyStateInfo.setReplyTime(smsInfo.getDate());
                replyStateInfo.setReplyContent(smsInfo.getSmsbody());
                // 判断电话是否位专家电话
                if (expertInfo == null) {
                    // 无法匹配的
                    replyStateInfo.setIsTelValid("2");
                    replyStateInfo.setYesNoOther("0");

                } else {
                    replyStateInfo.setExpertCode(expertInfo.getExpertCode());
                    replyStateInfo.setExpertName(expertInfo.getName());
                    replyStateInfo.setIsTelValid("1");
                    switch (smsInfo.getSmsbody().trim()) {
                        // 回复y
                        case "Y":
                            replyStateInfo.setYesNoOther("1");
                            break;
                        case "y":
                            replyStateInfo.setYesNoOther("1");
                            break;
                        case "yes":
                            replyStateInfo.setYesNoOther("1");
                            break;
                        case "YES":
                            replyStateInfo.setYesNoOther("1");
                            break;
                        case "Yes":
                            replyStateInfo.setYesNoOther("1");
                            break;
                        // 回复n
                        case "N":
                            replyStateInfo.setYesNoOther("2");
                            break;
                        case "n":
                            replyStateInfo.setYesNoOther("2");
                            break;
                        case "no":
                            replyStateInfo.setYesNoOther("2");
                            break;
                        case "NO":
                            replyStateInfo.setYesNoOther("2");
                            break;
                        case "No":
                            replyStateInfo.setYesNoOther("2");
                            break;
                        // 回复其他
                        default:
                            replyStateInfo.setYesNoOther("3");
                            break;
                    }
                }
                ExpertDBManager.getInstance(ShowReplySmsActivity.this)
                        .saveReplyStateInfo(ShowReplySmsActivity.this,
                                replyStateInfo);
            } else {
                ExpertDBManager.getInstance(ShowReplySmsActivity.this)
                        .updateMoreReplyResult(smsInfo.getSmsbody(),
                                smsInfo.getDate(), theReplyStateInfo);
            }
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
                case "0":
                    replyUnknownlist.add(replyStateInfo);
                    break;
                case "1":
                    replyYlist.add(replyStateInfo);
                    break;
                case "2":
                    replyNlist.add(replyStateInfo);
                    break;
                case "3":
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
