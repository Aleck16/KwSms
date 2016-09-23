package cn.edu.hebut.iscs.kwsms.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import cn.edu.hebut.iscs.kwsms.db.DBManager;
import cn.edu.hebut.iscs.kwsms.db.SQLiteTemplate;
import cn.edu.hebut.iscs.kwsms.entity.ExpertInfo;
import cn.edu.hebut.iscs.kwsms.entity.ReplyStateInfo;
import cn.edu.hebut.iscs.kwsms.entity.SendStateInfo;
import cn.edu.hebut.iscs.kwsms.util.ToastUtil;


public class ExpertDBManager {



    private static ExpertDBManager expertDBManager;
    private static DBManager dbManager = null;

    private ExpertDBManager(Context context) {
        dbManager = DBManager.getInstance(context, DBManager.DB_NAME);
    }

    public static synchronized ExpertDBManager getInstance(Context context) {
        if (expertDBManager == null) {
            expertDBManager = new ExpertDBManager(context);
        }
        return expertDBManager;
    }

    /**
     * 保存专家信息
     *
     * @param context
     * @param expertInfo
     */
    public long saveExpertDBManager(Context context, ExpertInfo expertInfo) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        ContentValues values = new ContentValues();
        values.put("EXPERT_CODE", expertInfo.getExpertCode());
        values.put("TELE", expertInfo.getTel());
        values.put("NAME", expertInfo.getName());
        values.put("MSG_CONTENT", expertInfo.getMsgContent());
        return st.insert(DBManager.EXPERT_TABLE, values);
    }

    /**
     * 加载所有专家信息
     *
     * @return
     */
    public List<ExpertInfo> loadExpertInfo() {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        List<ExpertInfo> list = st.queryForList(new SQLiteTemplate.RowMapper<ExpertInfo>() {

            @Override
            public ExpertInfo mapRow(Cursor cursor, int index) {
                ExpertInfo expertInfo = new ExpertInfo();
                expertInfo.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                expertInfo.setExpertCode(cursor.getString(cursor
                        .getColumnIndex("EXPERT_CODE")));
                expertInfo.setName(cursor.getString(cursor
                        .getColumnIndex("NAME")));
                expertInfo.setTel(cursor.getString(cursor
                        .getColumnIndex("TELE")));
                expertInfo.setMsgContent(cursor.getString(cursor
                        .getColumnIndex("MSG_CONTENT")));
                return expertInfo;
            }
        }, "SELECT * FROM EXPERT_TABLE", null);
        return list;
    }

    /**
     * 匹配电话， 如果与专家表中电话匹配，则返回专家新信息 否则返回null
     *
     * @param phone
     * @return
     */
    public ExpertInfo matchExpertInfo(String phone) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        ExpertInfo expertInfo = st.queryForObject(new SQLiteTemplate.RowMapper<ExpertInfo>() {

            @Override
            public ExpertInfo mapRow(Cursor cursor, int index) {
                ExpertInfo expertInfo = new ExpertInfo();
                expertInfo.setExpertCode(cursor.getString(cursor
                        .getColumnIndex("EXPERT_CODE")));
                expertInfo.setName(cursor.getString(cursor
                        .getColumnIndex("NAME")));
                expertInfo.setTel(cursor.getString(cursor
                        .getColumnIndex("TELE")));
                return expertInfo;
            }
        }, "select * from EXPERT_TABLE where TELE=?", new String[] { phone });
        return expertInfo;
    }

    /**
     * 通过专家姓名得到专家信息
     *
     * @param name
     * @return ExpertInfo
     */
    public List<ExpertInfo> loadExpertInfo(String name) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        List<ExpertInfo> list = st.queryForList(new SQLiteTemplate.RowMapper<ExpertInfo>() {

            @Override
            public ExpertInfo mapRow(Cursor cursor, int index) {
                ExpertInfo expertInfo = new ExpertInfo();
                expertInfo.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                expertInfo.setExpertCode(cursor.getString(cursor
                        .getColumnIndex("EXPERT_CODE")));
                expertInfo.setName(cursor.getString(cursor
                        .getColumnIndex("NAME")));
                expertInfo.setTel(cursor.getString(cursor
                        .getColumnIndex("TELE")));
                expertInfo.setMsgContent(cursor.getString(cursor
                        .getColumnIndex("MSG_CONTENT")));
                return expertInfo;
            }
        }, "select * from EXPERT_TABLE where NAME=?", new String[] { name });

        return list;

    }

    /**
     * 保存发送状态
     *
     * @param context
     * @param sendStateInfo
     */
    public void saveSendStateInfo(Context context, SendStateInfo sendStateInfo) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        ContentValues values = new ContentValues();
        values.put("MSG_ID", sendStateInfo.getMsgId());
        values.put("EXPERT_CODE", sendStateInfo.getExpertCode());
        values.put("STATUS", sendStateInfo.getStatus());
        values.put("TIME", sendStateInfo.getTime());
        long isSucsess = st.insert(DBManager.SEND_STATUS_TABLE, values);
        if (isSucsess <= 0) {
            ToastUtil.showToast(context,
                    "EXPERT_CODE为" + sendStateInfo.getExpertCode()
                            + "的教授信息存入数据库失败");
        }
    }

    /**
     * 加载所有发送信息
     *
     * @return
     */
    public List<SendStateInfo> loadSendStateInfo() {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        List<SendStateInfo> list = st.queryForList(
                new SQLiteTemplate.RowMapper<SendStateInfo>() {

                    @Override
                    public SendStateInfo mapRow(Cursor cursor, int index) {
                        SendStateInfo sendStateInfo = new SendStateInfo();
                        sendStateInfo.setId(Integer.parseInt(cursor
                                .getString(cursor.getColumnIndex("ID"))));
                        sendStateInfo.setMsgId(cursor.getString(cursor
                                .getColumnIndex("MSG_ID")));
                        sendStateInfo.setStatus(cursor.getString(cursor
                                .getColumnIndex("STATUS")));
                        sendStateInfo.setExpertCode(cursor.getString(cursor
                                .getColumnIndex("EXPERT_CODE")));
                        sendStateInfo.setTime(cursor.getString(cursor
                                .getColumnIndex("TIME")));
                        return sendStateInfo;
                    }
                }, "SELECT * FROM SEND_STATUS_TABLE", null);
        return list;
    }

    /**
     * 更新发送状态
     * @param expertCode
     * @param status
     */

    public void updateSendStateInfo(String expertCode, String status) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        ContentValues values = new ContentValues();
        values.put("STATUS", status);
        st.update(DBManager.SEND_STATUS_TABLE, values, "EXPERT_CODE=?",
                new String[] { expertCode });
    }

    /**
     * 保存回复状态
     *
     * @param context
     * @param replyStateInfo
     */
    public void saveReplyStateInfo(Context context,
                                   ReplyStateInfo replyStateInfo) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        ContentValues values = new ContentValues();
        values.put("EXPERT_CODE", replyStateInfo.getExpertCode());
        values.put("NAME", replyStateInfo.getExpertName());
        values.put("TEL", replyStateInfo.getTel());
        values.put("REPLY_CONTENT", replyStateInfo.getReplyContent());
        values.put("REPLY_TIME", replyStateInfo.getReplyTime());
        values.put("YES_NO_OTHER", replyStateInfo.getYesNoOther());
        values.put("IS_TEL_VALID", replyStateInfo.getIsTelValid());
        values.put("TELLN_EXPERT_TABLE", replyStateInfo.getTellnExpertTable());
        st.insert(DBManager.REPLY_TABLE, values);
    }

    /**
     * 加载所有回复信息
     *
     * @return
     */
    public List<ReplyStateInfo> loadReplyStateInfoList() {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        List<ReplyStateInfo> list = st.queryForList(
                new SQLiteTemplate.RowMapper<ReplyStateInfo>() {

                    @Override
                    public ReplyStateInfo mapRow(Cursor cursor, int index) {
                        ReplyStateInfo replyStateInfo = new ReplyStateInfo();
                        replyStateInfo.setId(Integer.parseInt(cursor
                                .getString(cursor.getColumnIndex("ID"))));
                        replyStateInfo.setExpertCode(cursor.getString(cursor
                                .getColumnIndex("EXPERT_CODE")));
                        replyStateInfo.setExpertName(cursor.getString(cursor
                                .getColumnIndex("NAME")));
                        replyStateInfo.setTel(cursor.getString(cursor
                                .getColumnIndex("TEL")));
                        replyStateInfo.setReplyTime(cursor.getString(cursor
                                .getColumnIndex("REPLY_TIME")));
                        replyStateInfo.setReplyContent(cursor.getString(cursor
                                .getColumnIndex("REPLY_CONTENT")));
                        replyStateInfo.setIsTelValid(cursor.getString(cursor
                                .getColumnIndex("IS_TEL_VALID")));
                        replyStateInfo.setYesNoOther(cursor.getString(cursor
                                .getColumnIndex("YES_NO_OTHER")));
                        replyStateInfo.setTellnExpertTable(cursor
                                .getString(cursor
                                        .getColumnIndex("TELLN_EXPERT_TABLE")));
                        return replyStateInfo;
                    }
                }, "SELECT * FROM REPLY_TABLE ORDER BY REPLY_TIME DESC", null);
        return list;
    }

    /**
     * 更新回复表中回复结果，是yes还是no
     * @param type
     * @param tel
     * @param expertInfo
     */

    public void updateReplyYesNo(String type, String tel, ExpertInfo expertInfo) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        ContentValues values = new ContentValues();
        values.put("YES_NO_OTHER", type);
        if (expertInfo != null) {
            values.put("EXPERT_CODE", expertInfo.getExpertCode());
            values.put("NAME", expertInfo.getName());
            values.put("TELLN_EXPERT_TABLE", expertInfo.getTel());
            values.put("IS_TEL_VALID", "1");
        }
        st.update(DBManager.REPLY_TABLE, values, "TEL=?", new String[] { tel });
    }

    /**
     * 得到回复表中最后的时间
     *
     * @return
     */

    public long queryReplyLastTime() {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        String sql = "SELECT * FROM REPLY_TABLE ORDER BY REPLY_TIME DESC LIMIT 1";
        ReplyStateInfo replyStateInfo = st.queryForObject(
                new SQLiteTemplate.RowMapper<ReplyStateInfo>() {

                    @Override
                    public ReplyStateInfo mapRow(Cursor cursor, int index) {
                        ReplyStateInfo replyStateInfo = new ReplyStateInfo();
                        replyStateInfo.setReplyTime(cursor.getString(cursor
                                .getColumnIndex("REPLY_TIME")));
                        return replyStateInfo;
                    }
                }, sql, null);
        if (replyStateInfo == null) {
            return 0;
        } else {
            return Long.valueOf(replyStateInfo.getReplyTime());
        }
    }

    /**
     * 获取发送情况不同的专家信息 status=="1"为成功，2为发出去后对方未接收到，3为发了未发出去，0为未发送
     *
     * @param status
     * @return
     */
    public List<ExpertInfo> querySendResultList(String status) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        List<ExpertInfo> list = st
                .queryForList(
                        new SQLiteTemplate.RowMapper<ExpertInfo>() {

                            @Override
                            public ExpertInfo mapRow(Cursor cursor, int index) {
                                ExpertInfo expertInfo = new ExpertInfo();
                                expertInfo.setId(cursor.getInt(cursor
                                        .getColumnIndex("ID")));
                                expertInfo
                                        .setExpertCode(cursor.getString(cursor
                                                .getColumnIndex("EXPERT_CODE")));
                                expertInfo.setName(cursor.getString(cursor
                                        .getColumnIndex("NAME")));
                                expertInfo.setTel(cursor.getString(cursor
                                        .getColumnIndex("TELE")));
                                expertInfo
                                        .setMsgContent(cursor.getString(cursor
                                                .getColumnIndex("MSG_CONTENT")));
                                return expertInfo;
                            }
                        },
                        "SELECT * FROM EXPERT_TABLE WHERE EXPERT_CODE IN (SELECT EXPERT_CODE FROM SEND_STATUS_TABLE WHERE STATUS=? ORDER BY TIME DESC)",
                        new String[] { status });
        return list;
    }

    /**
     * 获取未发送的专家信息
     *
     * @return
     */
    public List<ExpertInfo> queryNoSendList() {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        List<ExpertInfo> list = st
                .queryForList(
                        new SQLiteTemplate.RowMapper<ExpertInfo>() {

                            @Override
                            public ExpertInfo mapRow(Cursor cursor, int index) {
                                ExpertInfo expertInfo = new ExpertInfo();
                                expertInfo.setId(cursor.getInt(cursor
                                        .getColumnIndex("ID")));
                                expertInfo
                                        .setExpertCode(cursor.getString(cursor
                                                .getColumnIndex("EXPERT_CODE")));
                                expertInfo.setName(cursor.getString(cursor
                                        .getColumnIndex("NAME")));
                                expertInfo.setTel(cursor.getString(cursor
                                        .getColumnIndex("TELE")));
                                expertInfo
                                        .setMsgContent(cursor.getString(cursor
                                                .getColumnIndex("MSG_CONTENT")));
                                return expertInfo;
                            }
                        },
                        "SELECT * FROM EXPERT_TABLE WHERE EXPERT_CODE NOT IN (SELECT EXPERT_CODE FROM SEND_STATUS_TABLE)",
                        null);
        return list;
    }

    /**
     * 获取未回复的专家信息
     *
     * @return
     */
    public List<ExpertInfo> queryNoReplyList() {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        List<ExpertInfo> list = st
                .queryForList(
                        new SQLiteTemplate.RowMapper<ExpertInfo>() {

                            @Override
                            public ExpertInfo mapRow(Cursor cursor, int index) {
                                ExpertInfo expertInfo = new ExpertInfo();
                                expertInfo.setId(cursor.getInt(cursor
                                        .getColumnIndex("ID")));
                                expertInfo
                                        .setExpertCode(cursor.getString(cursor
                                                .getColumnIndex("EXPERT_CODE")));
                                expertInfo.setName(cursor.getString(cursor
                                        .getColumnIndex("NAME")));
                                expertInfo.setTel(cursor.getString(cursor
                                        .getColumnIndex("TELE")));
                                expertInfo
                                        .setMsgContent(cursor.getString(cursor
                                                .getColumnIndex("MSG_CONTENT")));
                                return expertInfo;
                            }
                        },
                        "SELECT * FROM EXPERT_TABLE WHERE EXPERT_CODE NOT IN (SELECT EXPERT_CODE FROM REPLY_TABLE WHERE EXPERT_CODE IS NOT NULL)",
                        null);
        return list;
    }

    /**
     * 获取不同情况的回复 status=="1"为回复y，2为回复n，3为回复其他，0为无法匹配
     *
     * @param status
     * @return
     */
    public List<ReplyStateInfo> queryReplyResultList(String status) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        List<ReplyStateInfo> list = st.queryForList(
                new SQLiteTemplate.RowMapper<ReplyStateInfo>() {

                    @Override
                    public ReplyStateInfo mapRow(Cursor cursor, int index) {
                        ReplyStateInfo replyStateInfo = new ReplyStateInfo();
                        replyStateInfo.setId(Integer.parseInt(cursor
                                .getString(cursor.getColumnIndex("ID"))));
                        replyStateInfo.setTel(cursor.getString(cursor
                                .getColumnIndex("TEL")));
                        replyStateInfo.setReplyTime(cursor.getString(cursor
                                .getColumnIndex("REPLY_TIME")));
                        replyStateInfo.setReplyContent(cursor.getString(cursor
                                .getColumnIndex("REPLY_CONTENT")));
                        replyStateInfo.setIsTelValid(cursor.getString(cursor
                                .getColumnIndex("IS_TEL_VALID")));
                        replyStateInfo.setYesNoOther(cursor.getString(cursor
                                .getColumnIndex("YES_NO_OTHER")));
                        replyStateInfo.setTellnExpertTable(cursor
                                .getString(cursor
                                        .getColumnIndex("TELLN_EXPERT_TABLE")));
                        return replyStateInfo;
                    }
                }, "SELECT * FROM REPLY_TABLE WHERE YES_NO_OTHER=?",
                new String[] { status });
        return list;
    }

    /**
     * 查看回复表是否已存在该电话
     *
     * @param tel
     * @return
     */

    public ReplyStateInfo loadReplyStateInfo(String tel) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        ReplyStateInfo replyStateInfo = st.queryForObject(
                new SQLiteTemplate.RowMapper<ReplyStateInfo>() {

                    @Override
                    public ReplyStateInfo mapRow(Cursor cursor, int index) {
                        ReplyStateInfo replyStateInfo = new ReplyStateInfo();
                        replyStateInfo.setExpertCode(cursor.getString(cursor
                                .getColumnIndex("EXPERT_CODE")));
                        replyStateInfo.setExpertName(cursor.getString(cursor
                                .getColumnIndex("NAME")));
                        replyStateInfo.setTel(cursor.getString(cursor
                                .getColumnIndex("TEL")));
                        replyStateInfo.setReplyContent(cursor.getString(cursor
                                .getColumnIndex("REPLY_CONTENT")));
                        replyStateInfo.setYesNoOther(cursor.getString(cursor
                                .getColumnIndex("YES_NO_OTHER")));
                        return replyStateInfo;
                    }
                }, "select * from REPLY_TABLE where TEL=?",
                new String[] { tel });
        return replyStateInfo;
    }

    /**
     * 更新多次短信
     * @param content
     * @param time
     * @param replyStateInfo
     */


    public void updateMoreReplyResult(String content, String time,
                                      ReplyStateInfo replyStateInfo) {
        SQLiteTemplate st = SQLiteTemplate.getInstance(dbManager, false);
        ContentValues values = new ContentValues();
        if (!replyStateInfo.getYesNoOther().equals("0")) {
            values.put("YES_NO_OTHER", "3");
        }
        values.put("REPLY_TIME", time);
        values.put("REPLY_CONTENT", replyStateInfo.getReplyContent() + "#"
                + content);
        st.update(DBManager.REPLY_TABLE, values, "TEL=?",
                new String[] { replyStateInfo.getTel() });
    }

}
