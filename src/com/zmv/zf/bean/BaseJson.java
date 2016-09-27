package com.zmv.zf.bean;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class BaseJson implements Serializable {

	private String status; // 状态

	private String name;// 名称
	private String icon;// 头像
	private String pics;// 个人图片(多张)
	private String bigPics;// 个人图片(多张)

	private int msgnum;// 私信次数
	private String online;// 在线时间
	private int age;// 年龄
	private int likenum;// 点赞人数
	private String bigicon;// 个人大图
	private String uid;// 用户id;
	private int playnums;// 播放次数
	private int videonums;// 视频个数
	private String intro;// 个人介绍
	private String address;// 地址与工作
	private int fans;// 粉丝
	private int reviewnums;// 评论数
	private int buynums;// 购买次数
	private String title;// 标题
	private String topicId;// 视频id
	private int type;// 类型
	private String videourl;// 视频播放地址
	private String videoimg;// 视频图片地址
	private int isVip;// vip时间
	private int day;// vip保留天数
	private String time;// 普通
	private String format_time;// 格式化的时间
	private int open;
	private String dialogId;// 对话ID
	private String playtimes;// 播放时长
	
	
	

	public String getPlaytimes() {
		return playtimes;
	}

	public void setPlaytimes(String playtimes) {
		this.playtimes = playtimes;
	}

	public int getOpen() {
		return open;
	}

	public void setOpen(int open) {
		this.open = open;
	}

	private String msg;//

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDialogId() {
		return dialogId;
	}

	public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFormat_time() {
		return format_time;
	}

	public void setFormat_time(String format_time) {
		this.format_time = format_time;
	}

	public String getPics() {
		return pics;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setPics(String pics) {
		this.pics = pics;
	}

	public String getBigPics() {
		return bigPics;
	}

	public void setBigPics(String bigPics) {
		this.bigPics = bigPics;
	}

	public int getMsgnum() {
		return msgnum;
	}

	public void setMsgnum(int msgnum) {
		this.msgnum = msgnum;
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getLikenum() {
		return likenum;
	}

	public void setLikenum(int likenum) {
		this.likenum = likenum;
	}

	public String getBigicon() {
		return bigicon;
	}

	public void setBigicon(String bigicon) {
		this.bigicon = bigicon;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getPlaynums() {
		return playnums;
	}

	public void setPlaynums(int playnums) {
		this.playnums = playnums;
	}

	public int getVideonums() {
		return videonums;
	}

	public void setVideonums(int videonums) {
		this.videonums = videonums;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getReviewnums() {
		return reviewnums;
	}

	public void setReviewnums(int reviewnums) {
		this.reviewnums = reviewnums;
	}

	public int getBuynums() {
		return buynums;
	}

	public void setBuynums(int buynums) {
		this.buynums = buynums;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getVideourl() {
		return videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}

	public String getVideoimg() {
		return videoimg;
	}

	public void setVideoimg(String videoimg) {
		this.videoimg = videoimg;
	}

	public int getIsVip() {
		return isVip;
	}

	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
