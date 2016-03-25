package com.zmv.zf.pay;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.junho.mu.R;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.UserDAO;
import com.zmv.zf.utils.BasicUtils;

public class ThirdDialog implements OnClickListener {
	private static ThirdDialog instance;
	int pay_type = 1;
	Dialog payDialog, resultDialog;
	Activity mActivity;
	ImageView img_oneselect, img_monselect;
	AlipayUtils payUtils;
	TextView tv_result;
	ImageView img_prog;
	TextView tv_msg;
	ImageView img_submit;
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	private Timer mTimer; //

	private ThirdDialog() {
	}

	public static ThirdDialog getInstance() {
		if (instance == null) {
			instance = new ThirdDialog();
		}
		return instance;
	}

	private void setTimerTask() {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(5);
			}
		}, 3000, 5000);
	}

	public void makeDialog(final Activity activity) {
		try {
			mActivity = activity;
			MobclickAgent.onEvent(mActivity, "thirdpay_request");
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

	public void resultDialog(final Activity activity) {
		try {
			mActivity = activity;
			MobclickAgent.onEvent(mActivity, "thirdpay_request");
			resultDialog = BasicUtils.showDialog(activity, R.style.BasicDialog);
			resultDialog.setContentView(R.layout.dialog_result);
			resultDialog.setCanceledOnTouchOutside(false);
			resultDialog.setCancelable(false);
			tv_result = (TextView) resultDialog.getWindow().findViewById(
					R.id.tv_result);
			img_prog = (ImageView) resultDialog.getWindow().findViewById(
					R.id.img_result_progress);
			tv_msg = (TextView) resultDialog.getWindow().findViewById(
					R.id.tv_result_msg);
			img_submit = (ImageView) resultDialog.getWindow().findViewById(
					R.id.img_result_submit);
			Animation anim = AnimationUtils.loadAnimation(mActivity,
					R.anim.dialog_prog);
			LinearInterpolator lir = new LinearInterpolator();
			anim.setInterpolator(lir);
			img_prog.startAnimation(anim);
			img_submit.setOnClickListener(this);
			resultDialog.setOnKeyListener(new OnKeyListener() {

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
			resultDialog.show();
			handler.sendEmptyMessageDelayed(4, 3000);
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
			}
			// Intent intent = new Intent("com.zmv.login.action");
			// mActivity.sendBroadcast(intent);
			break;
		case R.id.img_dialog_alipay:// 支付宝
			payUtils = new AlipayUtils(mActivity, handler);
			// payUtils.getHttpData();handler
			MobclickAgent.onEvent(mActivity, "zfb_request");
			// Date nowTime = new Date(System.currentTimeMillis());
			// SimpleDateFormat sdFormatter = new SimpleDateFormat(
			// "yyyyMMddHHmmssms");
			if (pay_type == 0)
				payUtils.payInfo("单次点播", "单次点播", "5");
			else
				payUtils.payInfo("包月无限", "包月无限", "20");
			break;
		case R.id.img_dialog_wx:// 微信
			if (BasicUtils.isInstallApk(mActivity, "com.tencent.mm")) {
				MobclickAgent.onEvent(mActivity, "wx_request");
				if (pay_type == 0)
					QiYeWxH5.getInstance((Activity) mActivity, handler).goToPay(
							"单次点播", "5");
				else
					QiYeWxH5.getInstance((Activity) mActivity, handler).goToPay(
							"包月无限", "20");
				resultDialog(mActivity);
			} else {
				Toast.makeText(mActivity, "未安装微信", Toast.LENGTH_SHORT).show();
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
		case R.id.img_result_submit:
			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}
			if (resultDialog != null)
				resultDialog.dismiss();
			break;
		default:
			break;
		}
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 0:// 支付成功
				case 2:
					if (pay_type == 1) {
						Conf.VIP = true;
						UserDAO user = new UserDAO(mActivity);
						user.updateVIP(30);
					} else {
						Conf.OPEN += 1;
						UserDAO user = new UserDAO(mActivity);
						user.updateOpen(Conf.OPEN);
					}
					if (payDialog != null) {
						payDialog.dismiss();
					}
					if (msg.what == 0)
						MobclickAgent.onEvent(mActivity, "zfb_success");
					else
						MobclickAgent.onEvent(mActivity, "wx_success");
					Toast.makeText(mActivity, "支付成功", Toast.LENGTH_SHORT).show();
					// Intent intent = new Intent("com.zmv.login.action");
					// mActivity.sendBroadcast(intent);
					break;
				case 1:// 支付失败
				case 3:
					if (msg.what == 1)
						MobclickAgent.onEvent(mActivity, "zfb_fail");
					else
						MobclickAgent.onEvent(mActivity, "wx_fail");
					Toast.makeText(mActivity, "支付失败", Toast.LENGTH_SHORT).show();
					break;
				case 4:
					if (tv_result != null)
						tv_result.setVisibility(View.VISIBLE);
//					if (img_prog != null){
//						
//						img_prog.setVisibility(View.GONE);}
					if (tv_msg != null)
						tv_msg.setText("检验中");
					if (img_submit != null)
						img_submit.setVisibility(View.VISIBLE);
					setTimerTask();
					break;
				case 5:
					QiYeWxH5.getInstance((Activity) mActivity, handler)
							.getPayResult();
					break;
				case 6:// 微信失败
					if (tv_msg != null)
						tv_msg.setText("支付失败");
					break;
				case 7:// 微信成功
					if (mTimer != null) {
						mTimer.cancel();
						mTimer = null;
					}if (tv_msg != null)
						tv_msg.setText("支付成功");
					if (resultDialog != null)
						resultDialog.dismiss();
					handler.sendEmptyMessage(2);
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
