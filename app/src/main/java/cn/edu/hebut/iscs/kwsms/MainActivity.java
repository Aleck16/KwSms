package cn.edu.hebut.iscs.kwsms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;

public class MainActivity extends Activity {

    @BindView(R.id.imports)
    Button importsButton;
    @BindView(R.id.send)
    Button sendButton;
    @BindView(R.id.reply)
    Button replyButton;
    @BindView(R.id.export)
    Button exportButton;
    @BindView(R.id.db_operation)
    Button dbOperationButton;

    private List<ExpertInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        list = ExpertDBManager.getInstance(MainActivity.this).loadExpertInfo();
    }

    @OnClick({R.id.imports, R.id.send, R.id.reply, R.id.export, R.id.db_operation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imports:
                Intent intent = new Intent(MainActivity.this,
                        QueryExcelActivity.class);
                startActivity(intent);
                break;
            case R.id.send:
                if (list != null && list.size() > 0) {
                    Intent intent1 = new Intent(MainActivity.this,
                            SendSmsResultActivity.class);
                    startActivity(intent1);
                } else {
                    ToastUtil.showToast(MainActivity.this, "专家数据未导入，请先导入数据！");
                }
                break;
            case R.id.reply:
                if (list != null && list.size() > 0) {
                    Intent intent2 = new Intent(MainActivity.this,
                            ShowReplySmsActivity.class);
                    startActivity(intent2);
                } else {
                    ToastUtil.showToast(MainActivity.this, "专家数据未导入，请先导入数据！");
                }

                break;
            case R.id.export:
                if (list != null && list.size() > 0) {
                    Intent intent3 = new Intent(MainActivity.this,
                            ExportExcelActivity.class);
                    startActivity(intent3);
                } else {
                    ToastUtil.showToast(MainActivity.this, "专家数据未导入，请先导入数据！");
                }
                break;
            case R.id.db_operation:
                Intent intent4 = new Intent(MainActivity.this,
                        DbOperationActivity.class);
                startActivity(intent4);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        list = ExpertDBManager.getInstance(MainActivity.this).loadExpertInfo();
    }
}
