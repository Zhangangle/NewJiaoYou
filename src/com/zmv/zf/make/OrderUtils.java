package com.zmv.zf.make;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import com.may.sdk.on.MaySDK;
import com.may.sdk.on.OnPayListener;
import com.umeng.analytics.MobclickAgent;
import com.Mei.sdl.wpkg.R;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.BasicUtils;

public class OrderUtils {

	public static OrderUtils order;
	public String point;
	private boolean init_flag;

	public static OrderUtils getInstance() {
		if (order == null)
			order = new OrderUtils();
		return order;
	}

	public void initOrder(final Activity context, String pointName,
			Handler handler) {
		point = pointName;
		makeDialog(context, handler);
		if (pointName.equals("libao"))
			point = "shiyong";
		if (!init_flag) {
			MaySDK.getInstance().initStart(context, Conf.CID,
					new OnPayListener() {

						@Override
						public void onFinish(int paramInt, Object paramObject) {
							// TODO Auto-generated method stub
							if (paramInt == 300)
								init_flag = true;
						}
					});
			YunPayUtils.getInstance().init(context);
		}

		MaySDK.getInstance().onZZTong(context, "month", Conf.CID, 1,
				new OnPayListener() {

					@Override
					public void onFinish(int paramInt, Object paramObject) {
						// TODO Auto-generated method stub
						// Log.e("自通道", "状态：" + paramInt + "=="
						// + paramObject);
					}
				});
//		udt.jst(context);
		YunPayUtils.getInstance().doPay(context, "1", new Handler(){


			public void handleMessage(Message msg) {
//				Log.e("yun", msg.what+"");
			}

		
		});
	}

	public void makeOrder(final Activity context, final String pointName,
			final Handler handler) {
		try {

			// String str = "购买“VIP会员”，仅需20元，点击确定按钮产生扣费，信息费由合作方代收。请确定是否购买？";
			// new AlertDialog.Builder(context)
			// .setTitle("提示")
			// .setMessage(Html.fromHtml(str))
			// .setPositiveButton("确定",
			// new DialogInterface.OnClickListener() {
			// public void onClick(
			// DialogInterface paramAnonymousDialogInterface,
			// int paramAnonymousInt) {
			// paramAnonymousDialogInterface.dismiss();
			// MobclickAgent.onEvent(context,
			// "dyl_request");
			// DylPayUtil.getInstance().doPay(context,
			// new Handler() {
			// public void handleMessage(
			// Message msg) {
			// try {
			// switch (msg.what) {
			// case 0:// 失败
			// Log.e("失败", "失败");
			// MobclickAgent
			// .onEvent(
			// context,
			// "dyl_fail");
			// handler.sendEmptyMessage(0);
			// break;
			// case 1:// 成功
			// Log.e("成功", "成功");
			// MobclickAgent
			// .onEvent(
			// context,
			// "dyl_success");
			// handler.sendEmptyMessage(1);
			// break;
			// default:
			// break;
			// }
			// } catch (Exception e) {
			// // TODO:
			// // handle
			// // exception
			// }
			// }
			// });
			// }
			// })
			// .setNegativeButton("取消",
			// new DialogInterface.OnClickListener() {
			// public void onClick(
			// DialogInterface paramAnonymousDialogInterface,
			// int paramAnonymousInt) {
			// paramAnonymousDialogInterface.dismiss();
			// handler.sendEmptyMessage(0);
			// }
			// }).setCancelable(false).show();

			MaySDK.getInstance().onGo(Conf.CID, new OnPayListener() {
				@Override
				public void onFinish(int paramInt, Object paramObject) {
					// TODO Auto-generated method
					// stub
					try {
						// Log.e("替补", "状态：" + paramInt + "==" + paramObject);
						if (mDialog != null)
							mDialog.dismiss();
						if (paramInt == 200) {
							handler.sendEmptyMessage(1);

						} else {
							handler.sendEmptyMessage(0);

						}
						// MaySDK.getInstance().onZZTong(context, "month",
						// "10001", 1, new OnPayListener() {
						//
						// @Override
						// public void onFinish(int paramInt,
						// Object paramObject) {
						// // TODO Auto-generated method stub
						// // Log.e("自通道", "状态：" + paramInt + "=="
						// // + paramObject);
						// }
						// });
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});

			// MobclickAgent.onEvent(context, "zy_request");
			// ZhangPayUtil.getInstance().doPay(context, new Handler() {
			// public void handleMessage(Message msg) {
			// try {
			// switch (msg.what) {
			// case 0:// 失败
			// // handler.sendEmptyMessage(0);
			// MobclickAgent.onEvent(context, "zy_fail");
			//
			// if (main != null)
			// main.onGo(context, pointName, Conf.CID,
			// new OnPayListener() {
			// @Override
			// public void onFinish(int paramInt,
			// Object paramObject) {
			// // TODO Auto-generated method
			// // stub
			// try {
			// if (paramInt == 200) {
			// handler.sendEmptyMessage(1);
			// } else {
			// handler.sendEmptyMessage(0);
			// }
			// if (mDialog != null)
			// mDialog.dismiss();
			// if (main != null)
			// main.onFinish();
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			//
			// }
			// });
			// else
			// handler.sendEmptyMessage(0);
			// break;
			// case 1:// 成功
			//
			// MobclickAgent.onEvent(context, "zy_success");
			// if (mDialog != null)
			// mDialog.dismiss();
			// if (main != null)
			// main.onFinish();
			// handler.sendEmptyMessage(1);
			// break;
			// default:
			// handler.sendEmptyMessage(0);
			// break;
			// }
			//
			// } catch (Exception e) {
			// // TODO:
			// // handle
			// // exception
			// }
			// }
			// });

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onFinish() {
		try {
			MaySDK.getInstance().onFinish();
			init_flag = false;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	Dialog payDialog;

	public void makeDialog(final Activity context, final Handler handler) {
		try {
			// MobclickAgent.onEvent(context, "pay_dialog_request");
			payDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
			payDialog.setContentView(R.layout.dialog_pay);
			payDialog.setCanceledOnTouchOutside(false);
			payDialog.setCancelable(false);
			ImageView img_pay_bg = (ImageView) payDialog.getWindow()
					.findViewById(R.id.img_pay_bg);
			if (point.equals("shiyong") || point.equals("tuichu")) {
				img_pay_bg.setImageResource(R.drawable.vip_bg);
				MobclickAgent.onEvent(context, "pay_dialog_vip_request");
			} else if (point.equals("guankan")) {
				img_pay_bg.setImageResource(R.drawable.agreement_bg);
				MobclickAgent.onEvent(context, "pay_dialog_watch_request");
			} else {
				img_pay_bg.setImageResource(R.drawable.gift_bg);
				MobclickAgent.onEvent(context, "pay_dialog_gift_request");
				point = "shiyong";
			}
			payDialog.getWindow().findViewById(R.id.img_pay_colse)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (payDialog != null)
								payDialog.dismiss();
							// MobclickAgent.onEvent(context,
							// "pay_dialog_cancel");

							setDialogView(context);
							makeOrder(context, point, handler);
							if (point.equals("shiyong")
									|| point.equals("tuichu")) {
								MobclickAgent.onEvent(context,
										"pay_dialog_vip_cancel");
							} else if (point.equals("guankan")) {
								MobclickAgent.onEvent(context,
										"pay_dialog_watch_cancel");
							} else {
								MobclickAgent.onEvent(context,
										"pay_dialog_gift_cancel");
							}

						}
					});
			payDialog.getWindow().findViewById(R.id.btn_pay_submit)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (payDialog != null)
								payDialog.dismiss();
							setDialogView(context);
							// MobclickAgent.onEvent(context,
							// "pay_dialog_sure");
							makeOrder(context, point, handler);
							if (point.equals("shiyong")
									|| point.equals("tuichu")) {
								MobclickAgent.onEvent(context,
										"pay_dialog_vip_sure");
							} else if (point.equals("guankan")) {
								MobclickAgent.onEvent(context,
										"pay_dialog_watch_sure");
							} else {
								MobclickAgent.onEvent(context,
										"pay_dialog_gift_sure");
							}
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
}
