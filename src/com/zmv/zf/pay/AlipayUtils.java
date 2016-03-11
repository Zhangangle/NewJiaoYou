package com.zmv.zf.pay;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.wbvideo.dm.R;
import com.zmv.zf.alipay.Result;
import com.zmv.zf.alipay.SignUtils;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.BasicUtils;

@SuppressLint("HandlerLeak")
public class AlipayUtils {
	// public static String PARTNER = "2088121135452422";
	// public static String SELLER = "zzsy@tonnn.com";
	// public static String PARTNER = "2088121686731993";
	// public static String SELLER = "3867096@qq.com";
	public static String PARTNER;// = "2088121604298942";
	public static String SELLER;// = "ttwl@tonnn.com";
	public static final int SDK_PAY_FLAG = 1;
	public Context context;
	public static final int SDK_CHECK_FLAG = 2;
	public Handler pay_handler;
	private static String TN_URL_01 = "http://sms.ejamad.com/interfaceAction";
	public static String RSA_PRIVATE;//
	private String orderId = "";
	String jsonString, notifyUrl, subject, price;

	public AlipayUtils() {
	}

	// kinds "pay"->支付会员 "buy"->购物
	public AlipayUtils(Context context, Handler handler) {
		this.context = context;
		this.pay_handler = handler;

	}

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 20160203:
				String tn = "";
				if (mDialog != null)
					mDialog.dismiss();
				if (msg.obj == null || ((String) msg.obj).length() == 0) {
					pay_handler.sendEmptyMessageDelayed(1, 0);
				} else {
					tn = (String) msg.obj;
					try {
						JSONObject jsonObject = new JSONObject(tn);
						PARTNER = jsonObject.getString("partner");
						SELLER = jsonObject.getString("seller");
						RSA_PRIVATE = jsonObject.getString("privateKey");
						notifyUrl = jsonObject.getString("notifyUrl");
						orderId = jsonObject.getString("out_trade_no");
						pay(orderId, subject, subject, price);
					} catch (JSONException e) {
						pay_handler.sendEmptyMessageDelayed(1, 0);
					}
				}
				break;
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				@SuppressWarnings("static-access")
				String resultStatus = resultObj.resultStatus;

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					pay_handler.sendEmptyMessageDelayed(0, 0);
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”
					// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						// ViewInject.toast("支付结果确认中");
					} else {
						// ViewInject.toast("支付失败");
					}
					pay_handler.sendEmptyMessageDelayed(1, 0);
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				// ViewInject.toast("检查结果为：" + msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};
	Dialog mDialog;

	public void setDialogView(Context context) {
		mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		mDialog.setContentView(R.layout.dialog_wait);
		mDialog.setCanceledOnTouchOutside(false);

		Animation anim = AnimationUtils.loadAnimation(context,
				R.anim.dialog_prog);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
		mDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if (mDialog != null)
						mDialog.dismiss();

					return false;
				}
				return false;
			}
		});
		mDialog.show();
	}

	public void payInfo(String subject, String body, String price) {
		orderId = getOutTradeNo();
		jsonString = "{\"operation\":\"2\",\"version\":\"1.0.0\",\"reqChannel\":{\"type\":3,\"appId\":\"1156\",\"extData\":\""
				+ Conf.CID
				+ "\",\"money\":\""
				+ price
				+ "00\",\"feeName\":\""
				+ subject + "\",\"orderId\":\"" + orderId + "\"}}";
		this.subject = subject;
		this.price = price;
		setDialogView(context);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String tn = null;
				InputStream is;
				try {
					String url = TN_URL_01;
					URL myURL = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) myURL
							.openConnection();
					conn.setRequestMethod("POST");
					conn.setConnectTimeout(15000);
					conn.connect();
					OutputStreamWriter writer = new OutputStreamWriter(conn
							.getOutputStream());
					// 发送参数
					writer.write(jsonString);
					writer.flush();

					is = conn.getInputStream();
					int i = -1;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					while ((i = is.read()) != -1) {
						baos.write(i);
					}
					tn = baos.toString();
					is.close();
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message msg = mHandler.obtainMessage();
				msg.obj = tn;
				msg.what = 20160203;
				mHandler.sendMessage(msg);

			}
		}).start();

	}

	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String tradeNo, String subject, String body, String price) {

		String orderInfo = getOrderInfo(tradeNo, subject, body, price);
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask((Activity) context);
				// 调用支付接口
				String result = alipay.pay(payInfo);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask payTask = new PayTask((Activity) context);
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask((Activity) context);
		String version = payTask.getVersion();
		Toast.makeText(context, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String tradeNo, String subject, String body,
			String price) {
		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + tradeNo + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径

		orderInfo += "&notify_url=" + "\"" + notifyUrl + "\"";
		// orderInfo += "&notify_url=" + "\""
		// + "http://t1.u1them.com/report_zfb/cc/" + Conf.CID + "\"";

		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"3m\"";

		return orderInfo;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
