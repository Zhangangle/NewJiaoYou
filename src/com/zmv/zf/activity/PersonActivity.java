package com.zmv.zf.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zjy.zf.jj.R;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.adapter.HotGridAdapter;
import com.zmv.zf.adapter.PicGridAdapter;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.database.PersonDAO;
import com.zmv.zf.database.VideoDAO;
import com.zmv.zf.utils.BasicUtils;
import com.zmv.zf.utils.ExitManager;
import com.zmv.zf.utils.ImageUtil;
import com.zmv.zf.utils.ImageUtil.ImageCallback;

@SuppressLint("ResourceAsColor")
public class PersonActivity extends FragmentActivity implements
		OnClickListener, OnItemClickListener {

	private TextView tv_top_title, tv_per_name, tv_per_age, tv_per_say,
			tv_per_playnums, tv_per_flower, tv_per_videos, tv_per_fans,
			tv_per_times;
	private GridView gv_hot, gv_new, gv_pic;
	private PicGridAdapter picAdapter;
	private HotGridAdapter hotAdapter, newAdapter;
	private List<BaseJson> list_hot, list_new;
	private BaseJson base_pic;
	private String[] array_pic;
	private ViewPager mPager;
	private List<View> listViews;// 切换页面
	private Activity context;
	private LinearLayout llayout_top_back, llayout_per_hot, llayout_per_new,
			llayout_per_xiangce;
	private View view_per_hot, view_per_new, view_per_xiangce;
	private ImageView img_top_video, cimg_per_icon;
	int type = 0;
	private Button btn_per_msg, btn_per_guanzhu;
	private BaseJson base_user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person);
		context = PersonActivity.this;
		ExitManager.getScreenManager().pushActivity(this);
		base_user = (BaseJson) getIntent().getSerializableExtra("person");
		initView();
		setVaule();
		initViewPager();
		initOnClick();
		setHotVaule();
	}

	// 单击事件
	private void initOnClick() {
		// TODO Auto-generated method stub
		llayout_top_back.setOnClickListener(this);
		llayout_per_hot.setOnClickListener(this);
		llayout_per_new.setOnClickListener(this);
		llayout_per_xiangce.setOnClickListener(this);
		btn_per_msg.setOnClickListener(this);
		btn_per_guanzhu.setOnClickListener(this);
	}

	/**
	 * 横向滑动
	 */
	private void initViewPager() {
		LayoutInflater mInflater = context.getLayoutInflater();
		listViews = new ArrayList<View>();
		listViews.add(mInflater.inflate(R.layout.layout_perhot, null));
		listViews.add(mInflater.inflate(R.layout.layout_perhot, null));
		listViews.add(mInflater.inflate(R.layout.layout_perpic, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		View mylist_share = listViews.get(0);
		gv_hot = (GridView) mylist_share.findViewById(R.id.gv_perhot);
		View mylist_hot = listViews.get(1);
		gv_new = (GridView) mylist_hot.findViewById(R.id.gv_perhot);
		View mylist_last = listViews.get(2);
		gv_pic = (GridView) mylist_last.findViewById(R.id.gv_perpic);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		gv_hot.setOnItemClickListener(this);
		gv_new.setOnItemClickListener(this);
		gv_pic.setOnItemClickListener(this);

	}

	// 适配器

	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			super();
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mListViews.get(position));
		}

		@Override
		public void finishUpdate(ViewGroup container) {

		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

	}

	// 实现页卡切换监听
	public class MyOnPageChangeListener implements OnPageChangeListener {

		// 当滑动状态改变时调用
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		// 当新的页面被选中时调用
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		// 当新的页面被选中时调用
		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				selectHot();
			} else if (arg0 == 1) {
				selectNew();
			} else {
				selectPic();
			}
		}

	}

	private void selectPic() {
		// TODO Auto-generated method stub
		type = 2;
		setTitleBG(view_per_hot, 0);
		setTitleBG(view_per_new, 0);
		setTitleBG(view_per_xiangce, 1);

		if (list_new != null && list_new.size() > 0 && picAdapter != null) {
			gv_pic.setAdapter(picAdapter);

		} else {
			setPicVaule();
		}
	}

	private void selectNew() {
		// TODO Auto-generated method stub
		type = 1;
		setTitleBG(view_per_hot, 0);
		setTitleBG(view_per_new, 1);
		setTitleBG(view_per_xiangce, 0);

		if (list_new != null && list_new.size() > 0 && newAdapter != null) {
			gv_new.setAdapter(newAdapter);

		} else {
			setNewVaule();
		}
	}

	private void selectHot() {
		// TODO Auto-generated method stub
		type = 0;
		setTitleBG(view_per_hot, 1);
		setTitleBG(view_per_new, 0);
		setTitleBG(view_per_xiangce, 0);

		if (list_hot != null && list_hot.size() > 0 && hotAdapter != null) {
			gv_hot.setAdapter(hotAdapter);

		} else {
			setHotVaule();
		}
	}

	/**
	 * 设置背景颜色
	 * 
	 * @param view
	 * @param check
	 *            1表示被选中 0表示没被选中
	 */
	private void setTitleBG(View view, int check) {
		if (check == 1) {
			BasicUtils.setBgDrawable(context, view, R.drawable.p_line);
		} else {
			BasicUtils.setBgDrawable(context, view, R.color.transparent);
		}
	}

	private void setHotVaule() {
		// TODO Auto-generated method stub
		list_hot = new ArrayList<BaseJson>();
		VideoDAO video = new VideoDAO(context);
		list_hot = video.perData(base_user.getUid());
		hotAdapter = new HotGridAdapter(context, list_hot);
		gv_hot.setAdapter(hotAdapter);

	}

	private void setNewVaule() {
		// TODO Auto-generated method stub
		list_new = new ArrayList<BaseJson>();
		VideoDAO video = new VideoDAO(context);
		list_new = video.perData(base_user.getUid());
		newAdapter = new HotGridAdapter(context, list_new);
		gv_new.setAdapter(hotAdapter);

	}

	private void setPicVaule() {
		// TODO Auto-generated method stub
		PersonDAO per = new PersonDAO(context);
		base_pic = per.picData(base_user.getUid());
		array_pic = base_pic.getPics().split(";");
		picAdapter = new PicGridAdapter(context, array_pic);
		gv_pic.setAdapter(picAdapter);

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		tv_top_title = (TextView) findViewById(R.id.tv_top_title);
		tv_per_name = (TextView) findViewById(R.id.tv_per_name);
		tv_per_age = (TextView) findViewById(R.id.tv_per_age);
		tv_per_say = (TextView) findViewById(R.id.tv_per_say);
		tv_per_playnums = (TextView) findViewById(R.id.tv_per_playnums);
		tv_per_flower = (TextView) findViewById(R.id.tv_per_flower);
		tv_per_videos = (TextView) findViewById(R.id.tv_per_videos);
		tv_per_fans = (TextView) findViewById(R.id.tv_per_fans);
		tv_per_times = (TextView) findViewById(R.id.tv_per_times);
		mPager = (ViewPager) findViewById(R.id.vPager_person);
		llayout_top_back = (LinearLayout) findViewById(R.id.llayout_top_back);
		llayout_per_hot = (LinearLayout) findViewById(R.id.llayout_per_hot);
		llayout_per_new = (LinearLayout) findViewById(R.id.llayout_per_new);
		llayout_per_xiangce = (LinearLayout) findViewById(R.id.llayout_per_xiangce);
		view_per_hot = (View) findViewById(R.id.view_per_hot);
		view_per_new = (View) findViewById(R.id.view_per_new);
		view_per_xiangce = (View) findViewById(R.id.view_per_xiangce);
		img_top_video = (ImageView) findViewById(R.id.img_top_video);
		cimg_per_icon = (ImageView) findViewById(R.id.cimg_per_icon);
		btn_per_msg = (Button) findViewById(R.id.btn_per_msg);
		btn_per_guanzhu = (Button) findViewById(R.id.btn_per_guanzhu);

	}

	private void setVaule() {
		tv_top_title.setVisibility(View.VISIBLE);
		img_top_video.setVisibility(View.VISIBLE);
		llayout_top_back.setVisibility(View.VISIBLE);
		tv_top_title.setText(getResources().getString(R.string.str_top_person));
		tv_per_name.setText(base_user.getName());
		tv_per_age.setText(base_user.getAge() + "岁，" + base_user.getAddress());
		tv_per_say.setText("个性签名：" + base_user.getIntro());
		tv_per_playnums.setText(base_user.getPlaynums() + "次播放");
		tv_per_flower.setText(base_user.getMsgnum() + "个送花");
		tv_per_videos.setText(base_user.getVideonums() + "个视频");
		tv_per_fans.setText(base_user.getFans() + "个关注");
		tv_per_times.setText(base_user.getOnline());
		ImageUtil.loadImage(
				ImageUtil.getCacheImgPath()
						+ base_user.getBigicon().substring(
								base_user.getBigicon().lastIndexOf("/") + 1,
								base_user.getBigicon().lastIndexOf(".")),
				base_user.getBigicon(), new ImageCallback() {

					@Override
					public void loadImage(Bitmap bitmap, String imagePath) {
						// TODO Auto-generated method stub
						if (bitmap != null) {
							cimg_per_icon.setImageBitmap(bitmap);
						}
					}
				});
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
		case R.id.llayout_per_hot:
			if (type != 0) {
				mPager.setCurrentItem(0);
			}
			break;
		case R.id.llayout_per_new:
			if (type != 1) {
				mPager.setCurrentItem(1);
			}
			break;
		case R.id.llayout_per_xiangce:
			if (type != 2) {
				mPager.setCurrentItem(2);
			}
			break;
		case R.id.btn_per_guanzhu:
			Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_per_msg:
			Intent intent = new Intent(context, TalkActivity.class);
			base_user.setIcon(base_user.getBigicon());
			intent.putExtra("person", base_user);
			context.startActivity(intent);
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
			ExitManager.getScreenManager().pullActivity(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		if (type == 0) {
			Intent intent = new Intent(context, DetailActivity.class);
			intent.putExtra("person", hotAdapter.getAllData().get(pos));
			context.startActivity(intent);
		} else if (type == 1) {
			Intent intent = new Intent(context, DetailActivity.class);
			intent.putExtra("person", hotAdapter.getAllData().get(pos));
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(context, ViewPageActivity.class);
			intent.putExtra("image", base_pic.getBigPics().split(";"));
			intent.putExtra("pos", pos);
			context.startActivity(intent);
		}
	}

}
