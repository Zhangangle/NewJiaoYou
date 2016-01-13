package com.zmv.zf.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hihi.jy.R;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.adapter.DetailListAdapter;
import com.zmv.zf.adapter.HotGridAdapter;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.PersonDAO;
import com.zmv.zf.database.UserDAO;
import com.zmv.zf.database.VideoDAO;
import com.zmv.zf.pay.SMSPayUtils;
import com.zmv.zf.utils.BasicUtils;
import com.zmv.zf.utils.ExitManager;
import com.zmv.zf.utils.ImageUtil;
import com.zmv.zf.utils.ImageUtil.ImageCallback;
import com.zmv.zf.view.CircleImageView;
import com.zmv.zf.view.MyGridView;
import com.zmv.zf.view.MyListView;

@SuppressLint("ResourceAsColor")
public class DetailActivity extends FragmentActivity implements
		OnClickListener, OnItemClickListener {

	private TextView tv_top_title, tv_detail_nick, tv_detail_msg;
	private MyGridView gv_more;
	private MyListView lv_detail;
	private DetailListAdapter detailAdapter;
	private HotGridAdapter moreAdapter;
	private List<BaseJson> list_more, list_talk;
	private Activity context;
	private CircleImageView cimg_per_icon;

	private LinearLayout llayout_top_back;
	private SurfaceView mSurfaceView;
	private SeekBar seekbar;
	private ImageView img_select_icon, img_video_play, img_detail_like;
	private EditText ed_detail_talk;
	private Button btn_detail_msg, btn_detail_send;
	private MediaPlayer mediaPlayer; // 播放器控件
	private int postSize; // 保存义播视频大小
	private boolean flag_play = true; // 用于判断视频是否在播放中
	private boolean display; // 用于是否显示其他按钮
	private upDateSeekBar update; // 更新进度条用
	private String url;
	// private boolean flag_canplay = true;
	private BaseJson base_user;
	int pay = 0;
	SMSPayUtils payUtils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		context = DetailActivity.this;
		ExitManager.getScreenManager().pushActivity(this);
		base_user = (BaseJson) getIntent().getSerializableExtra("person");
		initView();
		setvaule();
		initOnClick();
		setMoreVaule();
		setDetailVaule();
		pay = Conf.OPEN;
	}

	// 单击事件
	private void initOnClick() {
		// TODO Auto-generated method stub
		llayout_top_back.setOnClickListener(this);
		mSurfaceView.setOnClickListener(this);
		// seekbar
		btn_detail_msg.setOnClickListener(this);
		btn_detail_send.setOnClickListener(this);
		img_video_play.setOnClickListener(this);
		img_detail_like.setOnClickListener(this);
		gv_more.setOnItemClickListener(this);
		mediaPlayer
				.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
					}
				});

		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // 视频播放完成
					@Override
					public void onCompletion(MediaPlayer mp) {
						flag_play = false;
						img_video_play.setVisibility(View.VISIBLE);
					}
				});

		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {

			}
		});
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int value = seekbar.getProgress() * mediaPlayer.getDuration() // 计算进度条需要前进的位置数据大小
						/ seekbar.getMax();
				mediaPlayer.seekTo(value);

				if (!flag_play) {
					img_video_play.setVisibility(View.GONE);
					flag_play = true;
					new Thread(update).start();
					mediaPlayer.start();
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
	}

	private void setMoreVaule() {
		// TODO Auto-generated method stub
		list_more = new ArrayList<BaseJson>();
		VideoDAO video = new VideoDAO(context);
		list_more = video.perData(base_user.getUid());
		moreAdapter = new HotGridAdapter(context, list_more);
		gv_more.setAdapter(moreAdapter);

	}

	private void setDetailVaule() {
		list_talk = new ArrayList<BaseJson>();
		BaseJson base;
		for (int i = 0; i < 8; i++) {
			base = new BaseJson();
			base.setUid("1111");
			list_talk.add(base);
		}
		detailAdapter = new DetailListAdapter(context, list_more);
		lv_detail.setAdapter(detailAdapter);

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		tv_top_title = (TextView) findViewById(R.id.tv_top_title);

		llayout_top_back = (LinearLayout) findViewById(R.id.llayout_top_back);

		cimg_per_icon = (CircleImageView) findViewById(R.id.cimg_detail_avatar);
		tv_detail_nick = (TextView) findViewById(R.id.tv_detail_nick);
		tv_detail_msg = (TextView) findViewById(R.id.tv_detail_msg);
		gv_more = (MyGridView) findViewById(R.id.gv_detail_video);
		lv_detail = (MyListView) findViewById(R.id.Lv_detail_say);
		gv_more.setFocusable(false);
		lv_detail.setFocusable(false);
		mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		img_select_icon = (ImageView) findViewById(R.id.img_select_icon);
		img_video_play = (ImageView) findViewById(R.id.img_video_play);
		img_detail_like = (ImageView) findViewById(R.id.img_detail_like);
		ed_detail_talk = (EditText) findViewById(R.id.ed_detail_talk);
		btn_detail_msg = (Button) findViewById(R.id.btn_detail_msg);
		btn_detail_send = (Button) findViewById(R.id.btn_detail_send);

	}

	private void setvaule() {

		tv_detail_msg.setText(base_user.getIntro());
		tv_detail_nick.setText(base_user.getName());
		url = base_user.getVideourl();
		PersonDAO person = new PersonDAO(context);
		// Log.e("icon", person.userIcon(base_user.getUid()));
		String userIcon = person.userIcon(base_user.getUid());
		if (userIcon != null)
			base_user.setIcon(userIcon);
		else
			base_user.setIcon(base_user.getVideoimg());
		// Log.e("pic", base_user.getIcon());
		ImageUtil.loadImage(
				ImageUtil.getCacheImgPath()
						+ base_user.getIcon().substring(
								base_user.getIcon().lastIndexOf("/") + 1,
								base_user.getIcon().lastIndexOf(".")),
				base_user.getIcon(), new ImageCallback() {

					@Override
					public void loadImage(Bitmap bitmap, String imagePath) {
						// TODO Auto-generated method stub
						if (bitmap != null) {
							cimg_per_icon.setImageBitmap(bitmap);
						}
					}
				});
		ImageUtil.loadImage(
				ImageUtil.getCacheImgPath()
						+ base_user.getVideoimg().substring(
								base_user.getVideoimg().lastIndexOf("/") + 1,
								base_user.getVideoimg().lastIndexOf(".")),
				base_user.getVideoimg(), new ImageCallback() {

					@Override
					public void loadImage(Bitmap bitmap, String imagePath) {
						// TODO Auto-generated method stub
						if (bitmap != null) {
							img_select_icon.setImageBitmap(bitmap);
						}
					}
				});
		tv_top_title.setVisibility(View.VISIBLE);
		llayout_top_back.setVisibility(View.VISIBLE);
		tv_top_title
				.setText(getResources().getString(R.string.str_detial_title));
		mediaPlayer = new MediaPlayer(); // 创建一个播放器对象
		update = new upDateSeekBar(); // 创建更新进度条对象
		playVideo(url);
	}

	private void playVideo(String video_url) {
		// TODO Auto-generated method stub
		url = video_url;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)// 低于4.0版本
			mSurfaceView.getHolder().setType(
					SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // 不缓冲
		mSurfaceView.getHolder().setKeepScreenOn(true); // 保持屏幕高亮
		mSurfaceView.getHolder().addCallback(new surFaceView());

	}

	private void playOnClick() {
		if (!first_play) {
			first_play = true;
			setDialogView(context);
			new PlayMovie(0).start();
		} else {
			if (url != null && !url.equals("")) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					postSize = mediaPlayer.getCurrentPosition();
				} else {
					// if (!flag_canplay) {
					// Toast.makeText(context, "该视频暂时无法播放", 1000).show();
					// return;
					// }
					if (!flag_play) {
						flag_play = true;
						new Thread(update).start();
					}
					mediaPlayer.start();
					if (flag_play)
						img_video_play.setVisibility(View.GONE);
				}
			}
		}

	}

	class PlayMovie extends Thread { // 播放视频的线程

		int post = 0;

		public PlayMovie(int post) {
			this.post = post;

		}

		@Override
		public void run() {
			Message message = Message.obtain();
			Looper.prepare();
			try {
				mediaPlayer.reset(); // 回复播放器默认
				mediaPlayer.setDataSource(url); // 设置播放路径
				mediaPlayer.setDisplay(mSurfaceView.getHolder()); // 把视频显示在SurfaceView上
				mediaPlayer.setOnPreparedListener(new Ok(post)); // 设置监听事件
				mediaPlayer.prepare(); // 准备播放

				// Log.e("ee1", "ee1");
			} catch (Exception e) {
				first_play = false;
				handler.sendEmptyMessage(1);

			} finally {
				handler.sendEmptyMessage(0);
				Looper.loop();
			}

			super.run();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mDialog != null)
					mDialog.dismiss();
				break;
			case 1:
				Toast.makeText(context, "请检查网络，暂时无法播放", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		}
	};

	class Ok implements OnPreparedListener {
		int postSize;

		public Ok(int postSize) {
			this.postSize = postSize;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			// 准备完成后，隐藏控件
			// Log.e("e", "sdsd");
			if (!Conf.VIP && pay == Conf.OPEN && Conf.OPEN > 0) {
				UserDAO user = new UserDAO(context);
				Conf.OPEN = Conf.OPEN - 1;
				user.updateOpen(Conf.OPEN);

			}
			// Log.e("conf.open", Conf.OPEN + "");
			img_select_icon.setVisibility(View.INVISIBLE);
			img_select_icon.setFocusable(false);
			img_video_play.setVisibility(View.GONE);
			seekbar.setVisibility(View.GONE);
			mSurfaceView.setVisibility(View.VISIBLE);
			img_video_play.setEnabled(true);
			display = false;
			if (mediaPlayer != null) {
				mediaPlayer.start(); // 开始播放视频
			} else {
				return;
			}
			if (postSize > 0) { // 说明中途停止过（activity调用过pase方法，不是用户点击停止按钮），跳到停止时候位置开始播放
				mediaPlayer.seekTo(postSize); // 跳到postSize大小位置处进行播放
			}
			new Thread(update).start(); // 启动线程，更新进度条
		}
	}

	private class surFaceView implements Callback { // 上面绑定的监听的事件

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) { // 创建完成后调用
			if (postSize > 0 && url != null && !url.equals("")) { // 说明，停止过activity调用过pase方法，跳到停止位置播放
				new PlayMovie(postSize).start();
				flag_play = true;
				int sMax = seekbar.getMax();
				int mMax = mediaPlayer.getDuration();
				seekbar.setProgress(postSize * sMax / mMax);
				postSize = 0;
			} else {// 没有地址
				// new PlayMovie(0).start(); // 表明是第一次开始播放
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) { // activity调用过pase方法，保存当前播放位置
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				postSize = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
				flag_play = false;
			}
		}
	}

	/**
	 * 更新进度条
	 */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mediaPlayer == null) {
				flag_play = false;
			} else if (mediaPlayer.isPlaying()) {
				flag_play = true;
				int position = mediaPlayer.getCurrentPosition();
				int mMax = mediaPlayer.getDuration();
				int sMax = seekbar.getMax();
				seekbar.setProgress(position * sMax / mMax);
			} else {
				return;
			}
		};
	};

	class upDateSeekBar implements Runnable {

		@Override
		public void run() {
			mHandler.sendMessage(Message.obtain());
			if (flag_play) {
				mHandler.postDelayed(update, 200);
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		try {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				postSize = mediaPlayer.getCurrentPosition();
				img_video_play.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();

		if (isOpen) {
			imm.hideSoftInputFromWindow(ed_detail_talk.getWindowToken(), 0);
		}

	}

	boolean first_play = false;

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.llayout_top_back:
			finish();
			break;
		case R.id.mSurfaceView:
			if (display) {

				img_select_icon.setVisibility(View.INVISIBLE);
				img_select_icon.setFocusable(false);
				if (flag_play)
					img_video_play.setVisibility(View.GONE);
				seekbar.setVisibility(View.GONE);
				display = false;
			} else {
				// img_video_play.setVisibility(View.VISIBLE);
				mSurfaceView.setVisibility(View.VISIBLE);
				seekbar.setVisibility(View.VISIBLE);
				/**
				 * 设置播放为全屏
				 */
				// ViewGroup.LayoutParams lp =
				// mSurfaceView.getLayoutParams();
				// lp.height = LayoutParams.MATCH_PARENT;
				// lp.width = LayoutParams.MATCH_PARENT;
				// mSurfaceView.setLayoutParams(lp);
				display = true;
			}
			break;
		case R.id.btn_detail_msg:
			Intent intent = new Intent(context, TalkActivity.class);
			intent.putExtra("person", base_user);
			context.startActivity(intent);
			break;
		case R.id.btn_detail_send:
			Toast.makeText(context, "暂时无法评论", Toast.LENGTH_SHORT).show();
			closeInputMethod();
			ed_detail_talk.setText("");
			break;
		// seekbar
		case R.id.img_video_play:
			if (Conf.VIP || pay > 0 || Conf.OPEN > 0) {
				MobclickAgent.onEvent(context, "video_count");
				if (!Conf.VIP && pay >= Conf.OPEN && Conf.OPEN > 0)
					Toast.makeText(context, "剩余播放次数:" + pay, Toast.LENGTH_LONG)
							.show();
				playOnClick();
			} else {
				MobclickAgent.onEvent(context, "video_request");
				if (payUtils == null)
					payUtils = new SMSPayUtils(context, "shipin");
				payUtils.initSDK();
			}
			// if (Conf.user_VIP.equals("1") && !Conf.userID.equals("122312")) {
			// if (!BasicUtils.isInstallApk(ConfigUtils.packname)
			// && ConfigUtils.isdownplug) {
			// pluginNewDialog();
			// } else {
			// playOnClick();
			// }
			// return;
			// }
			// int count = PreferenceHelper.readInt(context, "read", "count");
			// if (count >= 2) {
			// if (!flag_pay) {
			// Toast.makeText(context, "超过免费播放次数,请充值", 2000).show();
			// handler.sendEmptyMessageDelayed(0, 500);
			// flag_pay = true;
			// } else if (!BasicUtils.isInstallApk(ConfigUtils.packname)
			// && ConfigUtils.isdownplug) {
			// pluginNewDialog();
			// Toast.makeText(context, "超过免费播放次数...", 2000).show();
			// } else {
			// playOnClick();
			// }
			// // handler.sendEmptyMessageDelayed(0, 500);
			// } else {
			// 表明是第一次开始播放)
			// playOnClick();
			// }
			break;
		case R.id.img_detail_like:
			Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
			ExitManager.getScreenManager().pullActivity(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				postSize = mediaPlayer.getCurrentPosition();
				img_video_play.setVisibility(View.VISIBLE);
			}
			Intent intent = new Intent(context, DetailActivity.class);
			intent.putExtra("person", moreAdapter.getAllData().get(pos));
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
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
