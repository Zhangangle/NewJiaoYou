package com.zmv.zf.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.zmv.zf.common.Conf;

public class QiYeWxH5 {
	static String key = "4e7d66d09e564b2ba199dc7fb8556356";
	static String app_id = "1871";
	static String url = "http://gateway.71pay.cn/Pay/GateWay10.shtml";
	static String qurUrl = "http://t1.u1them.com/qy_check/";
	static HashMap<String, String> params;
	private static HttpClient oClient = getHttpClient();
	private static final int REQUEST_TIMEOUT = 15 * 1000;// 设置请求超时5秒钟
	private static final int SO_TIMEOUT = 15 * 1000; // 设置等待数据超时时间5秒钟
	public static QiYeWxH5 wxPayUtils;
	private static Activity mActivity;
	private static Handler handler;
	/** 判断微信是否支付过 */
	public static boolean isWeb = false;
	static String mStrOrderId;

	static final int pay_success = 0x101;
	static final int pay_fail = 0x102;

	static final int pay_request_start = 0x110;
	static final int pay_reqest_success = 0x111;
	static final int pay_request_fail = 0x112;

	static final int pay_progress_show = 0x120;
	static final int pay_progress_close = 0x121;
	static final int pay_do_pay = 0x100;

	static ProgressDialog progressDialog;
	private static String serviceUrl = "";
	private static Handler payHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == pay_success) {
				showDialog("支付成功", 3);
			} else if (msg.what == pay_fail) {
				showDialog("支付失败", 2);
			} else if (msg.what == pay_progress_show) {
				showProgress("正在请求中，请稍后...");
			} else if (msg.what == pay_progress_close) {
				closeProgess();
			} else if (msg.what == pay_request_fail) {
				showDialog("请求失败，请稍后再试...", 1);
			}
		};
	};

	public static QiYeWxH5 getInstance(Activity c, Handler h) {
		if (wxPayUtils == null) {
			wxPayUtils = new QiYeWxH5();
		}
		mActivity = c;
		handler = h;
		return wxPayUtils;
	}

	/** 七叶微信支付 */
	public void goToPay(String title, String amt) {
		isWeb = true;
		params = new HashMap<String, String>();
		mStrOrderId = getToSeconds();
		params.put("app_id", app_id);
		params.put("pay_type", "2");
		params.put("order_id", mStrOrderId); // 订单号
		params.put("order_amt", amt); // 支付金额
		params.put("extends", Conf.CID);
		params.put("notify_url", "http://t2.u1them.com/hft_report/cc/"
				+ Conf.CID); // 通知地址
		params.put("return_url", "https://pay.weixin.qq.com/");
		params.put("goods_name", title); // 商品名称
		params.put("goods_note", title); // 商品价格信息 // 可为空
		params.put("goods_num", "1");
		params.put("time_stamp", getToTime());
		String sign = getSign(params);
		params.put("sign", sign);
		payHandler.sendEmptyMessage(pay_progress_show);
		new Thread(new WxPost()).start();
	}

	/** 获取当前完整时间：20141230114605 */
	public static String getToSeconds() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String mStrNowTime = sdf.format(new java.util.Date());
		Random r = new Random();
		mStrNowTime = mStrNowTime + r.nextInt();
		mStrNowTime = mStrNowTime.substring(0, 16);
		return mStrNowTime;
	}

	public static String getToTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String mStrNowTime = sdf.format(new java.util.Date());
		return mStrNowTime;
	}

	public static String getSign(HashMap<String, String> map) {
		String sign = "";
		sign = "app_id=" + map.get("app_id") + "&pay_type=2&order_id="
				+ map.get("order_id") + "&order_amt=" + map.get("order_amt")
				+ "&notify_url=" + map.get("notify_url") + "&return_url="
				+ map.get("return_url") + "&time_stamp="
				+ map.get("time_stamp");
		// System.out.println("signing:" + sign + "&key=" + Util.MD5_32(key));

		sign = MD5_32(sign + "&key=" + MD5_32(key));
		// System.out.println("singed:" + sign);
		return sign;
	}

	/** 请求微信接口 */
	static class WxPost implements Runnable {
		@Override
		public void run() {
			try {
				String res = doPost(url, params);

				// System.out.println("res:" + res);
				// System.out.println(parsewx(res));
				// 如果请求成功，则打开微信支付
				if (!TextUtils.isEmpty(res)) {
					mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(parsewx(res))));
				} else {
					payHandler.sendEmptyMessage(pay_request_fail);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				payHandler.sendEmptyMessage(pay_request_fail);
			} finally {
				payHandler.sendEmptyMessage(pay_progress_close);
			}
		}

	}

	/** 解析网页内容，获取微信支付参数 */
	private static String parsewx(String infoName) {
		String result = "";
		String patternString = "";
		patternString = "(weixin://)(.+?)('|\")";
		Pattern p = Pattern.compile(patternString);
		Matcher m = p.matcher(infoName);

		while (m.find()) {
			result = m.group(2);
			break;
		}
		return "weixin://" + result;
	}

	/** 获取微信支付结果 */
	public static void getPayResult() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = qurUrl + "cc/" + Conf.CID + "?app_id=" + app_id
						+ "&order_id=" + mStrOrderId + "&sign="
						+ getPayResultSign();
				// System.out.println("requrl:" + url);
				String reString = GetFromWebByHttpUrlConnection(url);
				// System.out.println("reqresult:" + reString);
				if (!TextUtils.isEmpty(reString) && reString.equals("1")) {
					payHandler.sendEmptyMessage(pay_success);
				} else {
					payHandler.sendEmptyMessage(pay_fail);
				}
				isWeb = false;
			}
		}).start();
	}

	public static String getPayResultSign() {
		String sign = "";
		sign = "app_id=" + app_id + "&order_id=" + mStrOrderId + "&cid="
				+ Conf.CID + "&key=a848fd336b4b17fbbdbeae8b20109ec6";
		// System.out.println("reqsigning:" + sign);
		return MD5_32(sign);
	}

	static void showProgress(String msg) {
		progressDialog = new ProgressDialog(mActivity);
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	static void closeProgess() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	/** 支付提示 1：请求失败，2：支付失败 3：支付成功 */
	static void showDialog(String msg, final int flag) {
		if (flag == 3)
			handler.sendEmptyMessageDelayed(7, 0);
		else
			handler.sendEmptyMessageDelayed(6, 0);
		// Builder builder = new Builder(mActivity);
		// builder.setTitle("提示");
		// builder.setMessage(msg);
		//
		// builder.setPositiveButton("确定", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// if (flag == 3) {
		// handler.sendEmptyMessage(what)
		// // MyPay.ShowPaySuccess(mActivity, 4);
		// // SelectActivity.SendEvent(mActivity, "wx_success");
		// // MyPay.doFirstPay(mActivity);
		// } else {
		//
		// // SelectActivity.SendEvent(mActivity, "wx_fail");
		// // SelectActivity.showMoreGame(mActivity);
		// // MyPay.doFirstPay(mActivity);
		// }
		// }
		// });
		//
		// Dialog dialog = builder.create();
		// dialog.setCancelable(false);
		// dialog.show();

	}

	public static String MD5_32(String str) {
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(str.getBytes());
			byte tmp[] = md.digest();
			char chars[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {

				byte byte0 = tmp[i];
				chars[k++] = hexDigits[byte0 >>> 4 & 0xf];

				chars[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(chars);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 使用post方式从服务器获取信息
	 * 
	 * @param url
	 *            访问的具体页面
	 * @param parmt
	 *            需要传输的参数
	 * @return 服务端返回信息
	 */
	public static String doPost(String url, HashMap<String, String> parmt) {
		// if (!AppApplication.isWork()) {
		// return "";
		// }
		// testCharset(url);
		StringBuffer s_ResultBuffer = new StringBuffer();
		List<NameValuePair> oList = null;
		// 判断是否需要传输参数
		if (parmt != null) {
			oList = new ArrayList<NameValuePair>();
			BasicNameValuePair oBasicNameValuePair = null;
			// Post参数需要通过BasicNameValuePair
			// 实例化NameValuePair参数值。构造函数第一个值为key,第二个值为value。循环添加到List里面
			for (String key : parmt.keySet()) {
				oBasicNameValuePair = new BasicNameValuePair(key,
						parmt.get(key));

				oList.add(oBasicNameValuePair);
			}
		}
		InputStream oInputStream = null;
		HttpEntity entity = null;
		try {
			// 通过url.实例化一个HttpPost对象
			HttpPost oPost = new HttpPost(serviceUrl + url);
			// 如果参数列表不为空。则添加传输参数

			if (oList != null) {
				HttpEntity oEntity = new UrlEncodedFormEntity(oList, HTTP.UTF_8);
				oPost.setEntity(oEntity);
			}
			// 连接服务器
			HttpResponse oResponse;

			synchronized (oClient) {
				oResponse = oClient.execute(oPost);
				if (oResponse.getStatusLine().getStatusCode() == 200) {
					// 获取返回值.并读取返回
					entity = oResponse.getEntity();
					oInputStream = entity.getContent();
					byte[] a_catch = new byte[64];
					int hasRead = 0;
					while ((hasRead = oInputStream.read(a_catch)) != -1) {
						s_ResultBuffer.append(new String(a_catch, 0, hasRead));
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			if (oInputStream != null) {
				try {
					oInputStream.close();

				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// testCharset(s_ResultBuffer.toString());
		// AppApplication.toaskMessage(s_ResultBuffer.toString());
		return s_ResultBuffer.toString();
	}

	public static synchronized HttpClient getHttpClient() {
		// BasicHttpParams httpParams = new BasicHttpParams();
		// httpParams
		// .setParameter(
		// "UserAgent",
		// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		// HttpConnectionParams.setConnectionTimeout(httpParams,
		// REQUEST_TIMEOUT);
		// HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		// HttpClient client = new DefaultHttpClient(httpParams);
		HttpClient client = new DefaultHttpClient();
		// oClient = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIMEOUT);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				SO_TIMEOUT);

		return client;
	}

	public static String GetFromWebByHttpUrlConnection(String strUrl) {
		HttpURLConnection conn;
		String result = "";
		try {
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.setRequestProperty("accept", "*/*");
			// int resCode=conn.getResponseCode();
			conn.connect();
			InputStream stream = conn.getInputStream();
			InputStreamReader inReader = new InputStreamReader(stream);
			BufferedReader buffer = new BufferedReader(inReader);
			String strLine = null;
			while ((strLine = buffer.readLine()) != null) {
				result += strLine;
			}
			inReader.close();
			conn.disconnect();
			return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
