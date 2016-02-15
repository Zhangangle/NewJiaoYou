package com.zmv.zf.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qqjy.zm.R;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.adapter.TalkListAdapter;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.DialogDAO;
import com.zmv.zf.database.SendDAO;
import com.zmv.zf.pay.SMSPayUtils;
import com.zmv.zf.utils.BasicUtils;
import com.zmv.zf.utils.BitmapUtils;
import com.zmv.zf.utils.ExitManager;
import com.zmv.zf.utils.IOUtils;

@SuppressLint("ResourceAsColor")
public class TalkActivity extends FragmentActivity implements OnClickListener {

	private Activity context;
	private TextView tv_top_title;
	private LinearLayout llayout_top_back;
	private Button btn_talk_pic, btn_talk_send;
	private EditText ed_talk;
	private ListView lv_talk;
	private String[] choice = { "拍照", "本地图片" };
	/*** 拍照上传 ***/
	private static final int UPLOAD_CAMERA = 1;
	/*** 本地上传 ***/
	private static final int UPLOAD_LOCAL = 2;
	/*** 结果 ***/
	private static final int PHOTO_REQUEST_CUT = 3;
	private Bitmap mBitmap;
	private byte[] byte_img;
	private BaseJson base_user;
	private List<BaseJson> list_talk;
	private TalkListAdapter talkAdapter;
	Timer mTimer;
	String[] talk_msg = new String[] { "好寂寞，想找个男朋友", "想领养我么，我会乖乖滴...",
			"喜欢我就发消息给我哟^^", "亲，在么？", "发个联系方式呗？", "天气好冷哟...", "我可爱不？", "你的职业是？",
			"我是上班白领，你呢？", "好多人都是我不美，你说呢", "欧巴觉得妹子怎么样", "想找个欧巴回家过年",
			"想知道你家里的情况？", "有空一起旅行吗？", "户外运动喜欢哪种？", "我喜欢游泳，你呢？", "我喜欢锤锤哪种类型的",
			"呜呜呜呜，我这要下雨", "上的厨房下的厅堂的我，没有男盆友..", "有什么好电影推荐的吗？" };
	SendDAO send;
	SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

	SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
	SMSPayUtils payUtils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talk);
		context = TalkActivity.this;
		ExitManager.getScreenManager().pushActivity(this);
		base_user = (BaseJson) getIntent().getSerializableExtra("person");
		Conf.OPUID = base_user.getUid();
		DialogDAO dialog = new DialogDAO(context);
		dialog.addDialog(base_user);
		initView();
		initOnClick();
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessageDelayed(0,
						(1 + (int) (Math.random() * 3)) * 1000);
			}
		}, 1000, 5 * 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);

		Intent intent = new Intent("com.zmv.mymsg.action");
		context.sendBroadcast(intent);
		// 测试支付
		// ThirdDialog.getInstance().makeDialog(context);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				send = new SendDAO(context);
				int sendTime = send.sendTime(Conf.UID + base_user.getUid(),
						Conf.UID);
				send = new SendDAO(context);
				int receTime = send.sendTime(Conf.UID + base_user.getUid(),
						base_user.getUid());
				if (sendTime <= 1 && receTime <= 1)
					sendMsg(base_user.getUid(), base_user.getIcon(),
							talk_msg[(int) (Math.random() * talk_msg.length)],
							"");
			default:
				break;
			}
		}
	};
	BaseJson user;

	DialogDAO dialogDao;
	boolean refresh = false;

	private void sendMsg(String id, String icon, String msg, String img) {
		user = new BaseJson();

		if (id.equals(Conf.UID) & !msg.equals(""))
			ed_talk.setText("");
		int type = 0;
		if (id.equals(Conf.UID))
			type = 1;
		dialogDao = new DialogDAO(context);
		dialogDao.updateMsg(Conf.UID + base_user.getUid(), msg);

		Date nowTime = new Date(new Date().getTime());
		String format = sd.format(nowTime);
		send = new SendDAO(context);
		send.add(Conf.UID + base_user.getUid(), id, type, icon, img, msg,
				nowTime.toString(), format);
		user.setUid(id);
		user.setType(type);
		user.setIcon(icon);
		user.setBigicon(img);
		user.setMsg(msg);
		user.setFormat_time(format);
		if (talkAdapter != null) {
			talkAdapter.insert(user);
			lv_talk.requestLayout();
			// talkAdapter.notifyDataSetChanged();
		} else {
			List<BaseJson> list = new ArrayList<BaseJson>();
			list.add(user);
			talkAdapter = new TalkListAdapter(context, list);
			lv_talk.setAdapter(talkAdapter);
		}
		lv_talk.setSelection(lv_talk.getCount() - 1);
		if (!refresh) {
			refresh = true;
			Intent intent = new Intent("com.zmv.mymsg.action");
			context.sendBroadcast(intent);
		}
	}

	// 单击事件
	private void initOnClick() {
		// TODO Auto-generated method stub
		llayout_top_back.setOnClickListener(this);
		btn_talk_pic.setOnClickListener(this);
		btn_talk_send.setOnClickListener(this);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		tv_top_title = (TextView) findViewById(R.id.tv_top_title);
		llayout_top_back = (LinearLayout) findViewById(R.id.llayout_top_back);
		btn_talk_pic = (Button) findViewById(R.id.btn_talk_pic);
		btn_talk_send = (Button) findViewById(R.id.btn_talk_send);
		lv_talk = (ListView) findViewById(R.id.lv_talk);
		ed_talk = (EditText) findViewById(R.id.ed_talk);
		tv_top_title.setVisibility(View.VISIBLE);
		llayout_top_back.setVisibility(View.VISIBLE);
		tv_top_title.setText(base_user.getName());
		send = new SendDAO(context);
		list_talk = send.findMsg(Conf.UID + base_user.getUid());
		if (list_talk != null && list_talk.size() > 0) {
			talkAdapter = new TalkListAdapter(context, list_talk);
			lv_talk.setAdapter(talkAdapter);
		}
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
		case R.id.llayout_top_back:
			finish();
			break;
		case R.id.btn_talk_pic:
			int sendTime2 = send.sendTime(Conf.UID + base_user.getUid(),
					Conf.UID);
			if (Conf.VIP || sendTime2 <= 2) {
				MobclickAgent.onEvent(context, "talk_pic");
				ShowChoiceDialog();
			} else {
				MobclickAgent.onEvent(context, "talk_request");
				if (payUtils == null)
					payUtils = new SMSPayUtils(context, "warning",1);
				payUtils.initSDK();
			}
			break;
		case R.id.btn_talk_send:
			String msg = ed_talk.getText().toString().trim();
			if (BasicUtils.containsEmoji(msg)) {
				Toast.makeText(context, "不能输入特殊符号", Toast.LENGTH_SHORT).show();
				return;
			} else if (msg.equals("")) {
				Toast.makeText(context, "不能输入空", Toast.LENGTH_SHORT).show();
			}
			closeInputMethod();
			send = new SendDAO(context);
			int sendTime = send.sendTime(Conf.UID + base_user.getUid(),
					Conf.UID);
			if (Conf.VIP || sendTime <= 2) {
				MobclickAgent.onEvent(context, "talk_send");
				sendMsg(Conf.UID, "", msg, "");
			} else {
				MobclickAgent.onEvent(context, "talk_request");
				if (payUtils == null)
					payUtils = new SMSPayUtils(context, "warning",1);
				payUtils.initSDK();
			}
			break;
		default:
			break;
		}
	}

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();

		if (isOpen) {
			imm.hideSoftInputFromWindow(ed_talk.getWindowToken(), 0);
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
			Conf.OPUID = "";
			ExitManager.getScreenManager().pullActivity(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 弹出选择图片对话框
	private void ShowChoiceDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context)
				.setItems(choice, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if (arg1 == 0) {
							// 调用相机拍照
							if (!BasicUtils.isSDCardAvaliable()) {
								Toast.makeText(context, "请检查SD卡是否存在。",
										Toast.LENGTH_SHORT).show();
								return;
							}
							BitmapUtils.initPictureFile();
							// 调用系统的拍照功能
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							int currentapiVersion = android.os.Build.VERSION.SDK_INT;
							try {

								// if (currentapiVersion > 11) {
								// 指定调用相机拍照后照片的储存路径
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(BitmapUtils.pictureFile));
								// }
								context.startActivityForResult(intent,
										UPLOAD_CAMERA);
							} catch (Exception e) {
								// TODO: handle exception
							}

						} else {
							// 调用本地相册
							try {
								Intent intent = new Intent(Intent.ACTION_PICK,
										null);
								intent.setType("image/*");
								context.startActivityForResult(intent,
										UPLOAD_LOCAL);
							} catch (Exception e) {
								// TODO: handle exception
								try {
									Intent intent = new Intent();
									/* 开启Pictures画面Type设定为image */
									intent.setType("image/*");
									/* 使用Intent.ACTION_GET_CONTENT这个Action */
									intent.setAction(Intent.ACTION_GET_CONTENT);
									context.startActivityForResult(intent,
											UPLOAD_LOCAL);
								} catch (Exception e2) {
									// TODO: handle exception
									Toast.makeText(context, "启动相册失败",
											Toast.LENGTH_SHORT).show();
								}

							}
						}
					}

				}).create().show();
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
					BitmapUtils.targetPictureFile, 150, 150);
			if (mBitmap == null)
				return;
			String time = sdFormatter.format(new Date(new Date().getTime()));
			BitmapUtils.targetPictureFile.renameTo(new File(IOUtils
					.getApplicationFolder(), time));
			sendMsg(Conf.UID, "", "", time);
			// Intent intent = new Intent("com.zmv.myicon.action");
			// context.sendBroadcast(intent);

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
