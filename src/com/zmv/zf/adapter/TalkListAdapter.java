package com.zmv.zf.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qqjy.zm.R;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.BitmapUtils;
import com.zmv.zf.utils.ImageUtil;
import com.zmv.zf.utils.ImageUtil.ImageCallback;

/**
 * @author
 */
public class TalkListAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_talk;

	public TalkListAdapter(Context context, List<BaseJson> list_talk) {
		this.context = context;
		this.list_talk = list_talk;
	}

	@Override
	public int getCount() {
		return list_talk.size();
	}

	@Override
	public Object getItem(int position) {
		return list_talk.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void insertData(List<BaseJson> list_add) {
		list_talk.addAll(list_add);
	}

	public List<BaseJson> getAllData() {
		return list_talk;

	}

	@Override
	public int getItemViewType(int position) {
		BaseJson base = list_talk.get(position);
		if (base.getType() == 0) {
			return 0;
		}
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public void insert(BaseJson user) {
		this.list_talk.add(user);
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {
		BaseJson user = list_talk.get(pos);

		if (convertView == null) {
			// 通过ItemType设置不同的布局
			if (getItemViewType(pos) == 0) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.list_item_talkta, parent, false);
				final ImageView img_icon = BaseAdapterHelper.get(convertView,
						R.id.img_talk_taicon);
				final ImageView img_pic = BaseAdapterHelper.get(convertView,
						R.id.img_talk_tapic);
				TextView tv_talk_time = BaseAdapterHelper.get(convertView,
						R.id.tv_talk_tatime);
				TextView tv_talk_msg = BaseAdapterHelper.get(convertView,
						R.id.tv_talk_tamsg);
				String url = user.getIcon();
				// Log.e("url", pos + "=" + url);
				ImageUtil.loadImage(
						ImageUtil.getCacheImgPath()
								+ url.substring(url.lastIndexOf("/") + 1,
										url.lastIndexOf(".")), url,
						new ImageCallback() {

							@Override
							public void loadImage(Bitmap bitmap,
									String imagePath) {
								// TODO Auto-generated method stub
								if (bitmap != null) {
									img_icon.setImageBitmap(bitmap);
								}
							}
						});
				if (user.getBigicon() != null && !user.getBigicon().equals(""))
					ImageUtil
							.loadImage(
									ImageUtil.getCacheImgPath()
											+ user.getBigicon()
													.substring(
															user.getBigicon()
																	.lastIndexOf(
																			"/") + 1,
															user.getBigicon()
																	.lastIndexOf(
																			".")),
									user.getBigicon(), new ImageCallback() {

										@Override
										public void loadImage(Bitmap bitmap,
												String imagePath) {
											// TODO Auto-generated method stub
											if (bitmap != null) {
												img_pic.setImageBitmap(bitmap);
												int height = (int) (0.5
														* Conf.width
														* bitmap.getHeight() / bitmap
														.getWidth());
												img_pic.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
														(int) (0.5 * Conf.width),
														height));
											}
										}
									});
				else
					img_pic.setVisibility(View.GONE);
				tv_talk_time.setText(user.getFormat_time());
				if (user.getMsg() != null && !user.getMsg().equals("")) {
					tv_talk_msg.setText(user.getMsg());
					tv_talk_msg.setVisibility(View.VISIBLE);
				} else
					tv_talk_msg.setVisibility(View.GONE);
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.list_item_talkme, parent, false);
				ImageView img_icon = BaseAdapterHelper.get(convertView,
						R.id.img_talk_meicon);
				ImageView img_pic = BaseAdapterHelper.get(convertView,
						R.id.img_talk_mepic);
				TextView tv_talk_time = BaseAdapterHelper.get(convertView,
						R.id.tv_talk_metime);
				TextView tv_talk_msg = BaseAdapterHelper.get(convertView,
						R.id.tv_talk_memsg);
				// Log.e("url", pos + "=" +
				// BitmapUtils.iconFile.getAbsolutePath());
				Bitmap bit = BitmapUtils.getCompressImage(BitmapUtils.iconFile,
						100, 100);
				if (bit != null)
					img_icon.setImageBitmap(bit);
				if (user.getBigicon() != null && !user.getBigicon().equals("")) {
					Bitmap bit2 = BitmapUtils
							.getCompressImage(
									BitmapUtils.getPicPath(user.getBigicon()),
									150, 150);
					if (bit2 != null) {
						img_pic.setImageBitmap(bit2);
						img_pic.setVisibility(View.VISIBLE);
					}
				} else {
					img_pic.setVisibility(View.GONE);
				}

				tv_talk_time.setText(user.getFormat_time());
				if (user.getMsg() != null && !user.getMsg().equals("")) {
					tv_talk_msg.setText(user.getMsg());
					tv_talk_msg.setVisibility(View.VISIBLE);
				} else
					tv_talk_msg.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
}
