package cn.edu.hebut.iscs.kwsms.entity;

import java.io.Serializable;

public class ExpertInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3145436033000995332L;

	private int id;
	private String name;
	private String expertCode;
	private String tel;
	private String msgContent;
	private String autoReplyContent;		//自动回复的内容

	public String getAutoReplyContent() {
		return autoReplyContent;
	}

	public ExpertInfo setAutoReplyContent(String autoReplyContent) {
		this.autoReplyContent = autoReplyContent;
		return this;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpertCode() {
		return expertCode;
	}

	public void setExpertCode(String expertCode) {
		this.expertCode = expertCode;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

}
