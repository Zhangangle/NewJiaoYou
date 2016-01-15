package com.zmv.zf.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.VideoView;

import com.mvjy.zf.R;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.service.MainService;
import com.zmv.zf.utils.ExitManager;

/**
 * 
 * 
 * @author admin
 * 
 */
public class VideoTestActivity extends FragmentActivity {

	VideoView video;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video);
		ExitManager.getScreenManager().pushActivity(this);
		context = VideoTestActivity.this;
		video = (VideoView) findViewById(R.id.video);
		ExitManager.getScreenManager().pushActivity(this);
		String uri = "android.resource://" + getPackageName() + "/" + R.raw.ok;
		video.setVideoURI(Uri.parse(uri));
		video.start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessageDelayed(0, 5000);
			}
		}).start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {

				switch (msg.what) {
				case 0:// 请求网络
					video.canPause();
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			ExitManager.getScreenManager().pullActivity(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
