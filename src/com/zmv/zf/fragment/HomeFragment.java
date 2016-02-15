package com.zmv.zf.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qqjy.zm.R;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.activity.DetailActivity;
import com.zmv.zf.activity.PersonActivity;
import com.zmv.zf.adapter.HotGridAdapter;
import com.zmv.zf.adapter.ShareListAdapter;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.database.DialogDAO;
import com.zmv.zf.database.PersonDAO;
import com.zmv.zf.database.VideoDAO;
import com.zmv.zf.utils.BasicUtils;
import com.zmv.zf.view.PullToRefreshLayout;
import com.zmv.zf.view.PullToRefreshLayout.OnRefreshListener;

public class HomeFragment extends Fragment implements OnRefreshListener,
		OnClickListener, OnItemClickListener {

	private TextView tv_share;
	private TextView tv_hot;
	private TextView tv_last;
	private ListView lv_share;
	private GridView gv_hot, gv_last;
	private int hotpage = 1, lastpage = 1;
	private ShareListAdapter shareAdapter;
	private HotGridAdapter hotAdapter, lastAdapter;
	private List<BaseJson> list_share, list_hot, list_last;
	private ViewPager mPager;
	private LinearLayout llayout_home;
	private List<View> listViews;// 切换页面
	private PullToRefreshLayout pull_share, pull_hot, pull_last;

	int type = 0;
	private Activity context;

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		findView(rootView);

		return rootView;
	}

	private void findView(View rootView) {
		if (context == null)
			context = getActivity();
		tv_share = (TextView) rootView.findViewById(R.id.tv_top_share);
		tv_hot = (TextView) rootView.findViewById(R.id.tv_top_hot);
		tv_last = (TextView) rootView.findViewById(R.id.tv_top_last);
		mPager = (ViewPager) rootView.findViewById(R.id.vPager_home);
		llayout_home = (LinearLayout) rootView.findViewById(R.id.llayout_home);

		llayout_home.setVisibility(View.VISIBLE);
		initViewPager();
		initOnClick();
		setShareVaule();

	}

	private void setShareVaule() {
		// TODO Auto-generated method stub
		list_share = new ArrayList<BaseJson>();
		PersonDAO video = new PersonDAO(context);
		list_share = video.shareData();
		// BaseJson base;
		// for (int i = 0; i < 6; i++) {
		// base = new BaseJson();
		// base.setUid("1111");
		// list_share.add(base);
		// }
		if (list_share != null && list_share.size() > 0) {
			shareAdapter = new ShareListAdapter(context, list_share);
			lv_share.setAdapter(shareAdapter);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
					DialogDAO dialog = new DialogDAO(context);
					dialog.addListDialog(list_share);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();

	}

	private void setHotVaule() {
		// TODO Auto-generated method stub
		list_hot = new ArrayList<BaseJson>();
		VideoDAO video = new VideoDAO(context);
		list_hot = video.playData();
		if (list_hot != null && list_hot.size() > 0)
			hotAdapter = new HotGridAdapter(context, list_hot);
		gv_hot.setAdapter(hotAdapter);

	}

	private void setLastVaule() {
		// TODO Auto-generated method stub
		list_last = new ArrayList<BaseJson>();
		VideoDAO video = new VideoDAO(context);
		list_last = video.playData();
		if (list_last != null && list_last.size() > 0) {
			lastAdapter = new HotGridAdapter(context, list_last);
			gv_last.setAdapter(lastAdapter);
		}

	}

	private void initOnClick() {
		// TODO Auto-generated method stub
		tv_share.setOnClickListener(this);
		tv_hot.setOnClickListener(this);
		tv_last.setOnClickListener(this);
		lv_share.setOnItemClickListener(this);
		gv_hot.setOnItemClickListener(this);
		gv_last.setOnItemClickListener(this);
		pull_share.setOnRefreshListener(this);
		pull_hot.setOnRefreshListener(this);
		pull_last.setOnRefreshListener(this);

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

	/**
	 * 横向滑动
	 */
	private void initViewPager() {
		LayoutInflater mInflater = context.getLayoutInflater();
		listViews = new ArrayList<View>();
		listViews.add(mInflater.inflate(R.layout.layout_share, null));
		listViews.add(mInflater.inflate(R.layout.layout_hot, null));
		listViews.add(mInflater.inflate(R.layout.layout_last, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		View mylist_share = listViews.get(0);
		pull_share = (PullToRefreshLayout) mylist_share
				.findViewById(R.id.refresh_view_share);

		lv_share = (ListView) mylist_share.findViewById(R.id.pullLv_share);
		View mylist_hot = listViews.get(1);
		pull_hot = (PullToRefreshLayout) mylist_hot
				.findViewById(R.id.refresh_view_hot);

		gv_hot = (GridView) mylist_hot.findViewById(R.id.pullgv_hot);
		View mylist_last = listViews.get(2);
		pull_last = (PullToRefreshLayout) mylist_last
				.findViewById(R.id.refresh_view_last);

		gv_last = (GridView) mylist_last.findViewById(R.id.pullgv_last);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

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
				selectShare();
			} else if (arg0 == 1) {
				selectHot();
			} else {
				selectLast();
			}
		}
	}

	private void selectShare() {
		type = 0;
		setTitleColor(tv_share, 1);
		setTitleColor(tv_hot, 0);
		setTitleColor(tv_last, 0);
		setTitleBg(llayout_home, R.drawable.topic_top_icon1);

		if (list_share != null && list_share.size() > 0 && shareAdapter != null) {
			lv_share.setAdapter(shareAdapter);
		} else {
			setShareVaule();
		}
	}

	private void selectHot() {
		type = 1;
		setTitleColor(tv_share, 0);
		setTitleColor(tv_hot, 1);
		setTitleColor(tv_last, 0);
		setTitleBg(llayout_home, R.drawable.topic_top_icon2);

		if (list_hot != null && list_hot.size() > 0 && hotAdapter != null) {
			gv_hot.setAdapter(hotAdapter);
		} else {
			setHotVaule();
		}
	}

	private void selectLast() {
		type = 2;
		setTitleColor(tv_share, 0);
		setTitleColor(tv_hot, 0);
		setTitleColor(tv_last, 1);
		setTitleBg(llayout_home, R.drawable.topic_top_icon3);

		if (list_last != null && list_last.size() > 0 && lastAdapter != null) {
			gv_last.setAdapter(lastAdapter);

		} else {
			setLastVaule();
		}
	}

	/**
	 * 设置背景颜色
	 * 
	 * @param view
	 * @param check
	 *            1表示被选中 0表示没被选中
	 */
	private void setTitleColor(TextView view, int check) {
		if (check == 1) {
			view.setTextColor(context.getResources().getColor(R.color.white));
		} else {
			view.setTextColor(context.getResources().getColor(R.color.lightred));
		}
	}

	private void setTitleBg(View view, int id) {
		BasicUtils.setBgDrawable(context, view, id);
	}

	@Override
	public void onStart() {
		// myImg_ad.startTimer();
		super.onStart();

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub
		if (type == 0) {
			setShareVaule();
		} else if (type == 1) {
			hotpage = 1;
			setHotVaule();
		} else {
			lastpage = 1;
			setLastVaule();
		}
		// 下拉刷新操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 千万别忘了告诉控件刷新完毕了哦！
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 1000);
		/*
		 * pullscroll_home.post(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * pullscroll_home.scrollTo(0, 0); } });
		 */
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub

		if (type == 0) {
			// shareAdapter.insertData(newitem);
			// lv_share.requestLayout();// 页面添加更快
			// shareAdapter.notifyDataSetChanged();
		} else {
			if (type == 1 && hotpage <= 3) {
				List<BaseJson> newitem = new ArrayList<BaseJson>();
				VideoDAO video = new VideoDAO(context);
				newitem = video.playData();
				if (newitem != null && newitem.size() > 0) {
					if (type == 1) {
						hotpage += 1;
						hotAdapter.insertData(newitem);
						gv_hot.requestLayout();
					}
				}
			}
			if (type == 2 && lastpage <= 3) {
				List<BaseJson> newitem = new ArrayList<BaseJson>();
				VideoDAO video = new VideoDAO(context);
				newitem = video.playData();
				lastpage += 1;
				lastAdapter.insertData(newitem);
				gv_last.requestLayout();
			}

		}
		// 加载操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 千万别忘了告诉控件加载完毕了哦！
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.tv_top_share:// 推荐
			if (type != 0) {
				mPager.setCurrentItem(0);
			}
			break;
		case R.id.tv_top_hot:// 最热
			if (type != 1) {
				mPager.setCurrentItem(1);
			}
			break;
		case R.id.tv_top_last:// 最新
			if (type != 2) {
				mPager.setCurrentItem(2);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		if (type == 0) {
			Intent intent = new Intent(context, PersonActivity.class);
			intent.putExtra("person", shareAdapter.getAllData().get(pos));
			context.startActivity(intent);
		} else if (type == 1) {
			Intent intent = new Intent(context, DetailActivity.class);
			intent.putExtra("person", hotAdapter.getAllData().get(pos));
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(context, DetailActivity.class);
			intent.putExtra("person", lastAdapter.getAllData().get(pos));
			context.startActivity(intent);
		}
	}

}
