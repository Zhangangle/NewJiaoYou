package com.zmv.zf.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

import com.zjy.qqjy.zm.R;
import com.zmv.zf.activity.LaunchActivity;
import com.zmv.zf.activity.MainActivity;
import com.zmv.zf.activity.TalkActivity;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.database.DialogDAO;

public class MainService extends Service {
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	private Timer mTimer; // 检测子包启动

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
		setTimerTask();
	}

	private void setTimerTask() {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessageDelayed(0,
						(1 + (int) (Math.random() * 20)) * 1000);
			}
		}, 10000, 180 * 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				try {
					DialogDAO dialog = new DialogDAO(MainService.this);
					BaseJson person = dialog.findTalker();
					showNotify(MainService.this, person.getMsg(), person);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			default:
				break;
			}
		}
	};

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

	public void showNotify(Context context, String message, BaseJson base) {
		// 先设定RemoteViews
		try {

			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			RemoteViews view_custom = new RemoteViews(context.getPackageName(),
					R.layout.view_custom);
			// 设置对应IMAGEVIEW的ID的资源图片
			view_custom
					.setImageViewResource(R.id.custom_icon, R.drawable.logo1);
			view_custom.setTextViewText(R.id.tv_custom_title, base.getName());
			view_custom.setTextViewText(R.id.tv_custom_content, message);
			view_custom.setTextViewText(R.id.tv_custom_time,
					format.format(new Date(System.currentTimeMillis())));
			Builder mBuilder = new Builder(context);
			mBuilder.setContent(view_custom).setAutoCancel(true)
					// 通知产生的时间，会在通知信息里显示
					.setTicker("\"" + base.getName() + "\"向你打招呼啦~")
					.setPriority(1000)// 设置该通知优先级
					// Notification.PRIORITY_DEFAULT
					.setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
					.setSmallIcon(R.drawable.logo1);
			Intent resultIntent;
			if (!MainActivity.flag_status) {
				resultIntent = new Intent(context, LaunchActivity.class);
			} else {
				resultIntent = new Intent(context, TalkActivity.class);
				resultIntent.putExtra("person", base);

			}

			resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
			Notification notify = mBuilder.build();
			notify.defaults |= Notification.DEFAULT_SOUND;
			notify.defaults |= Notification.DEFAULT_LIGHTS;
			notify.contentView = view_custom;
			mNotificationManager.notify(1, notify);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
