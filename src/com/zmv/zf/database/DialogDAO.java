package com.zmv.zf.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.IOUtils;

public class DialogDAO {

	private SQLiteDatabase db;
	private static final String TABLENAME = "dialoginfo";

	public DialogDAO(Context context) {

		try {
			db = SQLiteDatabase.openOrCreateDatabase(
					IOUtils.getDatabaseFolder2(), null);
			db.execSQL("create table  if  not  exists  "
					+ TABLENAME
					+ "(id integer primary key autoincrement,dialogid varchar(50),uid varchar(20),nickname varchar(20),icon varchar(100),msg varchar(200),msgnums integer, logintime  datetime)");
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
	public List<BaseJson> findDialog() {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		List<BaseJson> list_user = new ArrayList<BaseJson>();
		BaseJson user = null;
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ "  ORDER  BY  logintime  DESC", null);
			while (cursor.moveToNext()) {
				user = new BaseJson();
				user.setDialogId(cursor.getString(cursor
						.getColumnIndex("dialogid")));
				user.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				user.setName(cursor.getString(cursor.getColumnIndex("nickname")));
				user.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
				user.setMsg(cursor.getString(cursor.getColumnIndex("msg")));
				user.setMsgnum(cursor.getInt(cursor.getColumnIndex("msgnums")));
				list_user.add(user);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			return list_user;
		}

	}

	public BaseJson findTalker() {
		BaseJson user = null;
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ "  ORDER BY RANDOM() limit 1", null);
			while (cursor.moveToNext()) {
				user = new BaseJson();
				user.setDialogId(cursor.getString(cursor
						.getColumnIndex("dialogid")));
				user.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				user.setName(cursor.getString(cursor.getColumnIndex("nickname")));
				user.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
				user.setMsg(cursor.getString(cursor.getColumnIndex("msg")));
				user.setMsgnum(cursor.getInt(cursor.getColumnIndex("msgnums")));
				
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			return user;
		}
	}

	/**
	 * // * 更新MSG
	 */
	public void updateMsg(String dialogid, String msg) {

		try {
			String sql;
			if ((Conf.UID + Conf.OPUID).equals(dialogid)) {
				sql = "UPDATE  "
						+ TABLENAME
						+ "  SET logintime = datetime(CURRENT_TIMESTAMP,'localtime') , msg='"
						+ msg + "' WHERE dialogid='" + dialogid + "'";
			} else {
				sql = "UPDATE  "
						+ TABLENAME
						+ "  SET logintime = datetime(CURRENT_TIMESTAMP,'localtime') , msg='"
						+ msg + "',msgnums=msgnums+1  WHERE dialogid='"
						+ dialogid + "'";
			}
			if (db.isOpen()) {
				db.execSQL(sql);
			}
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// public void updateMsgNum(String uid) {
	//
	// try {
	// String sql = "UPDATE  " + TABLENAME
	// + "  SET msgnums=0  WHERE uid='" + uid + "'";
	//
	// if (db.isOpen()) {
	// db.execSQL(sql);
	// }
	// db.close();
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	//
	// }

	/**
	 * addDialog
	 */
	public void addDialog(BaseJson user) {
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ " WHERE uid='" + user.getUid() + "'", null);
			boolean flag = false;
			while (cursor.moveToNext()) {
				flag = true;
			}
			if (!flag) {
				db.execSQL(
						"insert into  "
								+ TABLENAME
								+ "(dialogid,uid,nickname,icon,msg,msgnums, logintime) values(?,?,?,?,?,?,datetime(CURRENT_TIMESTAMP,'localtime'))",
						new Object[] { Conf.UID + user.getUid(), user.getUid(),
								user.getName(), user.getIcon(), "", 0 });
			} else {
				String sql = "UPDATE  " + TABLENAME
						+ "  SET msgnums=0  WHERE uid='" + user.getUid() + "'";
				if (db.isOpen()) {
					db.execSQL(sql);
				}
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * addDialog
	 */
	public void addListDialog(List<BaseJson> list_user) {
		try {
			int user_size = list_user.size();
			BaseJson user;
			Cursor cursor = null;
			if (user_size > 3)
				user_size = 3;

			for (int i = 0; i < user_size; i++) {
				user = list_user.get(i);
				cursor = db.rawQuery("SELECT * FROM " + TABLENAME
						+ " WHERE uid='" + user.getUid() + "'", null);
				boolean flag = false;
				while (cursor.moveToNext()) {
					flag = true;
				}
				if (!flag) {
					db.execSQL(
							"insert into  "
									+ TABLENAME
									+ "(dialogid,uid,nickname,icon,msg,msgnums,logintime) values(?,?,?,?,?,?,datetime(CURRENT_TIMESTAMP,'localtime'))",
							new Object[] { Conf.UID + user.getUid(),
									user.getUid(), user.getName(),
									user.getIcon(), user.getIntro(), 1 });
				}
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	// 更新使用时间
	// public void updateUser(String userid) {
	// // db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
	// // null);
	// String sql = "UPDATE  "
	// + TABLENAME
	// +
	// "  SET  logintime = datetime(CURRENT_TIMESTAMP,'localtime')  WHERE  uid='"
	// + userid + "'";
	// try {
	// if (db.isOpen()) {
	// db.execSQL(sql);
	// db.close();
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }

	/**
	 * 刪除
	 * 
	 * @param sids
	 */
	// public void delete(int sids) {
	// String whereClause = "uid=?";
	// String[] whereArgs = new String[] { String.valueOf(sids) };
	// try {
	//
	// if (db.isOpen()) {
	// db.delete(TABLENAME, whereClause, whereArgs);
	// db.close();
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }

}
