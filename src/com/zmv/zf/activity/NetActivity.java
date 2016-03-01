package com.zmv.zf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wsug.zjy.mm.R;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.utils.ExitManager;
import com.zmv.zf.utils.NetworkUtils;

public class NetActivity extends FragmentActivity {

	private Button sure;
	private EditText ed_dialog_msg;
	private TextView tv_dialog_open;
	private Button cancel;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_nick);
		context = NetActivity.this;
		ExitManager.getScreenManager().pushActivity(this);
		initView();
		setOnclick();
	}

	private void setOnclick() {
		// TODO Auto-generated method stub
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.zmv.myfinish.action");
				context.sendBroadcast(intent);
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
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		sure = (Button) findViewById(R.id.btn_dialog_sure);
		ed_dialog_msg = (EditText) findViewById(R.id.ed_dialog_msg);
		tv_dialog_open = (TextView) findViewById(R.id.tv_dialog_open);
		cancel = (Button) findViewById(R.id.btn_dialog_cancle);
		cancel.setText("退出");
		sure.setText("开启");
		ed_dialog_msg.setVisibility(View.GONE);
		tv_dialog_open.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			// LiBaoPayUtils.getInstance(SignActivity.this).cleanLiBao();
			ExitManager.getScreenManager().pullActivity(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

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

}
