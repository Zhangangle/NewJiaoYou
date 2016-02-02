package com.zmv.zf.pay;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.drjq.mm.R;
import com.zmv.zf.activity.TalkActivity;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.UserDAO;
import com.zmv.zf.utils.BasicUtils;

public class ThirdDialog implements OnClickListener {
	private static ThirdDialog instance;
	int pay_type = 1;
	Dialog payDialog;
	Context context;
	ImageView img_oneselect, img_monselect;
	AlipayUtils payUtils;
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	private ThirdDialog() {
	}

	public static ThirdDialog getInstance() {
		if (instance == null) {
			instance = new ThirdDialog();
		}
		return instance;
	}

	public void makeDialog(final Activity activity) {
		try {
			context = activity;
			MobclickAgent.onEvent(context, "thirdpay_request");
			payDialog = BasicUtils.showDialog(activity, R.style.BasicDialog);
			payDialog.setContentView(R.layout.dialog_thirdpay);
			payDialog.setCanceledOnTouchOutside(false);
			payDialog.setCancelable(false);
			TextView tv = (TextView) payDialog.getWindow().findViewById(
					R.id.tv_dialog_msg);
			// tv.setText("");
			payDialog.getWindow().findViewById(R.id.img_dialog_close)
					.setOnClickListener(this);
			payDialog.getWindow().findViewById(R.id.img_dialog_alipay)
					.setOnClickListener(this);
			payDialog.getWindow().findViewById(R.id.img_dialog_wx)
					.setOnClickListener(this);
			payDialog.getWindow().findViewById(R.id.tv_dialog_one)
					.setOnClickListener(this);
			img_oneselect = (ImageView) payDialog.getWindow().findViewById(
					R.id.img_dialog_oneselect);
			img_oneselect.setOnClickListener(this);
			payDialog.getWindow().findViewById(R.id.tv_dialog_mon)
					.setOnClickListener(this);
			img_monselect = (ImageView) payDialog.getWindow().findViewById(
					R.id.img_dialog_monselect);
			img_monselect.setOnClickListener(this);

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

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.img_dialog_close:// 关闭
			if (payDialog != null) {
				payDialog.dismiss();
				payDialog = null;
			}
			Intent intent = new Intent("com.zmv.login.action");
			context.sendBroadcast(intent);
			break;
		case R.id.img_dialog_alipay:// 支付宝
			payUtils = new AlipayUtils(context, handler);
			// payUtils.getHttpData();handler
			MobclickAgent.onEvent(context, "zfb_request");
//			Date nowTime = new Date(System.currentTimeMillis());
//			SimpleDateFormat sdFormatter = new SimpleDateFormat(
//					"yyyyMMddHHmmssms");
//			if (pay_type == 0)
//				payUtils.pay(sdFormatter.format(nowTime) + Conf.CID, "单次点播",
//						"单次点播", "5");
//			else
//				payUtils.pay(sdFormatter.format(nowTime) + Conf.CID, "包月无限",
//						"包月无限", "20");
			break;
		case R.id.img_dialog_wx:// 微信
			if (BasicUtils.isInstallApk(context, "com.tencent.mm")) {
				MobclickAgent.onEvent(context, "wx_request");
				if (pay_type == 0)
					WXPayUtils.getInstance(context, handler).goToPay("单次点播",
							"5");
				else
					WXPayUtils.getInstance(context, handler).goToPay("包月无限",
							"20");
			} else {
				Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_dialog_one:// 点播
		case R.id.img_dialog_oneselect:// 点播选项
			if (pay_type == 1) {
				pay_type = 0;
				img_oneselect.setImageResource(R.drawable.pay_dialog_cb);
				img_monselect.setImageResource(R.drawable.pay_dialog_not_cb);
			}
			break;
		case R.id.tv_dialog_mon:// 包月
		case R.id.img_dialog_monselect:// 包月
			if (pay_type == 0) {
				pay_type = 1;
				img_monselect.setImageResource(R.drawable.pay_dialog_cb);
				img_oneselect.setImageResource(R.drawable.pay_dialog_not_cb);
			}
			break;

		default:
			break;
		}
	}

//	public void showNotify(Context context,String message, BaseJson base) {
//		// 先设定RemoteViews
//		try {
//
//			NotificationManager mNotificationManager = (NotificationManager) context
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//			RemoteViews view_custom = new RemoteViews( context.getPackageName()
//					R.layout.view_custom);
//			// 设置对应IMAGEVIEW的ID的资源图片
//			view_custom.setImageViewResource(R.id.custom_icon, R.drawable.logo1);
//			view_custom.setTextViewText(R.id.tv_custom_title,
//					context.getString(R.string.app_name));
//			view_custom.setTextViewText(R.id.tv_custom_content, message);
//			view_custom.setTextViewText(R.id.tv_custom_time,
//					format.format(new Date(System.currentTimeMillis())));
//			Builder mBuilder = new Builder(context);
//			mBuilder.setContent(view_custom).setAutoCancel(true)
//			// 通知产生的时间，会在通知信息里显示
//					.setTicker("您有新的私信").setPriority(1000)// 设置该通知优先级
//															// Notification.PRIORITY_DEFAULT
//					.setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
//					.setSmallIcon(R.drawable.logo1);
////			if (Conf.UID == null || Conf.UID.equals("")) {
////				dao = new UserDAO(context);
////				list = dao.findAll();
////				if (list != null && list.size() > 0) {
////					if (list.get(0).getUser_id() != null)
////						Conf.userID = list.get(0).getUser_id();
////				}
////			}
//			Intent resultIntent;
////			if (!MainFragmentActivity.flag_status) {
////				resultIntent = new Intent(context, SplashActivity.class);
////			} else {
////				if (base.getDialog_id() != null
////						&& !base.getDialog_id().equals("")
////						&& TalkActivity.talk_id.equals("")) {
////					resultIntent = new Intent(context, TalkActivity.class);
////					resultIntent.putExtra("person", base);
////				} else
////					resultIntent = new Intent(context,
////							MainFragmentActivity.class);
////			}
//
//			resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//					resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			mBuilder.setContentIntent(pendingIntent);
//			Notification notify = mBuilder.build();
//			notify.defaults |= Notification.DEFAULT_SOUND;
//			notify.defaults |= Notification.DEFAULT_LIGHTS;
//			notify.contentView = view_custom;
//			mNotificationManager.notify(1, notify);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 0:// 支付成功
				case 2:
					if (pay_type == 1) {
						Conf.VIP = true;
						UserDAO user = new UserDAO(context);
						user.updateVIP(30);
					} else {
						Conf.OPEN += 1;
						UserDAO user = new UserDAO(context);
						user.updateOpen(Conf.OPEN);
					}
					if (payDialog != null) {
						payDialog.dismiss();
						payDialog = null;
					}
					if (msg.what == 0)
						MobclickAgent.onEvent(context, "zfb_success");
					else
						MobclickAgent.onEvent(context, "wx_success");
					Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent("com.zmv.login.action");
					context.sendBroadcast(intent);
					break;
				case 1:// 支付失败
				case 3:
					if (msg.what == 1)
						MobclickAgent.onEvent(context, "zfb_fail");
					else
						MobclickAgent.onEvent(context, "wx_fail");
					Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		};
	};
}
