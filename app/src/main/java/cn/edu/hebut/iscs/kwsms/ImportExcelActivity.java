package cn.edu.hebut.iscs.kwsms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.hebut.iscs.kwsms.adapter.ExpertAdapter;
import cn.edu.hebut.iscs.kwsms.db.DBManager;
import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import cn.edu.hebut.iscs.kwsms.view.MyProgressDialog;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by lixueyang on 16-8-16.
 */
public class ImportExcelActivity extends BaseTitleActivity {

    @BindView(R.id.rb_legitimateDataNum)
    RadioButton rbLegitimateDataNum;
    @BindView(R.id.rb_illegalDataNum)
    RadioButton rbIllegalDataNum;
    @BindView(R.id.rb_dbDataNum)
    RadioButton rbDbDataNum;
    @BindView(R.id.rg_expert)
    RadioGroup rgExpert;
    @BindView(R.id.listView_expert)
    ListView listViewExpert;
    @BindView(R.id.show_num)
    TextView showNum;


    private File file;
    private Sheet sheetStudent;
    private int rowNum;
    private ExpertAdapter expertAdapter;
    //    合法数据
    private List<ExpertInfo> expertInfoList = new ArrayList<ExpertInfo>();
    //    非法数据
    private List<ExpertInfo> expertIllegalInfoList = new ArrayList<ExpertInfo>();
    //    存储失败数据
    private List<ExpertInfo> notExpertInfoList = new ArrayList<ExpertInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSetContentView(R.layout.activity_show_excel);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        file = new File(path);
        String name = (String) file.getName().subSequence(0,
                file.getName().lastIndexOf("."));
        initUIHeader(name);
        initUI();
    }

    public void initUIHeader(String name) {
        if (DBManager.getInstance(ImportExcelActivity.this, DBManager.DB_NAME)
                .exits(DBManager.EXPERT_TABLE)) {
            setTitleBarText("存在");
        }
        setTitleBarText(name);
        getTitleTextViewRight().setText("导入");
        getTitleTextViewRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveExcel();
            }
        });
    }

    private void initUI() {

        expertAdapter = new ExpertAdapter(ImportExcelActivity.this);
        initExpertAdapter(file);
        listViewExpert.setAdapter(expertAdapter);

        rgExpert.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                switch (arg1) {
                    case R.id.rb_legitimateDataNum:
                        hideTitleTextViewRight(false);
                        expertAdapter.clear();
                        expertAdapter.addAll(expertInfoList);
                        expertAdapter.notifyDataSetChanged();
                        break;
                    case R.id.rb_illegalDataNum:
                        hideTitleTextViewRight(true);
                        expertAdapter.clear();
                        expertAdapter.addAll(expertIllegalInfoList);
                        expertAdapter.notifyDataSetChanged();
                        break;
                    case R.id.rb_dbDataNum:
                        hideTitleTextViewRight(true);
                        expertAdapter.clear();
                        expertAdapter.addAll(ExpertDBManager.getInstance(
                                ImportExcelActivity.this).loadExpertInfo());
                        expertAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });


    }

    /**
     * 初始化adapter
     *
     * @param file
     */
    private void initExpertAdapter(File file) {

        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(file);
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }// 获取文档
        sheetStudent = wb.getSheet(0); // 获取工作簿

        rowNum = sheetStudent.getRows();// 获取行数
        if (sheetStudent.getColumns() >= 5) {
            // 判断列数是否为5
            for (int i = 1; i < rowNum; i++) {
                if (sheetStudent.getCell(0, i).getContents() != null
                        && sheetStudent.getCell(1, i).getContents() != null
                        && sheetStudent.getCell(2, i).getContents() != null
                        && sheetStudent.getCell(3, i).getContents() != null
                        && sheetStudent.getCell(4, i).getContents() != null
                        && sheetStudent.getCell(3, i).getContents().length() >= 11) {
//                    合法数据
                    ExpertInfo expertInfo = new ExpertInfo();
                    expertInfo.setId(Integer.valueOf(sheetStudent.getCell(0,
                            i)
                            .getContents()));
                    expertInfo.setExpertCode(sheetStudent.getCell(1, i)
                            .getContents());
                    expertInfo
                            .setName(sheetStudent.getCell(2, i).getContents());
                    expertInfo.setTel(sheetStudent.getCell(3, i).getContents());
                    expertInfo.setMsgContent(sheetStudent.getCell(4, i)
                            .getContents());
                    expertInfoList.add(expertInfo);
                } else if (sheetStudent.getCell(0, i).getContents() != null && sheetStudent.getCell(0, i).getContents().length() != 0) {
//                    非法数据
                    ExpertInfo expertInfo = new ExpertInfo();
                    expertInfo.setId(Integer.valueOf(sheetStudent.getCell(0, i)
                            .getContents()));
                    expertInfo.setExpertCode(sheetStudent.getCell(1, i)
                            .getContents());
                    expertInfo
                            .setName(sheetStudent.getCell(2, i).getContents());
                    expertInfo.setTel(sheetStudent.getCell(3, i).getContents());
                    expertInfo.setMsgContent(sheetStudent.getCell(4, i)
                            .getContents());
                    expertIllegalInfoList.add(expertInfo);
                }
            }
            expertAdapter.addAll(expertInfoList);
            expertAdapter.notifyDataSetChanged();
            rbLegitimateDataNum.setText("合法数据：" + expertInfoList.size());
            rbIllegalDataNum.setText("非法数据：" + expertIllegalInfoList.size());
            rbDbDataNum.setText("入库数据："
                    + ExpertDBManager.getInstance(ImportExcelActivity.this)
                    .loadExpertInfo().size());
        } else {
            ToastUtil.showToast(ImportExcelActivity.this,
                    "excle表格式不正确，请查看是否选对excel表！");
            finish();
        }
        wb.close();

    }

    public void saveExcel() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                notExpertInfoList.clear();
                MyProgressDialog.startProgressDialog(ImportExcelActivity.this,
                        "正在向数据库导入数据，请稍后...");
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                for (ExpertInfo expertInfo : expertInfoList) {
                    long result = ExpertDBManager.getInstance(
                            ImportExcelActivity.this).saveExpertDBManager(
                            ImportExcelActivity.this, expertInfo);
                    if (result == -1) {
                        notExpertInfoList.add(expertInfo);
                    } else if (result == 0 || result < -1) {
                        notExpertInfoList.add(expertInfo);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                MyProgressDialog.stopProgressDialog();
                int listSize = expertInfoList.size();

                expertInfoList.clear();
                expertInfoList.addAll(notExpertInfoList);

                expertAdapter.clear();
                expertAdapter.addAll(expertInfoList);
                expertAdapter.notifyDataSetChanged();
                int dbNum = ExpertDBManager
                        .getInstance(ImportExcelActivity.this).loadExpertInfo()
                        .size();
                rbDbDataNum.setText("入库数据：" + dbNum);
                rbLegitimateDataNum.setText("合法数据：" + expertInfoList.size());
                showNum.setVisibility(View.VISIBLE);
                showNum.setText("已入库：" + dbNum + ",本次导入失败："
                        + notExpertInfoList.size() + ",本次导入成功："
                        + (listSize - notExpertInfoList.size()));
            }

        }.execute();
    }
}
