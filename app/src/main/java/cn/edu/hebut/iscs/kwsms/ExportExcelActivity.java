package cn.edu.hebut.iscs.kwsms;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.entity.ReplyStateInfo;
import cn.edu.hebut.iscs.kwsms.entity.SendStateInfo;
import cn.edu.hebut.iscs.kwsms.helper.ExpertDBManager;
import cn.edu.hebut.iscs.kwsms.util.DateTimeUtil;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by lixueyang on 16-8-22.
 */
public class ExportExcelActivity extends BaseTitleActivity {
    @BindView(R.id.editText_name)
    EditText editTextName;
    @BindView(R.id.parent_folder)
    TextView parentFolder;
    @BindView(R.id.file_list)
    ListView fileList;

    private List<ReplyStateInfo> reList = new ArrayList<ReplyStateInfo>();
    private List<SendStateInfo> sendList = new ArrayList<SendStateInfo>();

    // 记录当前的父文件夹
    private File currentParent;
    // 记录当前路径下的所有文件的文件数组
    private File[] currentFiles;
    private String path;
    private List<ExpertInfo> noReplylist;

    private Label bll0, bll1, bll2, bll3, bll4, bll5, bll6, bll7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSetContentView(R.layout.activity_export_excel);
        ButterKnife.bind(this);
        initUIHeader();
        initUI();
    }

    private void initUIHeader() {
        setTitleBarText("导出数据");
        getTitleTextViewRight().setText("导出");
        getTitleTextViewRight().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String time = DateTimeUtil
                        .getCurrentTime(DateTimeUtil.format_7);

                String rname = editTextName.getText().toString() + "(" + time
                        + ")";
                String sname = editTextName.getText().toString() + "send" + "("
                        + time + ")";
                try {
                    path = currentParent.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rname = path + "/" + rname + ".xls";
                sname = path + "/" + sname + ".xls";
                writeReplyExcel(rname);
                writeSendExcel(sname);
                ToastUtil.showToast(ExportExcelActivity.this, "导出成功！");
            }
        });
    }

    public void initUI() {
        File root = new File(Environment.getExternalStorageDirectory()
                .toString());
        if (root.exists()) {
            currentParent = root;
            currentFiles = root.listFiles();
            inflateListView(currentFiles);
        }
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (currentFiles[position].isFile())
                    return;
                File[] tmp = currentFiles[position].listFiles();
                if (tmp == null || tmp.length == 0) {
                    ToastUtil.showToast(ExportExcelActivity.this,
                            "当前路径不可访问或该路径没有文佳");
                } else {
                    currentParent = currentFiles[position];
                    currentFiles = tmp;
                    inflateListView(currentFiles);
                }
            }
        });
        parentFolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (!currentParent.getCanonicalPath().equals(
                            Environment.getExternalStorageDirectory()
                                    .toString())) {
                        currentParent = currentParent.getParentFile();
                        currentFiles = currentParent.listFiles();
                        inflateListView(currentFiles);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void inflateListView(File[] files) {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            if (files[i].isDirectory()) {
                listItem.put("icon", R.mipmap.folder);
            } else {
                listItem.put("icon", R.mipmap.file);
            }
            listItem.put("fileName", files[i].getName());
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.layout_folder_list,
                new String[] { "icon", "fileName" }, new int[] { R.id.icon,
                R.id.file_name });
        fileList.setAdapter(simpleAdapter);
        // try{
        //
        // }catch(IOException e){
        // e.printStackTrace();
        // }

    }

    // public String getTimeNow() {
    //
    // String time = null;
    // Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
    // t.setToNow(); // 取得系统时间。
    // // int year = t.year;
    // // int month = t.month;
    // // int date = t.monthDay;
    // // time = year + "_" + month + "_" + date;
    // time=t.format(DateTimeUtil.format_7).toString();
    // return time;
    // }

    // 导出回复信息表
    public void writeReplyExcel(String fileName) {
        WritableWorkbook wwb = null;
        reList.clear();
        reList = ExpertDBManager.getInstance(ExportExcelActivity.this)
                .loadReplyStateInfoList();
        noReplylist = ExpertDBManager.getInstance(ExportExcelActivity.this)
                .queryNoReplyList();
        try {
            // 创建一个可写入的工作薄(Workbook)对象
            wwb = Workbook.createWorkbook(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wwb != null) {
            // 第一个参数是工作表的名称，第二个是工作表在工作薄中的位置
            WritableSheet wsY = wwb.createSheet("同意", 0);// 同意的表
            WritableSheet wsN = wwb.createSheet("不同意", 0);// 反对的表
            WritableSheet wsO = wwb.createSheet("回复其他", 0);// 回复其他的表
            WritableSheet wsUnReply = wwb.createSheet("未回复", 0);// 未回复的表
            WritableSheet wsUnKnow = wwb.createSheet("无法匹配", 0);// 未回复的表

            // 在指定单元格插入数据
            int i = 0, j = 0, x = 0, y = 0, z = 0;

            bll0 = new Label(0, 0, "序号");
            bll1 = new Label(1, 0, "专家号");
            bll2 = new Label(2, 0, "专家姓名");
            bll3 = new Label(3, 0, "电话");
//            bll4 = new Label(4, 0, "回复结果");
            bll4 = new Label(4, 0, "短新内容");
            try {
                wsUnReply.addCell(bll0);
                wsUnReply.addCell(bll1);
                wsUnReply.addCell(bll2);
                wsUnReply.addCell(bll3);
                wsUnReply.addCell(bll4);
            } catch (RowsExceededException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
            z++;
            for (ExpertInfo expertInfo : noReplylist) {

                bll0 = new Label(0, z, expertInfo.getId() + "");
                bll1 = new Label(1, z, expertInfo.getExpertCode());
                bll2 = new Label(2, z, expertInfo.getName());
                bll3 = new Label(3, z, expertInfo.getTel());
//                bll4 = new Label(4, z, "未回复");
                bll4 = new Label(4, z, expertInfo.getMsgContent());
                try {
                    wsUnReply.addCell(bll0);
                    wsUnReply.addCell(bll1);
                    wsUnReply.addCell(bll2);
                    wsUnReply.addCell(bll3);
                    wsUnReply.addCell(bll4);
                } catch (RowsExceededException e1) {
                    e1.printStackTrace();
                } catch (WriteException e1) {
                    e1.printStackTrace();
                }
                z++;
            }

            bll0 = new Label(0, 0, "序号");
            bll1 = new Label(1, 0, "专家号");
            bll2 = new Label(2, 0, "专家姓名");
            bll3 = new Label(3, 0, "回复电话");
            bll4 = new Label(4, 0, "回复时间");
            bll5 = new Label(5, 0, "回复内容");
            bll6 = new Label(6, 0, "回复结果");
            bll7 = new Label(7, 0, "专家电话");
            try {
                wsUnKnow.addCell(bll0);
                wsUnKnow.addCell(bll1);
                wsUnKnow.addCell(bll2);
                wsUnKnow.addCell(bll3);
                wsUnKnow.addCell(bll4);
                wsUnKnow.addCell(bll5);
                wsUnKnow.addCell(bll6);
                wsUnKnow.addCell(bll7);
            } catch (RowsExceededException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
            bll0 = new Label(0, 0, "序号");
            bll1 = new Label(1, 0, "专家号");
            bll2 = new Label(2, 0, "专家姓名");
            bll3 = new Label(3, 0, "回复电话");
            bll4 = new Label(4, 0, "回复时间");
            bll5 = new Label(5, 0, "回复内容");
            bll6 = new Label(6, 0, "回复结果");
            bll7 = new Label(7, 0, "专家电话");
            try {
                wsY.addCell(bll0);
                wsY.addCell(bll1);
                wsY.addCell(bll2);
                wsY.addCell(bll3);
                wsY.addCell(bll4);
                wsY.addCell(bll5);
                wsY.addCell(bll6);
                wsY.addCell(bll7);
            } catch (RowsExceededException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
            bll0 = new Label(0, 0, "序号");
            bll1 = new Label(1, 0, "专家号");
            bll2 = new Label(2, 0, "专家姓名");
            bll3 = new Label(3, 0, "回复电话");
            bll4 = new Label(4, 0, "回复时间");
            bll5 = new Label(5, 0, "回复内容");
            bll6 = new Label(6, 0, "回复结果");
            bll7 = new Label(7, 0, "专家电话");
            try {
                wsN.addCell(bll0);
                wsN.addCell(bll1);
                wsN.addCell(bll2);
                wsN.addCell(bll3);
                wsN.addCell(bll4);
                wsN.addCell(bll5);
                wsN.addCell(bll6);
                wsN.addCell(bll7);
            } catch (RowsExceededException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
            bll0 = new Label(0, 0, "序号");
            bll1 = new Label(1, 0, "专家号");
            bll2 = new Label(2, 0, "专家姓名");
            bll3 = new Label(3, 0, "回复电话");
            bll4 = new Label(4, 0, "回复时间");
            bll5 = new Label(5, 0, "回复内容");
            bll6 = new Label(6, 0, "回复结果");
            bll7 = new Label(7, 0, "专家电话");
            try {
                wsO.addCell(bll0);
                wsO.addCell(bll1);
                wsO.addCell(bll2);
                wsO.addCell(bll3);
                wsO.addCell(bll4);
                wsO.addCell(bll5);
                wsO.addCell(bll6);
                wsO.addCell(bll7);
            } catch (RowsExceededException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
            i++;
            j++;
            x++;
            y++;
            for (ReplyStateInfo rpi : reList) {

                switch (rpi.getYesNoOther()) {
                    case "0":
                        bll0 = new Label(0, i, rpi.getId() + "");
                        bll1 = new Label(1, i, rpi.getExpertCode());
                        bll2 = new Label(2, i, rpi.getExpertName());
                        bll3 = new Label(3, i, rpi.getTel());
                        bll4 = new Label(4, i, rpi.getReplyTime());
                        bll5 = new Label(5, i, rpi.getReplyContent());
                        bll6 = new Label(6, i, "未匹配");
                        bll7 = new Label(7, i, rpi.getTellnExpertTable());
                        try {
                            wsUnKnow.addCell(bll0);
                            wsUnKnow.addCell(bll1);
                            wsUnKnow.addCell(bll2);
                            wsUnKnow.addCell(bll3);
                            wsUnKnow.addCell(bll4);
                            wsUnKnow.addCell(bll5);
                            wsUnKnow.addCell(bll6);
                            wsUnKnow.addCell(bll7);
                        } catch (RowsExceededException e1) {
                            e1.printStackTrace();
                        } catch (WriteException e1) {
                            e1.printStackTrace();
                        }
                        i++;
                        break;
                    case "1":
                        bll0 = new Label(0, j, rpi.getId() + "");
                        bll1 = new Label(1, j, rpi.getExpertCode());
                        bll2 = new Label(2, j, rpi.getExpertName());
                        bll3 = new Label(3, j, rpi.getTel());
                        bll4 = new Label(4, j, rpi.getReplyTime());
                        bll5 = new Label(5, j, rpi.getReplyContent());
                        bll6 = new Label(6, j, "同意");
                        bll7 = new Label(7, j, rpi.getTellnExpertTable());
                        try {
                            wsY.addCell(bll0);
                            wsY.addCell(bll1);
                            wsY.addCell(bll2);
                            wsY.addCell(bll3);
                            wsY.addCell(bll4);
                            wsY.addCell(bll5);
                            wsY.addCell(bll6);
                            wsY.addCell(bll7);
                        } catch (RowsExceededException e1) {
                            e1.printStackTrace();
                        } catch (WriteException e1) {
                            e1.printStackTrace();
                        }
                        j++;
                        break;
                    case "2":
                        bll0 = new Label(0, x, rpi.getId() + "");
                        bll1 = new Label(1, x, rpi.getExpertCode());
                        bll2 = new Label(2, x, rpi.getExpertName());
                        bll3 = new Label(3, x, rpi.getTel());
                        bll4 = new Label(4, x, rpi.getReplyTime());
                        bll5 = new Label(5, x, rpi.getReplyContent());
                        bll6 = new Label(6, x, "不同意");
                        bll7 = new Label(7, x, rpi.getTellnExpertTable());
                        try {
                            wsN.addCell(bll0);
                            wsN.addCell(bll1);
                            wsN.addCell(bll2);
                            wsN.addCell(bll3);
                            wsN.addCell(bll4);
                            wsN.addCell(bll5);
                            wsN.addCell(bll6);
                            wsN.addCell(bll7);
                        } catch (RowsExceededException e1) {
                            e1.printStackTrace();
                        } catch (WriteException e1) {
                            e1.printStackTrace();
                        }
                        x++;
                        break;
                    case "3":
                        bll0 = new Label(0, y, rpi.getId() + "");
                        bll1 = new Label(1, y, rpi.getExpertCode());
                        bll2 = new Label(2, y, rpi.getExpertName());
                        bll3 = new Label(3, y, rpi.getTel());
                        bll4 = new Label(4, y, rpi.getReplyTime());
                        bll5 = new Label(5, y, rpi.getReplyContent());
                        bll6 = new Label(6, y, "回复其他");
                        bll7 = new Label(7, y, rpi.getTellnExpertTable());
                        try {
                            wsO.addCell(bll0);
                            wsO.addCell(bll1);
                            wsO.addCell(bll2);
                            wsO.addCell(bll3);
                            wsO.addCell(bll4);
                            wsO.addCell(bll5);
                            wsO.addCell(bll6);
                            wsO.addCell(bll7);
                        } catch (RowsExceededException e1) {
                            e1.printStackTrace();
                        } catch (WriteException e1) {
                            e1.printStackTrace();
                        }
                        y++;
                        break;
                }
            }

            try {
                // 从内存中写入文件中
                wwb.write();
                wwb.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }

    // 导出发送状态表
    public void writeSendExcel(String fileName) {
        WritableWorkbook wwb = null;
        sendList.clear();
        sendList = ExpertDBManager.getInstance(ExportExcelActivity.this)
                .loadSendStateInfo();
        try {
            // 创建一个可写入的工作薄(Workbook)对象
            wwb = Workbook.createWorkbook(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wwb != null) {
            // 第一个参数是工作表的名称，第二个是工作表在工作薄中的位置
            WritableSheet ws = wwb.createSheet("sheet1", 0);

            /**
             * 在指定单元格插入数据
             */
            // 插入标题
            bll0 = new Label(0, 0, "序号");
            bll1 = new Label(1, 0, "专家号");
            bll2 = new Label(2, 0, "短信编号");
            bll3 = new Label(3, 0, "发送时间");
            bll4 = new Label(4, 0, "发送状态");
            try {
                ws.addCell(bll0);
                ws.addCell(bll1);
                ws.addCell(bll2);
                ws.addCell(bll3);
                ws.addCell(bll4);
            } catch (RowsExceededException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
            // 插入数据
            int i = 1;
            for (SendStateInfo sdi : sendList) {
                bll0 = new Label(0, i, sdi.getId() + "");
                bll1 = new Label(1, i, sdi.getExpertCode());
                bll2 = new Label(2, i, sdi.getMsgId());
                bll3 = new Label(3, i, sdi.getTime());
                bll4 = null;
                switch (sdi.getStatus()) {
                    case "0":
                        bll4 = new Label(4, i, "未发送");
                        break;
                    case "1":
                        bll4 = new Label(4, i, "发送成功");
                        break;
                    case "2":
                        bll4 = new Label(4, i, "对方未接受");
                        break;
                    case "3":
                        bll4 = new Label(4, i, "发送失败");
                        break;
                }
                i++;
                try {
                    ws.addCell(bll0);
                    ws.addCell(bll1);
                    ws.addCell(bll2);
                    ws.addCell(bll3);
                    ws.addCell(bll4);
                } catch (RowsExceededException e1) {
                    e1.printStackTrace();
                } catch (WriteException e1) {
                    e1.printStackTrace();
                }
            }

            try {
                // 从内存中写入文件中
                wwb.write();
                wwb.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }
}
