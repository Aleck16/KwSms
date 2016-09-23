package cn.edu.hebut.iscs.kwsms.entity;

/**
 * class name��SmsInfo<BR>
 * class description����ȡ���Ÿ�����Ϣ����<BR>
 * 
 */
public class SmsInfo {
	/**
	 * ��������
	 */
	private String smsbody;
	/**
	 * ���Ͷ��ŵĵ绰����
	 */
	private String phoneNumber;
	/**
	 * ���Ͷ��ŵ����ں�ʱ��
	 */
	private String date;
	/**
	 * ���Ͷ����˵�����
	 */
	private String name;
	/**
	 * ��������1�ǽ��յ��ģ�2���ѷ���
	 */
	private String type;

	public String getSmsbody() {
		return smsbody;
	}

	public void setSmsbody(String smsbody) {
		this.smsbody = smsbody;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
