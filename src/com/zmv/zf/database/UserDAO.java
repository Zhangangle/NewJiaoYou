package com.zmv.zf.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.IOUtils;

public class UserDAO {

	private SQLiteDatabase db;
	private static final String TABLENAME = "userinfo";
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

	public UserDAO(Context context) {

		try {
			db = SQLiteDatabase.openOrCreateDatabase(
					IOUtils.getDatabaseFolder(), null);
			db.execSQL("create table  if  not  exists  "
					+ TABLENAME
					+ "(id integer primary key autoincrement,uid varchar(25) ,nickname varchar(50),isvip integer,isday integer,open integer, logintime  datetime)");
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
	public BaseJson findUid() {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		BaseJson user = null;
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME, null);
			boolean flag_user = false;
			while (cursor.moveToNext()) {
				user = new BaseJson();
				user.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				user.setName(cursor.getString(cursor.getColumnIndex("nickname")));
				user.setIsVip(cursor.getInt(cursor.getColumnIndex("isvip")));
				user.setDay(cursor.getInt(cursor.getColumnIndex("isday")));
				user.setOpen(cursor.getInt(cursor.getColumnIndex("open")));
				flag_user = true;
			}
			if (!flag_user) {
				db.execSQL(
						"insert into  "
								+ TABLENAME
								+ "(uid,nickname,isvip,isday,open,logintime) values(?,?,?,?,?,datetime(CURRENT_TIMESTAMP,'localtime'))",
						new Object[] { Conf.IMEI + "2", "", 0, 0, 1 });
				user = new BaseJson();
				user.setUid(Conf.IMEI + "2");
				user.setName("");
				user.setIsVip(0);
				user.setDay(0);
				user.setOpen(1);
			}// 添加个人
			db.execSQL("create table  if  not  exists  perinfo(id integer primary key autoincrement,"
					+ "uid varchar(20),nickname varchar(50),"
					+ "age integer,address varchar(100),"
					+ "intro varchar(100),"
					+ "icon varchar(100),bigicon varchar(100),"
					+ "pic varchar(5000),bigpic varchar(5000),"
					+ "fans integer,reviews integer,"
					+ "videos integer,"
					+ "likes integer,playnums integer, "
					+ "online varchar(50)," + "messages integer)");

			cursor = db.rawQuery("SELECT * FROM  perinfo", null);
			boolean flag_per = false;
			while (cursor.moveToNext()) {
				flag_per = true;
//				Log.e("已经添加数据", "已经添加数据");
				break;
			}
			if (!flag_per) {
				JSONObject jsonObject = new JSONObject(DataBase.USERMSG);
				JSONArray jsonArray = jsonObject.getJSONArray("Content");
				String uid, nickname, address, intro, icon, bigicon, pic, bigpic, online;
				int age, fans, reviews, videos, likes, playnums, messages;
				JSONArray picArray;
				int json_Length = jsonArray.length();
				int pic_Length;
				StringBuffer sb_pic, sb_bigpic;
				for (int i = 0; i < json_Length; i++) {
					uid = jsonArray.getJSONObject(i).getString("UserId");// 用户ID
					nickname = jsonArray.getJSONObject(i).getString("Nick");// 昵称
					age = jsonArray.getJSONObject(i).getInt("Age");// 年龄
					address = jsonArray.getJSONObject(i)
							.getString("Occupation");// 地址
					intro = jsonArray.getJSONObject(i).getString("Intro");// 个人简介
					icon = jsonArray.getJSONObject(i).getString("Avatar");// 头像
					bigicon = jsonArray.getJSONObject(i).getString("Cover");// 头像大图
					fans = jsonArray.getJSONObject(i).getInt("Fans");// 粉丝
					reviews = jsonArray.getJSONObject(i).getInt("Reviews");// 评论
					videos = jsonArray.getJSONObject(i).getInt("Videos");// 视频个数
					likes = jsonArray.getJSONObject(i).getInt("Likes");// 点赞个数
					playnums = jsonArray.getJSONObject(i).getInt("VideoPlays");// 播放个数
					online = jsonArray.getJSONObject(i).getString("Online");// 在线时间
					messages = jsonArray.getJSONObject(i).getInt("Messages");// 消息数
					picArray = jsonArray.getJSONObject(i)
							.getJSONArray("Photos");
					pic_Length = picArray.length();
					sb_bigpic = new StringBuffer();
					sb_pic = new StringBuffer();
					for (int j = 0; j < pic_Length; j++) {
						sb_pic.append(picArray.getJSONObject(j).getString(
								"Img1"));
						sb_bigpic.append(picArray.getJSONObject(j).getString(
								"Img2"));
						if (j != pic_Length - 1) {
							sb_pic.append(";");
							sb_bigpic.append(";");
						}
					}
					pic = sb_pic.toString();
					bigpic = sb_bigpic.toString();
					db.execSQL(
							"insert into  perinfo(uid ,nickname,age,address,intro,icon,bigicon,"
									+ "pic ,bigpic,fans ,reviews,videos,likes,playnums,"
									+ "online,messages) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[] { uid, nickname, age, address, intro,
									icon, bigicon, pic, bigpic, fans, reviews,
									videos, likes, playnums, online, messages });
				}
			} else {
//				Log.e("增加数据", "add数据");
				String sql = "UPDATE  perinfo  SET fans=fans+"
						+ (new Random().nextInt(10) + 1) + " ,reviews=reviews+"
						+ (new Random().nextInt(10) + 2) + " ,likes=likes+"
						+ (new Random().nextInt(10) + 1)
						+ " ,playnums=playnums+"
						+ (new Random().nextInt(10) + 3) + " ,reviews=reviews+"
						+ (new Random().nextInt(10) + 2);
				if (db.isOpen()) {
					db.execSQL(sql);
				}
			}
			// 添加视频
			db.execSQL("create table  if  not  exists videoinfo(id integer primary key autoincrement,"
					+ "uid varchar(20),nickname varchar(50),"
					+ "topicid varchar(20),imgUrl varchar(100),"
					+ "timeLength varchar(10),videoUrl varchar(100),"
					+ "reviews integer,likes integer,sold integer, "
					+ "title varchar(50))");

			cursor = db.rawQuery("SELECT * FROM videoinfo", null);
			boolean flag_video = false;
			while (cursor.moveToNext()) {
				flag_video = true;
				break;
			}
			if (!flag_video) {
				JSONObject jsonObject = new JSONObject(DataBase.VIDEOMSG);
				JSONArray jsonArray = jsonObject.getJSONArray("Content");
				String uid, nickname, topicid, imgUrl, videoUrl, title, timeLength;
				int reviews, likes, sold;
				int json_Length = jsonArray.length();
				for (int i = 0; i < json_Length; i++) {
					uid = jsonArray.getJSONObject(i).getString("uid");// 用户ID
					nickname = jsonArray.getJSONObject(i).getString("nickName");// 昵称
					topicid = jsonArray.getJSONObject(i).getString("topicid");// 视频ID
					imgUrl = jsonArray.getJSONObject(i).getString("imgUrl");// 图片
					videoUrl = jsonArray.getJSONObject(i).getString("videoUrl");// 播放地址
					title = jsonArray.getJSONObject(i).getString("title");// 标题
					timeLength = jsonArray.getJSONObject(i).getString(
							"timeLength");// 播放时长
					reviews = jsonArray.getJSONObject(i).getInt("reviews");// 评论
					sold = jsonArray.getJSONObject(i).getInt("sold");// 出售视频个数
					likes = jsonArray.getJSONObject(i).getInt("likes");// 点赞个数

					db.execSQL(
							"insert into videoinfo(uid,nickname,"
									+ "topicid,imgUrl,timeLength,videoUrl,"
									+ "reviews,likes,sold,title) values(?,?,?,?,?,?,?,?,?,?)",
							new Object[] { uid, nickname, topicid, imgUrl,
									timeLength, videoUrl, reviews, likes, sold,
									title });
				}
			} else {
				String sql = "UPDATE  videoinfo  SET reviews=reviews+"
						+ (new Random().nextInt(10) + 2) + " ,likes=likes+"
						+ (new Random().nextInt(10) + 1) + " ,sold=sold+"
						+ (new Random().nextInt(10) + 3);
				if (db.isOpen()) {
					db.execSQL(sql);
				}
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
	 * // * 修改昵称
	 */
	public void updateNick(String nickname) {

		try {
			String sql = "UPDATE  " + TABLENAME + "  SET nickname='" + nickname
					+ "'";
			if (db.isOpen()) {
				db.execSQL(sql);
			}
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * // * 修改VIP
	 */
	public void updateVIP(int isday) {
		int isVip = Integer.valueOf(df.format(new Date()));
		try {
			String sql = "UPDATE  " + TABLENAME + "  SET isvip=" + isVip
					+ " ,isday=" + isday;
			if (db.isOpen()) {
				db.execSQL(sql);
			}
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * // * 修改Open
	 */
	public void updateOpen(int open) {
		try {
			String sql = "UPDATE  " + TABLENAME + "  SET open=" + open;
			if (db.isOpen()) {
				db.execSQL(sql);
			}
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
