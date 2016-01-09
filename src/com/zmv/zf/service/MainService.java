package com.zmv.zf.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.zmv.zf.common.Conf;

public class MainService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 获取公网IP地址
		getPublicNetwork();

	}

	// 获取公网IP地址
	private void getPublicNetwork() {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String url = "http://pv.sohu.com/cityjson?ie=utf-8";
					URL infoUrl = new URL(url);
					URLConnection connection = infoUrl.openConnection();
					HttpURLConnection httpConnection = (HttpURLConnection) connection;
					int responseCode = httpConnection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						InputStream inStream = httpConnection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(inStream, "utf-8"));
						String line = null;
						while ((line = reader.readLine()) != null) {
							JSONObject json = new JSONObject(line.substring(
									line.indexOf("{"), line.indexOf("}") + 1));
							Conf.PublicNetwork = json.getString("cip");
							Conf.Address = json.getString("cname");
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopForeground(true);
	}

}
