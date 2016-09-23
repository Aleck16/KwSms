package cn.edu.hebut.iscs.kwsms.entity;

import java.io.Serializable;

public class ReplyStateInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3497568592321411263L;

    private int id;
    private String expertCode;
    private String expertName;
    private String tel;
    private String replyContent;
    private String replyTime;
    private String yesNoOther;
    //	是否为有效电话，1为有效，2为未知
    private String isTelValid;
    //没有录入的电话，1为yes，2为no，3为其他
    private String tellnExpertTable;

    //	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpertCode() {
        return expertCode;
    }

    public void setExpertCode(String expertCode) {
        this.expertCode = expertCode;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getYesNoOther() {
        return yesNoOther;
    }

    public void setYesNoOther(String yesNoOther) {
        this.yesNoOther = yesNoOther;
    }

    public String getIsTelValid() {
        return isTelValid;
    }

    public void setIsTelValid(String isTelValid) {
        this.isTelValid = isTelValid;
    }

    public String getTellnExpertTable() {
        return tellnExpertTable;
    }

    public void setTellnExpertTable(String tellnExpertTable) {
        this.tellnExpertTable = tellnExpertTable;
    }

}
