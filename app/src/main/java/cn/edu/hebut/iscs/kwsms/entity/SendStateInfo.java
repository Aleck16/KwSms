package cn.edu.hebut.iscs.kwsms.entity;

import java.io.Serializable;

public class SendStateInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9100918809097252133L;

	private int id;
	private String expertCode;
	private String msgId;
	private String time;
	private String status;

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

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
