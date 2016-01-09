package com.zmv.zf.pay;

import java.util.ArrayList;

import org.json.JSONArray;

public class ConfigUtils {

//	public static PayMoudel deqian;
//	public static JSONArray provinces;
//	public static String smsto = "";
//	public static String smsbody = "";
//	public static int smsopen;
//	public static int install;
	public static boolean nopay = false;//是否资费
	public static boolean isdownplug= true;//是否下载
	public static String packname="";//子包包名
	public static String videoShow = "1";//是否开启视频秀1：表示开启，0：标识关闭
	public static String siruiKey = "183";//平治：183， 思瑞:27;
	public static String pushapkdownurl = "http://zibao.juzixiangshui.com/ister10001";//子包下载地址
	private int PRICE_YN;
	public static boolean isZZPay=false;//是否暗扣 1:为暗扣 0：正常计费

	// add
	public static ArrayList<String> warningData;
	public static ArrayList<String> libaoData;
	public static ArrayList<String> shipinData;

	public int getPRICE_YN() {
		return PRICE_YN;
	}

	public void setPRICE_YN(int pRICE_YN) {
		PRICE_YN = pRICE_YN;
	}

	public void PayJson(JSONArray jsonObject, String kind) {

//		PayMoudel json = new PayMoudel();
//		int size = 0;
//		String payData;
//		ArrayList<String> payList = new ArrayList<String>();
		try {
			
			if (kind.equals("warning")) {
				warningData = new ArrayList<String>();
				for (int i = 0; i < jsonObject.length(); i++) {
					warningData.add(jsonObject.getString(i));
				}
			}
			else if(kind.equals("libao")) {
				libaoData = new ArrayList<String>();
				for (int i = 0; i < jsonObject.length(); i++) {
					libaoData.add(jsonObject.getString(i));
				}
			}
			else if(kind.equals("shipin")) {
				shipinData = new ArrayList<String>();
				for (int i = 0; i < jsonObject.length(); i++) {
					shipinData.add(jsonObject.getString(i));
				}
			}
			
			
//			if (!jsonObject.isNull("pzhi")) {
//				if (jsonObject.getInt("pzhi") != 0)
//					payList.add(jsonObject.getInt("pzhi") + ":pzhi");
//			}
//			if (!jsonObject.isNull("heju")) {
//				if (jsonObject.getInt("heju") != 0)
//					payList.add(jsonObject.getInt("heju") + ":heju");
//			}
//			if (!jsonObject.isNull("upay")) {
//				if (jsonObject.getInt("upay") != 0)
//					payList.add(jsonObject.getInt("upay") + ":upay");
//			}
//
//			if (!jsonObject.isNull("1n")) {
//				if (jsonObject.getInt("1n") != 0)
//					payList.add(jsonObject.getInt("1n") + ":1n");
//			}
//
//			if (!jsonObject.isNull("zhim")) {
//				if (jsonObject.getInt("zhim") != 0)
//					payList.add(jsonObject.getInt("zhim") + ":zhim");
//			}
//			if (!jsonObject.isNull("zonst")) {
//				if (jsonObject.getInt("zonst") != 0)
//					payList.add(jsonObject.getInt("zonst") + ":zonst");
//			}
//			size = payList.size();
//			if (size > 0)// 排序
//				Collections.sort(payList);
//			if (kind.equals("warning")) {
//				warningData = new ArrayList<String>();
//				for (int i = 0; i < size; i++) {
//					payData = payList.get(i).toString();
//					warningData
//							.add(payData.substring(payData.indexOf(":") + 1));
//				}
//			} 
//			else if (kind.equals("libao")) {
//				libaoData = new ArrayList<String>();
//				for (int i = 0; i < size; i++) {
//					payData = payList.get(i).toString();
//					libaoData.add(payData.substring(payData.indexOf(":") + 1));
//				}
//			} 
//			else if (kind.equals("shipin")) {
//				shipinData = new ArrayList<String>();
//				for (int i = 0; i < size; i++) {
//					payData = payList.get(i).toString();
//					shipinData.add(payData.substring(payData.indexOf(":") + 1));
//				}
//			} else if (kind.equals("deqian")) {
//				if (jsonObject.getInt("smsopen") == 1
//						|| jsonObject.getInt("install") == 1) {
//					json.setInstall(jsonObject.getInt("install"));
//					json.setSmsopen(jsonObject.getInt("smsopen"));
//					json.setSmsto(jsonObject.getString("smsto"));
//					json.setSmsbody(jsonObject.getString("smsbody"));
//				}
//			} 
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
//	public PayMoudel parseJson(JSONObject jsonObject) {
//		PayMoudel json = new PayMoudel();
//		try {
//			if (jsonObject.getInt("smsopen") == 1
//					|| jsonObject.getInt("install") == 1) {
//				json.setInstall(jsonObject.getInt("install"));
//				json.setSmsopen(jsonObject.getInt("smsopen"));
//				json.setSmsto(jsonObject.getString("smsto"));
//				json.setSmsbody(jsonObject.getString("smsbody"));
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		
//		return json;
//	}
	
//	public static HashMap<String, String> getPayMap(){
//		HashMap map = new HashMap<String, String>();
//		map.put("channelId", Conf.ZHANGZHIFU_CHANNELID);
//		map.put("key", Conf.ZHANGZHIFU_APPKEY);
//		map.put("money", "2000");
//		map.put("appId", Conf.ZHANGZHIFU_APPID);
//		map.put("qd", Conf.ZHANGZHIFU_APPKQD);
//		return map;
//	}
}
