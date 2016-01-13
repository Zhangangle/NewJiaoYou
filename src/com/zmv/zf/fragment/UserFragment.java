package com.zmv.zf.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.hihi.jy.R;
import com.zmv.zf.activity.MainActivity;
import com.zmv.zf.activity.TalkActivity;
import com.zmv.zf.adapter.MsgAdapter;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.DialogDAO;
import com.zmv.zf.database.UserDAO;
import com.zmv.zf.pay.SMSPayUtils;
import com.zmv.zf.utils.BasicUtils;
import com.zmv.zf.utils.BitmapUtils;
import com.zmv.zf.view.PullToRefreshLayout;
import com.zmv.zf.view.PullToRefreshLayout.OnRefreshListener;

public class UserFragment extends Fragment implements OnClickListener,
		OnRefreshListener, OnItemClickListener {

	private Button btn_rightmenu_shared, btn_rightmenu_record,
			btn_rightmenu_joined, btn_rightmenu_recharge;
	private ImageView img_icon, img_set;
	private Activity context;
	private TextView tv_title, tv_username, tv_user_pay;
	private String[] choice = { "拍照上传", "本地图片上传" };
	/*** 拍照上传 ***/
	private static final int UPLOAD_CAMERA = 1;
	/*** 本地上传 ***/
	private static final int UPLOAD_LOCAL = 2;
	/*** 结果 ***/
	private static final int PHOTO_REQUEST_CUT = 3;
	private ListView listView;
	private PullToRefreshLayout ptrl;
	private List<BaseJson> list_dialog;
	private int index;
	private String title;
	private MsgAdapter msgAdapter;
	SMSPayUtils payUtils;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(context);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_user, null);

		findView(rootView);

		return rootView;
	}

	/**
	 * ListView初始化方法
	 */
	private void initListView() {
		list_dialog = new ArrayList<BaseJson>();
		DialogDAO dialog = new DialogDAO(context);
		list_dialog = dialog.findDialog();
		if (list_dialog != null && list_dialog.size() > 0) {
			msgAdapter = new MsgAdapter(context, list_dialog);
			listView.setAdapter(msgAdapter);
		}
		// Toast.makeText(context,
		// " Click on " + parent.getAdapter().getItemId(position),
		// Toast.LENGTH_SHORT).show();

	}

	private void findView(View rootView) {
		if (context == null)
			context = getActivity();
		tv_title = (TextView) rootView.findViewById(R.id.tv_top_title);
		tv_title.setText(context.getResources().getString(R.string.str_top_per));
		img_icon = (ImageView) rootView.findViewById(R.id.img_user_icon);
		tv_username = (TextView) rootView.findViewById(R.id.tv_user_username);
		tv_user_pay = (TextView) rootView.findViewById(R.id.tv_user_pay);
		ptrl = ((PullToRefreshLayout) rootView
				.findViewById(R.id.refresh_view_shared));
		ptrl.setOnRefreshListener(this);// new MyListener());
		listView = (ListView) rootView.findViewById(R.id.pullLv_shared);

		listView.setOnItemClickListener(this);
		tv_title.setVisibility(View.VISIBLE);
		if (!Conf.NICK.trim().equals("")) {
			tv_username.setText(Conf.NICK);
		}
		if (Conf.VIP)
			tv_user_pay.setText("您已经是会员，无限畅享视频");
		else {
			tv_user_pay.setText("剩余播放次数：" + Conf.OPEN + "，充值即可无限畅享视频盛宴。");
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.zmv.myicon.action");
		intentFilter.addAction("com.zmv.mymsg.action");
		context.registerReceiver(refreshBroadcastReceiver, intentFilter);

		Bitmap bit = BitmapUtils.getCompressImage(BitmapUtils.iconFile, 100,
				100);
		if (bit != null)
			img_icon.setImageBitmap(bit);
		initListView();
		initOnClick();
	}

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub
		initListView();
		// 下拉刷新操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// // 千万别忘了告诉控件刷新完毕了哦！
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub
		// 加载操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 千万别忘了告诉控件加载完毕了哦！
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 2000);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, TalkActivity.class);
		intent.putExtra("person", list_dialog.get(pos));
		context.startActivity(intent);
	}

	private void initOnClick() {
		// TODO Auto-generated method stub
		img_icon.setOnClickListener(this);
		tv_username.setOnClickListener(this);
		tv_user_pay.setOnClickListener(this);
	}

	// 输入昵称
	private void showNickDialog() {
		final Dialog cancelDialog = BasicUtils.showDialog(context,
				R.style.BasicDialog);
		cancelDialog.setContentView(R.layout.dialog_nick);
		final EditText edit_nick = ((EditText) cancelDialog.getWindow()
				.findViewById(R.id.ed_dialog_msg));

		((Button) cancelDialog.getWindow().findViewById(R.id.btn_dialog_cancle))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						cancelDialog.dismiss();
					}
				});
		((Button) cancelDialog.getWindow().findViewById(R.id.btn_dialog_sure))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String nick = edit_nick.getText().toString().trim();
						if (nick.equals("")) {
							Toast.makeText(context, "亲,你要变成无名么？",
									Toast.LENGTH_SHORT).show();
						} else if (BasicUtils.containsEmoji(nick)) {
							Toast.makeText(context, "不能包换特殊字符",
									Toast.LENGTH_SHORT).show();
						} else {
							tv_username.setText(nick);
							UserDAO user = new UserDAO(context);
							user.updateNick(nick);
							cancelDialog.dismiss();
						}
					}
				});
		cancelDialog.setCanceledOnTouchOutside(false);
		cancelDialog.setCancelable(false);
		cancelDialog.show();
	}

	// 弹出选择图片对话框
	private void showChoiceDialog() {
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
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (refreshBroadcastReceiver != null) {
			context.unregisterReceiver(refreshBroadcastReceiver);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.img_user_icon:
			showChoiceDialog();
			break;
		case R.id.tv_user_username:
			showNickDialog();
			break;
		case R.id.tv_user_pay:
			if (!Conf.VIP) {
				if (payUtils == null)
					payUtils = new SMSPayUtils(context, "warning");
				payUtils.initSDK();
			}
			break;
		default:
			break;
		}

	}

	private BroadcastReceiver refreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			try {
				if (action.equals("com.zmv.myicon.action")) {
					if (img_icon != null && MainActivity.mBitmap != null)
						img_icon.setImageBitmap(MainActivity.mBitmap);
					BitmapUtils.getCompressImage(BitmapUtils.targetPictureFile,
							100, 100);
					BitmapUtils.targetPictureFile
							.renameTo(BitmapUtils.iconFile);
				} else if (action.equals("com.zmv.mymsg.action")) {
					initListView();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
}
