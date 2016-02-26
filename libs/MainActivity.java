package com.ym.activity;

import A.B.C.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.yimeng.ympay.in.PayCalBackListener;
import com.android.yimeng.ympay.pay.YMPay;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		YMPay.getInstance(MainActivity.this).init(MainActivity.this);
	}

	public void onClick(View view) {
		YMPay.getInstance(MainActivity.this).pay(353, "ym", MainActivity.this,
				new PayCalBackListener() {

					@Override
					public void success(int code) {
						Log.e("MainActivity", "pay to sucess code = " + code);
					}

					@Override
					public void fail(int code) {
						Log.e("MainActivity", "fail code = " + code);
					}
				});
	}
}
