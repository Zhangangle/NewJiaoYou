package com.zmv.zf.pay;

import java.util.HashMap;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhangzhifu.sdk.ZhangPayCallback;
import com.zhangzhifu.sdk.ZhangPaySdk;
import com.zmv.zf.common.Conf;

public class ZhangPayUtils {
	public static ZhangPayUtils zhangPayUtils;
	private static Activity context;
	private static Handler handler;
	private String pointtype;
	private static PackageInfo packageInfo;
	public static boolean flag_pay = true;

	public static ZhangPayUtils getInstance(Activity c, Handler h) {
		if (zhangPayUtils == null) {
			zhangPayUtils = new ZhangPayUtils();
			// 掌游支付初始化
		}
		context = c;
		handler = h;

		return zhangPayUtils;
	}

	public HashMap<String, String> getPayMap() {
		HashMap map = new HashMap<String, String>();
		map.put("channelId", Conf.ZHANGZHIFU_CHANNELID);
		map.put("key", Conf.ZHANGZHIFU_APPKEY);
		map.put("money", "2000");
		map.put("appId", Conf.ZHANGZHIFU_APPID);
		map.put("qd", Conf.ZHANGZHIFU_APPKQD);
		return map;
	}

	public void ZhangPay(String pointype, String point, String trade,
			String pointDec) {
		String cp = Conf.UID + "," + Conf.CID + "," + pointype;
		this.pointtype = pointype;
		final HashMap map = getPayMap();
		map.put("channelId", Conf.ZHANGZHIFU_CHANNELID);
		map.put("key", Conf.ZHANGZHIFU_APPKEY);
		map.put("appId", Conf.ZHANGZHIFU_APPID);
		map.put("appName", Conf.ZHANGZHIFU_APPNAME);
		map.put("qd", Conf.ZHANGZHIFU_APPKQD);
		map.put("cpparam", cp);
		map.put("priciePointId", point);
		map.put("appVersion", Conf.ZHANGYOU_APPVERSION);
		map.put("money", "1000");
		map.put("priciePointDec", pointDec);
		map.put("priciePointName", trade);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ZhangPaySdk.getInstance().pay(context, map, new payCallBack(),
						true);
			}
		}, 1500);
	}

	class payCallBack implements ZhangPayCallback {

		@Override
		public void onZhangPayBuyProductFaild(String arg0, String arg1) {
			// TODO Auto-generated method stub
			if (flag_pay) {
//				Log.e("掌支付支付失败", "计费ID" + arg0 + "--响应码" + arg1);
				Message msg = Message.obtain();
				msg.arg1 = Integer.parseInt(arg1);
				if (pointtype != null && pointtype.equals("warning")) {
					msg.what = 1002;
					handler.sendMessage(msg);
				} else if (pointtype != null && pointtype.equals("libao")) {
					msg.what = 2002;
					handler.sendMessage(msg);

				} else if (pointtype != null && pointtype.equals("shipin")) {
					msg.what = 3002;
					handler.sendMessage(msg);

				}
			}
		}

		@Override
		public void onZhangPayBuyProductOK(String arg0, String arg1) {
			// TODO Auto-generated method stub
			if (flag_pay) {
//				Log.e("掌支付支付成功", "计费ID" + arg0 + "--响应码" + arg1);
				Message msg = new Message();
				msg.what = Integer.parseInt(arg1);
				if (pointtype != null && pointtype.equals("warning")) {
					handler.sendMessage(msg);

				} else if (pointtype != null && pointtype.equals("libao")) {
					handler.sendMessage(msg);
				} else if (pointtype != null && pointtype.equals("shipin")) {
					handler.sendMessage(msg);
				}
			}

		}

	};
}
