package com.zmv.zf.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.utils.IOUtils;

public class SendDAO {

	private static SQLiteDatabase db;
	static String TABLENAME = "talkmsg";

	public SendDAO(Context context) {
		try {
			db = SQLiteDatabase.openOrCreateDatabase(
					IOUtils.getDatabaseFolder(), null);
			db.execSQL("create table  if  not  exists  "
					+ TABLENAME
					+ "(id integer primary key autoincrement,dialogid varchar(50),uid varchar(20),type integer,icon varchar(100),img varchar(100),msg varchar(200),singletime varchar(20), formattime varchar(50))");
			// Log.e("创建表", "OK");
		} catch (Exception e) {
			// TODO: handle exception
			// Log.e("创建表异常", e.toString());
		}

	}

	/**
	 * 添加
	 * 
	 */
	public void add(String dialogid, String uid, int type, String icon,
			String img, String msg, String singletime, String formattime) {

		try {
			// 将所有数据过期的值设置为今日:0
			db.execSQL(
					"insert into "
							+ TABLENAME
							+ "(dialogid ,uid,type,icon,img,msg,singletime ,formattime) values(?,?,?,?,?,?,?,?)",
					new Object[] { dialogid, uid, type, icon, img, msg,
							singletime, formattime });
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 添加Exemplar
	 * 
	 */
	public List<BaseJson> findMsg(String dialogid) {
		List<BaseJson> list_person = new ArrayList<BaseJson>();
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ "  WHERE dialogid='" + dialogid + "'", null);
			BaseJson send;
			while (cursor.moveToNext()) {
				send = new BaseJson();
				send.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				send.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
				send.setBigicon(cursor.getString(cursor.getColumnIndex("img")));
				send.setMsg(cursor.getString(cursor.getColumnIndex("msg")));
				send.setType(cursor.getInt(cursor.getColumnIndex("type")));
				send.setTime(cursor.getString(cursor
						.getColumnIndex("singletime")));
				send.setFormat_time(cursor.getString(cursor
						.getColumnIndex("formattime")));
				list_person.add(send);
			}

			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			return list_person;
		}

	}

	// 发送次数
	public int sendTime(String dialogid, String uid) {
		int sendtime = 0;
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ "  WHERE dialogid='" + dialogid + "'  and uid ='" + uid
					+ "'", null);
			while (cursor.moveToNext()) {
				sendtime += 1;
			}

			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			return sendtime;
		}

	}

}
