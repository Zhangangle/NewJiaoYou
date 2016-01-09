package com.zmv.zf.pay;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hft.wechat.api.HftWeChatPay;
import com.hft.wechat.api.OnPayFinishedListener;
import com.ipaynow.plugin.api.IpaynowPlugin;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.LogUtils;

public class WXPayUtils {

	public static WXPayUtils wxPayUtils;
	private static Context context;
	private static Handler handler;
	/**
	 * 商户名称： pay2217 商 户 号： 154171 密钥： 3b45e79e5700f2d0257c3f1b9466b405
	 */
	/**
	 * 商户名称： pay2218 商 户 号： 154186 密钥： ee9f661f21a7aed6b571c3523e91c211
	 */

	// private static Activity act = null;
	// private static ProgressDialog progressDialog = null;
	// 商户秘钥
	// private static final String key = "130edc2f0de37c26007c7c2c62838ca3";
	// private static final String key = "ee9f661f21a7aed6b571c3523e91c211";
	// 商户号
	// private static final String consumerId = "151503";
	// private static final String consumerId = "154186";
	// 服务器回传地址
	// private static final String notifyUrl = "http://123.59.40.34/report";
	// private static final String notifyUrl = "http://t1.u1them.com/report";
	private static final String notifyUrl = "http://t2.u1them.com/hft_report/cc/"
			+ Conf.CID;

	public static WXPayUtils getInstance(Context c, Handler h) {
		if (wxPayUtils == null) {
			wxPayUtils = new WXPayUtils();
		}

		context = c;
		handler = h;

		return wxPayUtils;
	}

	public static void goToPay(String title, String amt) {
		try {

			HashMap<String, String> params = new HashMap<String, String>();

			String mStrOrderId = getToSeconds();//getAppKey(context) + 
			params.put("order_id", mStrOrderId+Conf.CID); // 订单号
			params.put("pay_amt", amt); // 支付金额
			params.put("notify_url", notifyUrl); // 通知地址
			params.put("goods_name", title); // 商品名称
			params.put("goods_note", title); // 商品价格信息 // 可为空
			params.put("extends_info", Conf.CID); // 标记
			params.put("goods_num", "1"); // 商品数量
			HftWeChatPay.getInstance().pay(context, params,
					new OnPayFinishedListener() {

						@Override
						public void onPaySuccess(Map params) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessageDelayed(2, 0);
							// 订单号
							// String mStrOrderId = ((Map<String, String>)
							// params)
							// .get("order_id");
							// // 支付总金额
							// String mStrTotalAmount = ((Map<String, String>)
							// params)
							// .get("total_amt");
							// // 商品名称
							// String mStrGoodsName = ((Map<String, String>)
							// params)
							// .get("goods_name");
							// // 商品说明
							// String mStrGoodsNote = ((Map<String, String>)
							// params)
							// .get("goods_note");
							// // 商品数量
							// String mStrGoodsNum = ((Map<String, String>)
							// params)
							// .get("goods_num");
							// // 扩展信息
							// String mStrExtendsInfo = ((Map<String, String>)
							// params)
							// .get("extends_info");
							// // 支付结果说明
							// String mStrResultMes = ((Map<String, String>)
							// params)
							// .get("result_message");
							LogUtils.printLogE("发送成功",
									"params=" + params.toString());

						}

						@Override
						public void onPayFail(Map params, int errorInt) {
							handler.sendEmptyMessageDelayed(3, 0);
							LogUtils.printLogE("发送失败",
									"params=" + params.toString());
						}

						@Override
						public void onPayCancel(Map params) {
							handler.sendEmptyMessageDelayed(3, 0);
							LogUtils.printLogE("发送取消",
									"params=" + params.toString());
						}

					});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/** 获取App_Key */
	public static String getAppKey(Context context) {
		return getStringMetaData(context, "HFT_APP_KEY");
	}

	private static String getStringMetaData(Context context, String key) {
		Bundle metaData = getMetaData(context);
		String strVal = metaData != null ? metaData.getString(key) : null;
		return strVal != null ? strVal : "";
	}

	private static Bundle getMetaData(Context context) {
		if (context == null) {
			return null;
		}

		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo appInfo = pm.getApplicationInfo(
					context.getPackageName(), 128);

			if (appInfo != null)
				return appInfo.metaData;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	/** 获取当前完整时间：20141230114605 */
	public static String getToSeconds() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String mStrNowTime = sdf.format(new java.util.Date());
		LogUtils.printLogE("eee", "当前时间：" + mStrNowTime);
		return mStrNowTime;
	}

}
