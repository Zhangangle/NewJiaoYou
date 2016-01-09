package com.zmv.zf.pay;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;

import com.dm.ml.MiLiNewApi;
import com.yl.ml.listen.PCallback;
import com.zmv.zf.utils.LogUtils;

public class MiLiPayUtils {
	public static MiLiPayUtils miliPayUtils;
	private static Activity context;
	private static Handler handler;
	private String pointtype;
	private static PackageInfo packageInfo;
	public static boolean flag_pay = true;

	public static MiLiPayUtils getInstance(Activity c, Handler h) {
		if (miliPayUtils == null) {
			miliPayUtils = new MiLiPayUtils();
			// 掌游支付初始化
		}
		context = c;
		handler = h;

		return miliPayUtils;
	}

	public void MiLiPay(final String payType, String payId, int price) {
		// orderNum为订单号，为了保证唯一性，各位开发者最好在后台生成唯一性的订单号。不要在前端生成。实在不想用这个功能，可以把orderNum设置为null
		String orderNum = "Only_" + System.currentTimeMillis();
		// 第一个参数为计费项ID，第2是为你们传入的订单号，第三个为计费项价格（单位为元）。
		// Pay方法必须在主线程和UI线程中调用。不支持在普通线程中调用
		MiLiNewApi.Pay(context, new PCallback() {
			@Override
			public void payEnd(int payResult) {
				if (flag_pay) {
					LogUtils.printLogE("米粒支付", payResult + "");
					Message msg = Message.obtain();
					// Toast.makeText(context, "错误码" + payResult,
					// Toast.LENGTH_LONG).show();
					if (payResult == 9000) {
						// 成功 在这里加道具
						if (payType.equals("warning"))
							msg.what = 1003;
						else if (payType.equals("libao"))
							msg.what = 2003;
						else
							msg.what = 3003;
						handler.sendMessage(msg);
					} else {
						if (payType.equals("warning"))
							msg.what = 1004;
						else if (payType.equals("libao"))
							msg.what = 2004;
						else
							msg.what = 3004;
						handler.sendMessage(msg);
						// 支付失败，开发者看这里的错误代码对照文档来咨询
						// // 支付失败种类很多。请谨参考开发文档payResult
						// Toast.makeText(context, "错误码" + payResult,
						// Toast.LENGTH_LONG).show();
					}
				}
				// 这里开发者可以自己进行统计，把订单号带上。方便查询。
			}
		}, payId, orderNum, price);
	}

}
