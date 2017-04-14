package cn.edu.hebut.iscs.kwsms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hebut.iscs.kwsms.db.DBManager;
import cn.edu.hebut.iscs.kwsms.util.ConstantValue;
import cn.edu.hebut.iscs.kwsms.util.PrefUtils;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import cn.edu.hebut.iscs.kwsms.view.MyDialog;

/**
 * Created by lixueyang on 16-8-22.
 */
public class DbOperationActivity extends BaseTitleActivity {
    @BindView(R.id.db_delete)
    Button dbDelete;

    private Button btn_default;
    private Button btn_defaultReply;
    private Button btn_defaultSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSetContentView(R.layout.activity_db_opration);
        ButterKnife.bind(this);
        setTitleBarText("数据库操作");

        btn_default=(Button)findViewById(R.id.btn_default);
        btn_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空正在自动回复状态码和正在发送状态码
                PrefUtils.setBoolean(DbOperationActivity.this, ConstantValue.AUTO_SEND_SUCCESS,true);    //标记自动回复正在进行
                PrefUtils.setBoolean(DbOperationActivity.this, ConstantValue.SEND_SUCCESS,true);    //标记正在发送中正在进行
            }
        });

        btn_defaultReply=(Button)findViewById(R.id.btn_defaultReply);
        btn_defaultReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空正在自动回复状态码和正在发送状态码
                PrefUtils.setBoolean(DbOperationActivity.this, ConstantValue.AUTO_SEND_SUCCESS,true);    //标记自动回复正在进行
//                PrefUtils.setBoolean(DbOperationActivity.this, ConstantValue.SEND_SUCCESS,true);    //标记正在发送中正在进行
            }
        });

        btn_defaultSend=(Button)findViewById(R.id.btn_defaultSend);
        btn_defaultSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空正在自动回复状态码和正在发送状态码
//                PrefUtils.setBoolean(DbOperationActivity.this, ConstantValue.AUTO_SEND_SUCCESS,true);    //标记自动回复正在进行
                PrefUtils.setBoolean(DbOperationActivity.this, ConstantValue.SEND_SUCCESS,true);    //标记正在发送中正在进行
            }
        });
    }

    @OnClick(R.id.db_delete)
    public void onClick() {

        final MyDialog dialog = new MyDialog(DbOperationActivity.this,
                R.style.YesNoDialog,
                new MyDialog.OnCustomDialogListener() {

                    @Override
                    public void back(int type) {
                        switch (type) {
                            case 0:
                                break;
                            case 1:
                                boolean result = DBManager.getInstance(
                                        DbOperationActivity.this,
                                        DBManager.DB_NAME).deleteDatabase();
                                if (result) {
                                    ToastUtil.showToast(
                                            DbOperationActivity.this,
                                            "已经将数据库清空！");
                                } else {
                                    ToastUtil.showToast(
                                            DbOperationActivity.this,
                                            "数据库已清空或者清空失败，请重新操作！");
                                }

                            default:
                                break;
                        }
                    }
                }, "温馨提示", "请问您是否要清空数据库？", "确定", "取消");
        dialog.show();

    }




}
