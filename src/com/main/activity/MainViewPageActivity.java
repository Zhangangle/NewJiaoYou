package com.main.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wangm.ncj.R;
import com.umeng.analytics.MobclickAgent;
import com.zmv.zf.utils.ExitManager;
import com.zmv.zf.utils.ImageLoader;
import com.zmv.zf.utils.ImageLoader.Type;

/**
 * 滑动引导页面
 * 
 * @author admin
 * 
 */
public class MainViewPageActivity extends FragmentActivity {
	private ViewPager mPager;
	private List<View> listViews;
	private int offset = 0;
	private int bmpw;
	private String[] list;
	private int pos;
	private ImageLoader mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viewpage_image);
		ExitManager.getScreenManager().pushActivity(this);
		Intent intent = getIntent();
		list = (String[]) getIntent().getSerializableExtra("image");
		pos = intent.getIntExtra("pos", 0);
		mImageLoader = ImageLoader.getInstance(3, Type.LIFO);

		IntiViewPager();
		
	}

	/**
	 * 初始化滑动控件
	 * 
	 * @author admin
	 * 
	 */
	Bitmap bitmap;

	private void IntiViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPagerintroduce);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		View view;

		for (int i = 0; i < list.length; i++) {
			view = mInflater.inflate(R.layout.viewpage_item_image, null);
			listViews.add(view);
			final ImageView img = (ImageView) view
					.findViewById(R.id.img_viewpage_icon);
			try {
				String url = list[i];
				mImageLoader.loadImage(url, img, true);
				// ImageUtil.loadImage(
				// ImageUtil.getCacheImgPath()
				// + url.substring(url.lastIndexOf("/") + 1,
				// url.lastIndexOf(".")), url,
				// new ImageCallback() {
				//
				// @Override
				// public void loadImage(Bitmap bitmap,
				// String imagePath) {
				// // TODO Auto-generated method stub
				// if (bitmap != null) {
				// img.setImageBitmap(bitmap);
				// int height = (Conf.width * bitmap
				// .getHeight()) / bitmap.getWidth();
				// img.setLayoutParams(new LinearLayout.LayoutParams(
				// android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				// height));
				// }
				// }
				// });
				img.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			} catch (Exception e) {
				// TODO: handle exception
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

		}
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(pos);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {

			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);

		}

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

	// 初始化动画
	// private void InitImageView() {
	//
	// bmpw = Conf.width;// 获取图片宽度
	// DisplayMetrics dm = new DisplayMetrics();
	// getWindowManager().getDefaultDisplay().getMetrics(dm);
	// int screenW = dm.widthPixels; // 获取分辨率的宽度
	// if (list.size() == 1) {
	// offset = 0;
	// } else
	// offset = (screenW / list.size() - bmpw) / (list.size() - 1);// 计算偏移值
	// Matrix matrix = new Matrix();
	// matrix.postTranslate(offset, 0);
	// }

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
