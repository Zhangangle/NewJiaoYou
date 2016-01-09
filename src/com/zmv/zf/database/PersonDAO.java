package com.zmv.zf.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.utils.IOUtils;

public class PersonDAO {

	private SQLiteDatabase db;
	private static final String TABLENAME = "perinfo";

	public PersonDAO(Context context) {

		try {
			db = SQLiteDatabase.openOrCreateDatabase(
					IOUtils.getDatabaseFolder(), null);
			db.execSQL("create table  if  not  exists " + TABLENAME
					+ "(id integer primary key autoincrement,"
					+ "uid varchar(20),nickname varchar(50),"
					+ "age integer,address varchar(100),"
					+ "intro varchar(100),"
					+ "icon varchar(100),bigicon varchar(100),"
					+ "pic varchar(5000),bigpic varchar(5000),"
					+ "fans integer,reviews integer," + "videos integer,"
					+ "likes integer,playnums integer, "
					+ "online varchar(50)," + "messages integer)");
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
	public void addper() {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME, null);
			boolean flag = false;
			while (cursor.moveToNext()) {
				flag = true;
//				Log.e("已经添加数据", "已经添加数据");
				break;
			}
			if (!flag) {
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
							"insert into  "
									+ TABLENAME
									+ "(uid ,nickname,age,address,intro,icon,bigicon,"
									+ "pic ,bigpic,fans ,reviews,videos,likes,playnums,"
									+ "online,messages) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[] { uid, nickname, age, address, intro,
									icon, bigicon, pic, bigpic, fans, reviews,
									videos, likes, playnums, online, messages });
				}
			} else {
//				Log.e("增加数据", "add数据");
				String sql = "UPDATE  " + TABLENAME + "  SET fans=fans+"
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
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	// 获取推荐列表数据
	public List<BaseJson> shareData() {
		List<BaseJson> list = new ArrayList<>();
		BaseJson user;
		try {
			String sql = "UPDATE  " + TABLENAME + "  SET fans=fans+"
					+ (new Random().nextInt(2) + 1) + " ,reviews=reviews+"
					+ (new Random().nextInt(2) + 1) + " ,likes=likes+"
					+ (new Random().nextInt(2) + 1) + " ,playnums=playnums+"
					+ (new Random().nextInt(2) + 1) + " ,reviews=reviews+"
					+ (new Random().nextInt(2) + 1);
			if (db.isOpen()) {
				db.execSQL(sql);
			}
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ " ORDER BY RANDOM() limit 6", null);
			while (cursor.moveToNext()) {
				user = new BaseJson();
				user.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				user.setName(cursor.getString(cursor.getColumnIndex("nickname")));
				user.setAge(cursor.getInt(cursor.getColumnIndex("age")));
				user.setAddress(cursor.getString(cursor
						.getColumnIndex("address")));
				user.setMsgnum(cursor.getInt(cursor.getColumnIndex("messages")));
				user.setLikenum(cursor.getInt(cursor.getColumnIndex("likes")));
				user.setReviewnums(cursor.getInt(cursor
						.getColumnIndex("reviews")));
				user.setFans(cursor.getInt(cursor.getColumnIndex("fans")));
				user.setVideonums(cursor.getInt(cursor.getColumnIndex("videos")));
				user.setPlaynums(cursor.getInt(cursor
						.getColumnIndex("playnums")));
				user.setIntro(cursor.getString(cursor.getColumnIndex("intro")));
				user.setIcon(cursor.getString(cursor.getColumnIndex("icon")));
				user.setBigicon(cursor.getString(cursor
						.getColumnIndex("bigicon")));
				user.setOnline(cursor.getString(cursor.getColumnIndex("online")));
				list.add(user);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	// 根据id获取数据
	public BaseJson picData(String uid) {
		BaseJson pic = null;
		try {

			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ " WHERE uid='" + uid + "'", null);
			while (cursor.moveToNext()) {
				pic = new BaseJson();
				pic.setPics(cursor.getString(cursor.getColumnIndex("pic")));
				pic.setBigPics(cursor.getString(cursor.getColumnIndex("bigpic")));
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return pic;
	}

	public String userIcon(String uid) {
		String pic = null;
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ " WHERE uid='" + uid + "'", null);
			while (cursor.moveToNext()) {
				pic = cursor.getString(cursor.getColumnIndex("icon"));
//				Log.e("图片", pic);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return pic;
	}
	// private void updateData() {
	// try {
	// String sql = "UPDATE  " + TABLENAME + "  SET fans=fans"
	// + (new Random().nextInt(2) + 1) + " ,reviews=reviews"
	// + (new Random().nextInt(2) + 2) + " ,likes=likes"
	// + (new Random().nextInt(2) + 1) + " ,playnums=playnums"
	// + (new Random().nextInt(2) + 3) + " ,reviews=reviews"
	// + (new Random().nextInt(3) + 2);
	// if (db.isOpen()) {
	// db.execSQL(sql);
	// }
	// db.close();
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// Log.e("hehe", e.toString());
	// }
	// }
}
