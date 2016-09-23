package cn.edu.hebut.iscs.kwsms.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hebut.iscs.kwsms.entity.SmsInfo;

public class SmsUtil {
	/**
	 * 所有的短信
	 */
	public static final String SMS_URI_ALL = "content://sms/";
	/**
	 * 收件箱短信
	 */
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	/**
	 * 发件箱短信
	 */
	public static final String SMS_URI_SEND = "content://sms/sent";
	/**
	 * 草稿箱短信
	 */
	public static final String SMS_URI_DRAFT = "content://sms/draft";

	// 读取的短信信息有：
	// _id：短信序号，如100 　　
	// 　thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的 　　
	// address：发件人地址，即手机号，如+8613811810000 　　
	// 　person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null 　　
	// 　date：日期，long型，如1256539465022，可以对日期显示格式进行设置 　　
	// 　protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信 　　 　　read：是否阅读0未读，1已读 　　
	// 　　status：短信状态-1接收，0complete,64pending,128failed 　　
	// 　　type：短信类型1是接收到的，2是已发出 　　 　　body：短信具体内容 　　
	// 　　service_center：短信服务中心号码编号，如+8613800755500

	/**
	 * 得到收件箱中的短信
	 * 
	 * @param context
	 * @return
	 */
	public List<SmsInfo> getSmsInfo(Context context, long date) {
		List<SmsInfo> infos = new ArrayList<SmsInfo>();
		ContentResolver content = context.getContentResolver();
		String[] projection = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		Uri uri = Uri.parse(SMS_URI_INBOX);
		Cursor cusor = content.query(uri, projection, "date>" + date, null,
				"date desc");
		int nameColumn = cusor.getColumnIndex("person");
		int phoneNumberColumn = cusor.getColumnIndex("address");
		int smsbodyColumn = cusor.getColumnIndex("body");
		int dateColumn = cusor.getColumnIndex("date");
		int typeColumn = cusor.getColumnIndex("type");
		if (cusor != null) {
			while (cusor.moveToNext()) {
				int phoneNumberLength=cusor.getString(phoneNumberColumn).length();
				if (phoneNumberLength >= 11&&phoneNumberLength <= 14) {
					SmsInfo smsinfo = new SmsInfo();
					smsinfo.setName(cusor.getString(nameColumn));
					smsinfo.setDate(cusor.getString(dateColumn));
					smsinfo.setPhoneNumber(cusor.getString(phoneNumberColumn).substring(phoneNumberLength-11));
					smsinfo.setSmsbody(cusor.getString(smsbodyColumn));
					smsinfo.setType(cusor.getString(typeColumn));
					infos.add(smsinfo);
				}
			}
			cusor.close();
		}
		return infos;
	}
}


