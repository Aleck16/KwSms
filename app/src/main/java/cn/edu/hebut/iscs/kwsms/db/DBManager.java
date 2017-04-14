package cn.edu.hebut.iscs.kwsms.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQLite数据库管理类
 *
 * 主要负责数据库资源的初始化,开启,关闭,以及获得DatabaseHelper帮助类操作
 * 
 * @author Tailyou0506
 * 
 */
public class DBManager {
	public final static int VERSION = 3;		//之前为2，现在修改为3。升级了一次
	public final static String DB_NAME = "MOBILE_SMS.db";
	public final static String EXPERT_TABLE = "EXPERT_TABLE";
	public final static String SEND_STATUS_TABLE = "SEND_STATUS_TABLE";
	public final static String REPLY_TABLE = "REPLY_TABLE";
	private Context mContext = null;
	private static DBManager dBManager = null;

	/**
	 * 构造函数
	 * 
	 * @param mContext
	 */
	private DBManager(Context mContext) {
		super();
		this.mContext = mContext;

	}

	public static synchronized DBManager getInstance(Context mContext,
			String databaseName) {
		if (null == dBManager) {
			dBManager = new DBManager(mContext);
		}
		return dBManager;
	}

	/**
	 * 关闭数据库 注意:当事务成功或者一次性操作完毕时候再关闭
	 */
	public void closeDatabase(SQLiteDatabase dataBase, Cursor cursor) {
		if (null != dataBase) {
			dataBase.close();
		}
		if (null != cursor) {
			cursor.close();
		}
	}

	/**
	 * 打开数据库 注:SQLiteDatabase资源一旦被关闭,该底层会重新产生一个新的SQLiteDatabase
	 */
	public SQLiteDatabase openDatabase() {
		return getDatabaseHelper().getWritableDatabase();
	}

	/**
	 * 获取DataBaseHelper
	 * 
	 * @return
	 */
	public DataBaseHelper getDatabaseHelper() {
		return new DataBaseHelper(mContext, DB_NAME, null, VERSION);
	}

	/**
	 * 查看该表是否存在
	 * 
	 * @param table
	 * @return
	 */

	public boolean exits(String table) {
		boolean exits = false;
		String sql = "select * from sqlite_master where name=" + "'" + table
				+ "'";
		Cursor cursor = openDatabase().rawQuery(sql, null);
		if (cursor.getCount() != 0) {
			exits = true;
		}
		return exits;
	}

	public void dropTable() {
		SQLiteDatabase sqLiteDatabase = dBManager.openDatabase();
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBManager.REPLY_TABLE);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
				+ DBManager.SEND_STATUS_TABLE);
		sqLiteDatabase
				.execSQL("DROP TABLE IF EXISTS " + DBManager.EXPERT_TABLE);
	}

	/**
	 * 删除数据库
	 * 
	 * @return
	 */
	public boolean deleteDatabase() {
		return getDatabaseHelper().getDatabasePath(DB_NAME).delete();
	}
}
