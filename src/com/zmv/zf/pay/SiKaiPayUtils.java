package com.zmv.zf.pay;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.wsfg.zd.R;
import com.skymobi.pay.sdk.normal.zimon.EpsEntry;
import com.skymobi.pay.sdk.normal.zimon.util.SkyPaySignerInfo;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.BasicUtils;
//import com.skymobi.pay.sdk.normal.zimon.EpsEntry;
//import com.skymobi.pay.sdk.normal.zimon.util.SkyPaySignerInfo;

public class SiKaiPayUtils {

	private static final String ORDER_INFO_PAY_METHOD = "payMethod";
	private static final String ORDER_INFO_SYSTEM_ID = "systemId";
	private static final String ORDER_INFO_CHANNEL_ID = "channelId";
	private static final String ORDER_INFO_PAY_POINT_NUM = "payPointNum";
	private static final String ORDER_INFO_ORDER_DESC = "orderDesc";
	private static final String ORDER_INFO_PRODUCT_NAME = "productName";
	private static final String ORDER_INFO_GAME_TYPE = "gameType";

	public static final String STRING_MSG_CODE = "msg_code";
	public static final String STRING_ERROR_CODE = "error_code";
	public static final String STRING_PAY_STATUS = "pay_status";
	public static final String STRING_PAY_PRICE = "pay_price";
	public static final String STRING_CHARGE_STATUS = "3rdpay_status";

	private static final String ORDER_INFO_MERCHANT_ID = "merchantId";
	private static final String ORDER_INFO_APP_ID = "appId";
	private static final String ORDER_INFO_APP_NAME = "appName";
	private static final String ORDER_INFO_APP_VER = "appVersion";
	private static final String ORDER_INFO_PAY_TYPE = "payType";
	private static final String ORDER_INFO_ACCOUNT = "appUserAccount";
	private static final String ORDER_INFO_PRICENOTIFYADDRESS = "priceNotifyAddress";

	static String merchantPasswd = "435346578756823465352357";// 商户密钥
	private static EpsEntry mEpsEntry = null;
	static Handler handler;

	public static void pay(Activity activity, String payPointNum, String price,
			String cp, Handler handler, boolean useAppUi) {
		SiKaiPayUtils.handler = handler;
		String merchantId = "16572";// 商户号
		String appId = "7011047";// appid
		// 1.获取付费实例并初始化
		mEpsEntry = EpsEntry.getInstance();

		// 配置文件
		String payMethod = "sms";// 支付类型
		/*
		 * 生成订单号 CP也可按照自己的规则生成订单号
		 */
		String orderId = SystemClock.elapsedRealtime() + "";
		String appName = activity.getString(R.string.app_name); // 游戏名称
		String appVersion = String.valueOf(BasicUtils.getAppVersion(activity)); // 游戏版本号

		String systemId = "300024";// 系统号

		String channelId = Conf.CID;// 渠道号

		// 计费类型： 0=注册 1=道具 2=积分 3=充值，50=网游小额支付（如果不填，默认是道具）
		String payType = "1";
		String gameType = "0"; // 0-单机、1-联网、2-弱联网
		/*
		 * orderDesc里必须把价格写成N.NN 不需要写成数字。在显示时会自动被price替换掉
		 * 如果有SkyPayInfo.mxl文件，则读取该文件中payPointNum引用的编号的描述
		 */
		String orderDesc = "流畅的操作体验，劲爆的超控性能，无与伦比的超级必杀，化身斩妖除魔的英雄，开启你不平凡的游戏人生！！需花费N.NN元。";

		String notifyAddress = "http://api2.347.cc/pay/zhim/callback";
		String reserved1 = Conf.UID + "," + Conf.CID + "," + cp;
		String reserved2 = "";
		String reserved3 = "";

		/*
		 * 生成签名：
		 * 
		 * 实像化SkyPaySignerInfo对象，并将相关参数注入
		 * 
		 * 调用mSkyPayServer的getSignOrderString方法，生成签名
		 */
		SkyPaySignerInfo skyPaySignerInfo = new SkyPaySignerInfo();

		skyPaySignerInfo.setMerchantPasswd(merchantPasswd);
		skyPaySignerInfo.setMerchantId(merchantId);
		skyPaySignerInfo.setAppId(appId);
		skyPaySignerInfo.setAppName(appName);
		skyPaySignerInfo.setAppVersion(appVersion);
		skyPaySignerInfo.setPayType(payType);
		skyPaySignerInfo.setPrice(price);
		skyPaySignerInfo.setOrderId(orderId);

		// 注解B 如果配置了服务端，则取消如下四条注解

		skyPaySignerInfo.setNotifyAddress(notifyAddress);
		skyPaySignerInfo.setReserved1(reserved1, false);
		skyPaySignerInfo.setReserved2(reserved2, false);
		skyPaySignerInfo.setReserved3(reserved3, true);

		String signOrderInfo = skyPaySignerInfo.getOrderString();

		/*
		 * 组装相关参数
		 */
		String mOrderInfo = ORDER_INFO_PAY_METHOD + "=" + payMethod + "&"
				+ ORDER_INFO_SYSTEM_ID + "=" + systemId + "&"
				+ ORDER_INFO_CHANNEL_ID + "=" + channelId + "&"
				+ ORDER_INFO_PAY_POINT_NUM + "=" + payPointNum + "&"
				+ ORDER_INFO_GAME_TYPE + "=" + gameType + "&"
				+ ORDER_INFO_ORDER_DESC + "=" + orderDesc + "&"
				+ "order_skipResult=" + "true" + "&" + "order_skipTip="
				+ "true" + "&"
				// 注解C 如果配置了服务端，则取消该条注解
				// + ORDER_INFO_PRICENOTIFYADDRESS + "=" + notifyAddress + "&"
				+ "useAppUI=" + useAppUi + "&" + signOrderInfo;

		if (cp.equals("warning")) {
			/** 警告包月计费点 **/
			mOrderInfo += "&appUserAccount=" + Conf.CID;// 可传任意值
		}

		// 开始计费
		int payRet = mEpsEntry.startPay(activity, mOrderInfo, mPayHandler);
		if (EpsEntry.PAY_RETURN_SUCCESS == payRet) {
			// 初始化成功
			// Log.e("斯凯初始化成功", "!!!!");
		} else {
			// 未初始化 \ 传入参数有误 \ 服务正处于付费状态
			// Log.e("斯凯初始化失败", "!!!!");
		}
	}

	private static Handler mPayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == EpsEntry.MSG_WHAT_TO_APP) {
				String retInfo = (String) msg.obj;
				Map<String, String> map = new HashMap<String, String>();

				// mActivity.refreshResult(retInfo);

				String[] keyValues = retInfo.split("&|=");
				for (int i = 0; i < keyValues.length; i = i + 2) {
					map.put(keyValues[i], keyValues[i + 1]);
				}

				int msgCode = Integer.parseInt(map.get(STRING_MSG_CODE));
				// 解析付费状态和已付费价格
				// 使用其中一种方式请删掉另外一种
				if (msgCode == 100) {

					// 短信付费返回
					if (map.get(STRING_PAY_STATUS) != null) {
						int payStatus = Integer.parseInt(map
								.get(STRING_PAY_STATUS));
						int payPrice = Integer.parseInt(map
								.get(STRING_PAY_PRICE));
						int errcrCode = 0;
						if (map.get(STRING_ERROR_CODE) != null) {
							errcrCode = Integer.parseInt(map
									.get(STRING_ERROR_CODE));
						}

						switch (payStatus) {
						case 102:
							// Toast.makeText(mActivity,
							// "付费成功" + payPrice / 100 + "元",
							// Toast.LENGTH_LONG).show();
							handler.sendEmptyMessage(102);
							break;
						case 101:
							// Toast.makeText(mActivity, "付费失败！原因：" + errcrCode,
							// Toast.LENGTH_LONG).show();
							Message message = Message.obtain();
							message.what = 101;
							message.arg1 = errcrCode;
							handler.sendMessageDelayed(message, 0);
							break;
						}
					}
				} else {
					// 解析错误码
					int errcrCode = Integer
							.parseInt(map.get(STRING_ERROR_CODE));
					Message message = Message.obtain();
					message.what = 101;
					message.arg1 = errcrCode;
					handler.sendMessageDelayed(message, 0);
				}
			}
		}
	};

}