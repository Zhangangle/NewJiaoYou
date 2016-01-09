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

public class VideoDAO {

	private SQLiteDatabase db;
	private static final String TABLENAME = "videoinfo";

	public VideoDAO(Context context) {

		try {
			db = SQLiteDatabase.openOrCreateDatabase(
					IOUtils.getDatabaseFolder(), null);
			db.execSQL("create table  if  not  exists " + TABLENAME
					+ "(id integer primary key autoincrement,"
					+ "uid varchar(20),nickname varchar(50),"
					+ "topicid varchar(20),imgUrl varchar(100),"
					+ "timeLength varchar(10),videoUrl varchar(100),"
					+ "reviews integer,likes integer,sold integer, "
					+ "title varchar(50))");
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
	public void addvideo() {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME, null);
			boolean flag = false;
			while (cursor.moveToNext()) {
				flag = true;
				break;
			}
			if (!flag) {
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
							"insert into  "
									+ TABLENAME
									+ "(uid,nickname,"
									+ "topicid,imgUrl,timeLength,videoUrl,"
									+ "reviews,likes,sold,title) values(?,?,?,?,?,?,?,?,?,?)",
							new Object[] { uid, nickname, topicid, imgUrl,
									timeLength, videoUrl, reviews, likes, sold,
									title });
				}
			} else {
				String sql = "UPDATE  " + TABLENAME + "  SET reviews=reviews+"
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
//			Log.e("添加数据2", e.toString());
		}

	}

	// 获取推荐列表数据
	public List<BaseJson> playData() {
		List<BaseJson> list = new ArrayList<>();
		BaseJson user;
		try {
			String sql = "UPDATE  " + TABLENAME + "  SET reviews=reviews+"
					+ (new Random().nextInt(2) + 1) + " ,likes=likes+"
					+ (new Random().nextInt(2) + 1) + " ,sold=sold+"
					+ (new Random().nextInt(2) + 1);
			if (db.isOpen()) {
				db.execSQL(sql);
			}
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ " ORDER BY RANDOM() limit 8", null);
			while (cursor.moveToNext()) {
				user = new BaseJson();
				user.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				user.setName(cursor.getString(cursor.getColumnIndex("nickname")));
				user.setIntro(cursor.getString(cursor.getColumnIndex("title")));
				user.setTopicId(cursor.getString(cursor
						.getColumnIndex("topicid")));
				user.setVideoimg(cursor.getString(cursor
						.getColumnIndex("imgUrl")));
				user.setVideourl(cursor.getString(cursor
						.getColumnIndex("videoUrl")));
				user.setReviewnums(cursor.getInt(cursor
						.getColumnIndex("reviews")));

				user.setLikenum(cursor.getInt(cursor.getColumnIndex("likes")));
				user.setBuynums(cursor.getInt(cursor.getColumnIndex("sold")));

				list.add(user);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	// 获取推荐列表数据
	public List<BaseJson> perData(String uid) {
		List<BaseJson> list = new ArrayList<BaseJson>();
		try {
			BaseJson user;
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ " WHERE uid='" + uid + "' ORDER BY RANDOM() limit 6",
					null);
			while (cursor.moveToNext()) {
				user = new BaseJson();
				user.setUid(cursor.getString(cursor.getColumnIndex("uid")));
				user.setName(cursor.getString(cursor.getColumnIndex("nickname")));
				user.setIntro(cursor.getString(cursor.getColumnIndex("title")));
				user.setTopicId(cursor.getString(cursor
						.getColumnIndex("topicid")));
				user.setVideoimg(cursor.getString(cursor
						.getColumnIndex("imgUrl")));
				user.setVideourl(cursor.getString(cursor
						.getColumnIndex("videoUrl")));
				user.setReviewnums(cursor.getInt(cursor
						.getColumnIndex("reviews")));
				user.setLikenum(cursor.getInt(cursor.getColumnIndex("likes")));
				user.setBuynums(cursor.getInt(cursor.getColumnIndex("sold")));
				list.add(user);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
}
