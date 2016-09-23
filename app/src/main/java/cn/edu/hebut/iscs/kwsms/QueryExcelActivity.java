package cn.edu.hebut.iscs.kwsms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import cn.edu.hebut.iscs.kwsms.view.MyProgressDialog;

/**
 * Created by lixueyang on 16-8-16.
 */
public class QueryExcelActivity extends BaseTitleActivity {

    @BindView(R.id.listView_excel)
    ListView listViewExcel;

    private ArrayList<Map<String, Object>> excelName = new ArrayList<Map<String, Object>>();
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSetContentView(R.layout.activity_query_excel);
        ButterKnife.bind(this);
        setTitleBarText("excel列表");
        queryExcle();
        listViewExcel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(QueryExcelActivity.this,
                        ImportExcelActivity.class);
                intent.putExtra("path", excelName.get(i).get("Path")
                        .toString());
                startActivity(intent);
            }
        });

    }

    public void queryExcle() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MyProgressDialog.startProgressDialog(QueryExcelActivity.this,
                        "正在读取excle列表，请稍后...");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    File path = Environment.getExternalStorageDirectory();// 获得SD卡路径
                    // File path = new File("/mnt/sdcard/");
                    File[] files = path.listFiles();// 读取
                    getFileName(files);
                }
                if (excelName != null && excelName.size() > 0) {
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
                    ToastUtil.showToast(QueryExcelActivity.this, "读取完成！");
                    adapter = new SimpleAdapter(QueryExcelActivity.this, excelName,
                            R.layout.item_for_excel_list, new String[]{"Name"},
                            new int[]{R.id.excel_name});
                    listViewExcel.setAdapter(adapter);
                } else {
                    ToastUtil
                            .showToast(QueryExcelActivity.this, "没有excel表，请添加excel表");
                    finish();
                }
            }

        }.execute();
    }

    private void getFileName(File[] files) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {
                    getFileName(file.listFiles());
                } else {
                    String fileName = file.getName();
                    if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("Name", fileName);
                        map.put("Path", file.getPath());
                        excelName.add(map);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyProgressDialog.stopProgressDialog();
    }

}
