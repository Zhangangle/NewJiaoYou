package com.yunkl.os;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.Mei.sdl.wpkg.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wdwe.u.MainService;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.UserDAO;
import com.zmv.zf.make.OrderUtils;
import com.zmv.zf.utils.BasicUtils;
import com.zmv.zf.utils.ExitManager;
import com.zmv.zf.utils.NetworkUtils;

public class Launch extends Activity {
	private Context context;
	private boolean open = false;
	private TextView tv;
	private boolean goIn = false;
//	private String run_status;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 0:// 请求网络
					if (!open) {
						open = true;
						OrderUtils.getInstance().initOrder(Launch.this,
								"shiyong", new Handler() {
									public void handleMessage(Message msg) {
										try {
											switch (msg.what) {
											case 0:// 失败
												break;
											case 1:// 成功
												BasicUtils.updateOpen(context,
														4);
												break;
											default:
												break;
											}
											handler.sendEmptyMessage(2);
										} catch (Exception e) {
										}
									}
								});

//						new Thread(new Runnable() {
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								try {
//									run_status = OnlineConfigAgent
//											.getInstance().getConfigParams(
//													context, "run_status");
//									Thread.sleep(50000);
//									handler.sendEmptyMessage(2);
//
//								} catch (Exception e) {
//									// TODO: handle exception
//								}
//							}
//						}).start();
					}
					break;
				case 1:
					networkDialog();
					break;
				case 2:
					if (!goIn) {
						goIn = true;

						startService(new Intent(context, MainService.class));
						startActivity(new Intent(Launch.this, Main.class));
//						finish();

					}

					break;
				case 3:
//					try {
//						if (run_status == null || run_status.trim().equals("")) {
//							run_status = OnlineConfigAgent.getInstance()
//									.getConfigParams(context, "run_status");
//							if (run_status != null
//									&& run_status.trim().equals("0")) {
//								handler.sendEmptyMessage(4);
//								return;
//							}
//						} else if (run_status.trim().equals("0")) {
//							handler.sendEmptyMessage(4);
//							return;
//						}
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
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
			context = Launch.this;
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
//			OnlineConfigAgent.getInstance().updateOnlineConfig(this);
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

		try {
			if (mReceiver != null)
				unregisterReceiver(mReceiver);
//			OrderUtils.getInstance().onFinish();
			super.onDestroy();
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
					handler.sendEmptyMessageDelayed(0, 2000);
					handler.sendEmptyMessageDelayed(3, 4000);
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

}
