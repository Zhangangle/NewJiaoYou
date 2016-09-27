package com.yunkl.os;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.may.sdk.on.MaySDK;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.CatchHandlerUtils;
import com.zmv.zf.utils.ExitManager;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initInfoData();
		// 先初始化我们的异常处理，再初始化Bugly，否者无法上抛异常
		tryCatch();
		MaySDK.getInstance().initApplication(getApplicationContext());

	}

	@Override
	protected void attachBaseContext(Context base) {
		// TODO Auto-generated method stub
		super.attachBaseContext(base);
		// SdkManager.install(base);
	}

	/**
	 * 异常处理
	 */
	private void tryCatch() {
		// 崩溃异常处理
		CatchHandlerUtils catchHandler = CatchHandlerUtils.getInstance();
		// catchHandler.initAndService(getApplicationContext(),
		// JiMoMainService.class);
		catchHandler.init(getApplicationContext());
		// 设置报错处理参数
		catchHandler.setParam(1, Environment.getExternalStorageDirectory()
				.getPath() + "/zmv/errorlog");
	}

	private void initInfoData() {
		// TODO Auto-generated method stub
		TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		PackageInfo packageInfo;
		ApplicationInfo appInfo;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					0);
			Conf.version = packageInfo.versionName;
			Conf.IMEI = phoneMgr.getDeviceId();
			phoneMgr.getSubscriberId();
			phoneMgr.getSimSerialNumber();
			phoneMgr.getSimSerialNumber();
//			Conf.SIM = phoneMgr.getNetworkOperatorName();
//			Conf.Model = android.os.Build.MODEL;
			appInfo = getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			Conf.CID = String.valueOf(appInfo.metaData.get("UMENG_CHANNEL"));
			Conf.IMSI = phoneMgr.getSubscriberId();
			if (Conf.IMSI != null) {
				if (Conf.IMSI.startsWith("46000")
						|| Conf.IMSI.startsWith("46002")
						|| Conf.IMSI.startsWith("46007")) {
					// "中国移动";
//					Conf.operators = 1;
				} else if (Conf.IMSI.startsWith("46001")
						|| Conf.IMSI.startsWith("46006")) {
					// "中国联通";
//					Conf.operators = 2;
				} else if (Conf.IMSI.startsWith("46003")
						|| Conf.IMSI.startsWith("46005")) {
					// "中国电信";
//					Conf.operators = 3;
				}
			}
			// else
			// ConfigUtils.nopay = true;
			// Log.e("conf.cid", Conf.CID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		ExitManager.getScreenManager().popAllActivity();
	}
}
