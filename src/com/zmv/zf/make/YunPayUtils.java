package com.zmv.zf.make;

import java.util.Random;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dxt.mipay.pay.AppTache;

public class YunPayUtils {
	private static YunPayUtils miliPayUtils;

	public static YunPayUtils getInstance() {
		if (miliPayUtils == null) {
			miliPayUtils = new YunPayUtils();
		}
		return miliPayUtils;
	}

	// public boolean isExist = false;

	// public boolean isExistLib() {
	// try {
	// Class.forName("com.dm.ml.MiLiNewApi");
	// } catch (Exception e) {
	// // TODO: handle exception
	// isExist = false;
	// return false;
	// }
	// isExist = true;
	// return true;
	// }

	public void init(Context context) {
		// File file = new File(context.getCacheDir().getAbsolutePath() +
		// "/info/ml206.jar");
		// CopyAssertJarToFile(context, "ml.zip", file.getAbsolutePath());

		AppTache.getInstance().onResume(context);
		// MiLiP.setAppInfo(appId, 渠道号, 包名);
		// MiLiNewApi.setAppInfo("21841521", "10004", context.getPackageName());
	}

	// public static void CopyAssertJarToFile(Context context, String fileName,
	// String filePath)
	// {
	// try
	// {
	// InputStream inStream = context.getAssets().open(fileName);
	// File newFile = new File(filePath);
	// if (!newFile.exists())
	// {
	// // 文件不存在，则创建目录
	// newFile.getParentFile().mkdirs();
	// }
	// FileOutputStream fs = new FileOutputStream(newFile);
	// byte[] buffer = new byte[1024];
	// int byteread = 0;
	// while ((byteread = inStream.read(buffer)) != -1)
	// {
	// fs.write(buffer, 0, byteread);
	// }
	// fs.flush();
	// fs.close();
	// inStream.close();
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
	public boolean _flagToBack = false;

	public void doPay(final Context context, String _itemId,
			final Handler handler) {
		_flagToBack = false;
		String payId = "礼包";// 计费点ID
//		int itemId = Integer.valueOf(_itemId);
		int priceNum = 2000;// 金额
		 Random r = new Random();
		 int itemId = r.nextInt(4);
		// switch (num) {
		// case 0:
		// payId = "53001612";
		// break;
		// case 1:
		// payId = "53001613";
		// break;
		// case 2:
		// payId = "53001614";
		// break;
		// case 3:
		// payId = "53001615";
		// break;
		//
		// default:
		// payId = "53001612";
		// break;
		// }
		if (itemId == 1) {
			payId = "新手礼包";
			priceNum = 2000;
		} else if (itemId == 2) {
			payId = "彩钻礼包";
			priceNum = 2000;
		} else if (itemId == 3) {
			payId = "挑战模式";
			priceNum = 2000;
		} else if (itemId == 4) {
			payId = "钻石2000";
			priceNum = 2000;
		} else if (itemId == 5) {
			payId = "钻石1500";
			priceNum = 1500;
		} else if (itemId == 6) {
			payId = "钻石1000";
			priceNum = 1000;
		} else if (itemId == 7) {
			payId = "钻石800";
			priceNum = 800;
		} else if (itemId == 8) {
			payId = "钻石400";
			priceNum = 400;
		} else if (itemId == 9) {
			payId = "钻石200";
			priceNum = 200;
		}
		AppTache.getInstance().requestTaskPay(System.currentTimeMillis() + "",
				itemId+"", payId, 1, priceNum, true, new Handler() {

					public void handleMessage(Message msg) {
						String result = (String) msg.obj;
						String is_success = AppTache.getValue(result,
								"is_success");
						String real_price = AppTache.getValue(result,
								"real_price");
						String user_order_id = AppTache.getValue(result,
								"user_order_id");
						String error_code = AppTache.getValue(result,
								"error_code");
						String error_msg = AppTache.getValue(result,
								"error_msg");
						if (_flagToBack)
							return;
						Log.e("yun", is_success + ",result" + result);

						if (is_success != null && is_success.equals("true")) {
							handler.sendEmptyMessage(200);
						} else {
							handler.sendEmptyMessage(201);
						}
						_flagToBack = true;
						switch (msg.what) {
						case AppTache.REQUEST_PAY:
							// Toast.makeText(context, result, 3000).show();
							break;

						// case AppTache.REQUEST_THIRD_LIST:
						// Toast.makeText(YCPayActivity.this, result,
						// 3000).show();
						// break;
						}
					}

				});

	}
}
