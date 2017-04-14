package cn.edu.hebut.iscs.kwsms.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import static cn.edu.hebut.iscs.kwsms.db.DBManager.DB_NAME;

public class DataBaseHelper extends SDCardSQLiteOpenHelper {

	public static final String CREATE_EXPERT_TABLE = "CREATE TABLE EXPERT_TABLE ("
			+ "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "TELE TEXT NOT NULL,"
			+ "NAME TEXT NOT NULL,"
			+ "EXPERT_CODE TEXT UNIQUE NOT NULL,"
			+ "MSG_CONTENT TEXT NOT NULL,"
			+ "AUTO_REPLY_CONTENT TEXT)";//AUTO_REPLY_CONTENT自动回复内容

	public static final String CREATE_SEND_STATUS_TABLE = "CREATE TABLE SEND_STATUS_TABLE ("
			+ "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "EXPERT_CODE TEXT,"
			+ "MSG_ID TEXT,"
			+ "STATUS TEXT," + "TIME TEXT)";

	public static final String CREATE_REPLY_TABLE = "CREATE TABLE REPLY_TABLE ("
			+ "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "EXPERT_CODE TEXT,"
			+ "NAME TEXT,"
			+ "TEL TEXT,"
			+ "REPLY_CONTENT TEXT,"
			+ "REPLY_TIME TEXT,"
			+ "YES_NO_OTHER TEXT,"
			+ "IS_TEL_VALID TEXT," + "TELLN_EXPERT_TABLE TEXT," + "AUTO_REPLY_NUM INTEGER DEFAULT 0)";	//Auto_Reply_num：自动回复次数,默认值为0

	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_EXPERT_TABLE);
		db.execSQL(CREATE_SEND_STATUS_TABLE);
		db.execSQL(CREATE_REPLY_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//数据库更新，直接删除所有的表，再重新建立表
		db.execSQL("drop table if exists EXPERT_TABLE");
		db.execSQL("drop table if exists SEND_STATUS_TABLE");
		db.execSQL("drop table if exists REPLY_TABLE");
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	
}
