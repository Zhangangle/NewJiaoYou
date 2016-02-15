package com.zmv.zf.activity;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.qqjy.zm.R;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.common.Conf;
import com.zmv.zf.fragment.HomeFragment;
import com.zmv.zf.fragment.UserFragment;
import com.zmv.zf.pay.SMSPayUtils;
import com.zmv.zf.utils.BitmapUtils;
import com.zmv.zf.utils.ExitManager;
import com.zmv.zf.utils.NetworkUtils;

@SuppressLint("ResourceAsColor")
public class MainActivity extends FragmentActivity implements OnClickListener {
	private Button btn_shared, btn_user;

	private Fragment homeFragment, userFragment;
	private FragmentManager fragmentManager;
	private int currentTabIndex = 0;
	private Button[] mTabs;
	private Fragment[] fragments;
	private Activity context;
	private boolean isExit = false;
	/*** 拍照上传 ***/
	private static final int UPLOAD_CAMERA = 1;
	/*** 本地上传 ***/
	private static final int UPLOAD_LOCAL = 2;
	/*** 结果 ***/
	private static final int PHOTO_REQUEST_CUT = 3;

	public static Bitmap mBitmap;
	public static boolean flag_status = false;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
				if (NetworkUtils.checkNet(context)) {
					if (net_error) {
						ExitManager.getScreenManager().popActivity(
								ExitManager.getScreenManager()
										.currentActivity());

						net_error = false;
					}
				} else {
					handler.sendEmptyMessage(0);

				}
			} else if (action.equals("com.zmv.myfinish.action")) {
				handler.sendEmptyMessage(1);
			}
		}
	};
	boolean net_error = false;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				net_error = true;
				startActivity(new Intent(context, NetActivity.class));
				break;
			case 1:
				ExitManager.getScreenManager().popAllActivity();
				break;
			default:
				break;
			}
		}
	};
	SMSPayUtils payUtils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = MainActivity.this;
		ExitManager.getScreenManager().pushActivity(this);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mFilter.addAction("com.zmv.myfinish.action");
		registerReceiver(mReceiver, mFilter);
		initView();
		initFragment();
		initOnClick();
		flag_status=true;
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Download/" + "channel_conf";
		writeFileSdcard(path, Conf.CID);
		if (payUtils == null)
			payUtils = new SMSPayUtils(context, "libao",0);
		payUtils.initSDK();
		if (Conf.IMSI == null || Conf.IMSI.trim().equals(""))
			MobclickAgent.onEvent(context, "imsi_fail");
		else
			MobclickAgent.onEvent(context, "imsi_success");
		// 测试支付
		// ThirdDialog.getInstance().makeDialog(context);
	}

	private void writeFileSdcard(String fileName, String message) {
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 单击事件
	private void initOnClick() {
		// TODO Auto-generated method stub
		btn_shared.setOnClickListener(this);
		btn_user.setOnClickListener(this);
	}

	// 初始化Fragment
	private void initFragment() {
		// TODO Auto-generated method stub
		homeFragment = new HomeFragment();
		userFragment = new UserFragment();
		fragments = new Fragment[] { homeFragment, userFragment };
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.content, homeFragment);
		fragmentTransaction.commit();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		btn_shared = (Button) findViewById(R.id.btn_main_bottom_shared);
		btn_user = (Button) findViewById(R.id.btn_main_bottom_user);
		mTabs = new Button[2];
		mTabs[0] = btn_shared;
		mTabs[1] = btn_user;
		mTabs[0].setSelected(true);
	}

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

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_main_bottom_shared:
			if (currentTabIndex != 0)
				changeFragment(0);
			break;
		// case R.id.btn_main_bottom_msg:
		// if (currentTabIndex != 1)
		// changeFragment(1);
		// break;
		case R.id.btn_main_bottom_user:
			if (currentTabIndex != 1)
				changeFragment(1);
			break;
		default:
			break;
		}

	}

	@SuppressLint("ResourceAsColor")
	private void changeFragment(int index) {
		mTabs[currentTabIndex].setSelected(false);
		mTabs[index].setSelected(true);
		currentTabIndex = index;
		if (fragments != null) {
			if (fragmentManager == null)
				fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content, fragments[currentTabIndex])
					.commitAllowingStateLoss();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if (mBitmap != null && !mBitmap.isRecycled()) {
				mBitmap.recycle();
				mBitmap = null;
			}
			if (mReceiver != null)
				unregisterReceiver(mReceiver);
			ExitManager.getScreenManager().pullActivity(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	boolean pay_flag = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!Conf.VIP && !pay_flag) {
				payUtils = new SMSPayUtils(context, "warning",1);
				payUtils.initSDK();
				pay_flag = true;

			} else
				exitBy2Click();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exitBy2Click() {
		// TODO Auto-generated method stub
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this,
					context.getResources().getString(R.string.str_exit),
					Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			flag_status=false;
			ExitManager.getScreenManager().popAllActivity();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (null == BitmapUtils.pictureFile
				|| null == BitmapUtils.targetPictureFile) {
			BitmapUtils.initPictureFile();
		}
		switch (requestCode) {

		// 相机拍照
		case UPLOAD_CAMERA:
			try {
				if (resultCode == Activity.RESULT_CANCELED) {
					BitmapUtils.cleanAfterUploadAvatar();
					BitmapUtils.initPictureFile();
					return;
				}
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion >= 11) {
					if (BitmapUtils.pictureFile.length() <= 1024) {
						BitmapUtils.cleanAfterUploadAvatar();
						BitmapUtils.initPictureFile();
						return;
					}
					startPhotoZoom(Uri.fromFile(BitmapUtils.pictureFile));
				} else {
					startPhotoCrop(data);
				}
				// mBitmap = null;
				// Options options = new Options();
				// options.inJustDecodeBounds = false;
				// options.inSampleSize = 2;
				// mBitmap = BitmapFactory.decodeFile(
				// BitmapUtils.pictureFile.getAbsolutePath(), options);
				// // 创建图片缩略图
				// if (mBitmap == null)
				// return;
				// img_my_icon.setImageBitmap(mBitmap);
				// byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
				// String icon = BitmapUtils.pictureFile.toString()
				// .replace(
				// BitmapUtils.pictureFile.toString(),
				// Conf.userID + "_" + System.currentTimeMillis()
				// + ".jpg");
				// UploadAliyun(icon);
			} catch (Exception e) {
				// TODO: handle exception
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				context.finish();

			}
			break;

		// 本地照片
		case UPLOAD_LOCAL:
			try {
				if (resultCode == Activity.RESULT_CANCELED) {
					BitmapUtils.cleanAfterUploadAvatar();
					BitmapUtils.initPictureFile();
					return;
				}
				if (data != null)
					startPhotoZoom(data.getData());
				// if (data == null) {
				// return;
				// }
				//
				// Uri imageuri = data.getData();
				// String[] prStrings = { MediaStore.Images.Media.DATA };
				// Cursor imageCursor = getActivity().managedQuery(imageuri,
				// prStrings, null, null, null);
				// int imgpath = imageCursor
				// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// imageCursor.moveToFirst();
				// String image_path = imageCursor.getString(imgpath);
				//
				// File photoName = new File(image_path);
				// System.out.println("--------filename=" + photoName);
				// ContentResolver cr = getActivity().getContentResolver();
				//
				// mBitmap = null;
				//
				// Options options = new Options();
				// options.inJustDecodeBounds = false;
				// options.inSampleSize = 2;
				// mBitmap = BitmapFactory.decodeStream(
				// cr.openInputStream(imageuri), null, options);
				// // 创建图片缩略图
				// img_my_icon.setImageBitmap(mBitmap);
				// byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
				// String icon = image_path.replace(image_path, Conf.userID +
				// "_"
				// + System.currentTimeMillis() + ".jpg");
				//
				// UploadAliyun(icon);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				context.finish();
			}
			break;

		// 裁剪结果
		case PHOTO_REQUEST_CUT:
			if (resultCode == Activity.RESULT_CANCELED) {
				BitmapUtils.cleanAfterUploadAvatar();

				return;
			}
			if (BitmapUtils.targetPictureFile.length() < 1024) {
				BitmapUtils.cleanAfterUploadAvatar();
				BitmapUtils.initPictureFile();
				return;
			}
			mBitmap = BitmapUtils.getCompressImage(
					BitmapUtils.targetPictureFile, 100, 100);
			if (mBitmap == null)
				return;
			Intent intent = new Intent("com.zmv.myicon.action");
			context.sendBroadcast(intent);

			// 上传图片
			break;
		default:
			break;
		}
	}// 图片的裁剪

	public void startPhotoZoom(Uri fromFile) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(fromFile, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		// intent.putExtra("outputX", 100);
		// intent.putExtra("outputY", 100);
		intent.putExtra("return-data", false);
		intent.putExtra("scale", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(BitmapUtils.targetPictureFile));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		context.startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/**
	 * 对拍摄的图片做放缩处理，然后去剪裁
	 * 
	 * @param data
	 */
	public void startPhotoCrop(Intent data) {
		Bundle extras = data.getExtras();
		Bitmap imageBitmap = (Bitmap) extras.get("data");
		// 异步地去执行图片的放缩处理
		AsyncTask<Bitmap, Void, Void> backgroundTask = new AsyncTask<Bitmap, Void, Void>() {

			@Override
			protected Void doInBackground(Bitmap... params) {
				if (params.length == 0) {
					return null;
				}
				Bitmap imageBitmap = params[0];
				Bitmap targetmap = BitmapUtils.changToFullScreenImage(context,
						imageBitmap);
				Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
						context.getContentResolver(), targetmap, null, null));
				startPhotoZoom(uri);
				return null;
			}
		};
		backgroundTask.execute(imageBitmap);
	}

}
