package com.zmv.zf.pay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.os.Handler;

import com.ehoo.app.ImageInfo;
import com.ehoo.app.OnInitListener;
import com.ehoo.app.OnPayListener;
import com.ehoo.app.Pay;
import com.ehoo.app.PayOption;
import com.ehoo.app.PaySDK;
import com.ehoo.app.ResultBean;
import com.zmv.zf.utils.LogUtils;

public class CkyfPayUtils {

	static CkyfPayUtils ckyfPayUtils;
	private static Activity context;
	private static Handler handler;

	public static CkyfPayUtils getInstance(Activity c, Handler h) {
		if (ckyfPayUtils == null) {
			ckyfPayUtils = new CkyfPayUtils();
		}
		context = c;
		handler = h;

		return ckyfPayUtils;
	}

	/**
	 * 生成订单号
	 */
	public String genOrderId() {
		String from = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		int index = 0;
		String orderId = "";
		for (int i = 0; i < 20; i++) {
			index = random.nextInt(from.length());
			orderId += from.charAt(index);
		}
		return orderId;
	}

	public void initPay() {
		PaySDK.setMerID("2013"); // 设置商户 NCID (merid)
		PaySDK.setOpenAppID("1004"); // 设置应用编号(appid)
		PaySDK.init(context, new OnInitListener() { // 调用初始化
					@Override
					public void onInitResult(String initResult) {
						if ("0000".equals(initResult)) {
							// 初始化成功
							LogUtils.printLogE("彩客易付", "初始化成功");
						} else {
							// 初始化失败
							LogUtils.printLogE("彩客易付", "初始化失败");
						}
					}
				});
	}

	public void ckyfPay(final String type, String point) {
		Pay pay = new Pay(context);

		/* 新建支付参数 */
		PayOption payOption = new PayOption();

		/* 设置计费点 */
		payOption.setOpenChargePoint(point);

		/* 设置订单日期。格式：“2013-11-29” */
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		payOption.setOrderDate(format.format(new Date()));

		/*
		 * 设置订单号。应用自主编订，对应单次交易。 最大长度 20。只能是字母或数字，例：“134abcABC”
		 */
		payOption.setOrderID(genOrderId());

		/*
		 * 如需定制图片资费提示，请在这里添加相关代码，详见 2.3（推荐使用这项定制，可提高计 费转化率）
		 */
		if (type.equals("warning")) {
			ImageInfo imageInfo = new ImageInfo(
					"assets:/ckyf/ckyf_warning.png",
					"assets:/ckyf/ckyf_ok1.png", 130, 420,
					"assets:/ckyf/ckyf_close.png", 440, 10, 0.8);
			payOption.setVipImageInfos(imageInfo, imageInfo);
		} else if (type.equals("libao")) {
			ImageInfo imageInfo = new ImageInfo("assets:/ckyf/ckyf_libao.png",
					"assets:/ckyf/ckyf_ok.png", 110, 400,
					"assets:/ckyf/ckyf_close.png", 440, 10, 0.8);
			payOption.setVipImageInfos(imageInfo, imageInfo);
		} else if (type.equals("shipin")) {
			ImageInfo imageInfo = new ImageInfo("assets:/ckyf/ckyf_shipin.png",
					"assets:/ckyf/ckyf_ok.png", 110, 400,
					"assets:/ckyf/ckyf_close.png", 440, 10, 0.8);
			payOption.setVipImageInfos(imageInfo, imageInfo);
		}

		/* 填入支付参数到 pay */
		pay.setPayOptions(payOption);

		/* 设置支付结果监听 */
		pay.setOnPayListener(new OnPayListener() {
			public boolean onPostPayResult(ResultBean result) {
				if (result != null && result.isSuccess()) {
					// 支付成功（联网游戏不以此作为判断依据，请以服务器回调为准）
					LogUtils.printLogE("彩客易付支付成功", result.getCode() + "\n"
							+ result.getType() + "\n" + result.getMessage());
					if (type.equals("warning")) {
						handler.sendEmptyMessage(1005);
					} else if (type.equals("libao")) {
						handler.sendEmptyMessage(2005);
					} else if (type.equals("shipin")) {
						handler.sendEmptyMessage(3005);
					}
				} else {
					// 支付失败
					LogUtils.printLogE("彩客易付支付失败", result.getCode() + "\n"
							+ result.getDetailCode() + "\n" + result.getType()
							+ "\n" + result.getMessage());
					if (type.equals("warning")) {
						handler.sendEmptyMessage(1006);
					} else if (type.equals("libao")) {
						handler.sendEmptyMessage(2006);
					} else if (type.equals("shipin")) {
						handler.sendEmptyMessage(3006);
					}
				}
				/*
				 * 返回 true 拦截 sdk 内部结果提示， 返回 false 显示 sdk 内部结果提示，建议返回 false
				 */
				return true;
			}
		});
		/* 启动支付 */
		pay.start();
	}
}
