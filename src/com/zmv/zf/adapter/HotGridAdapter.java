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

import com.zmv.zf.R;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.utils.ImageUtil;
import com.zmv.zf.utils.ImageUtil.ImageCallback;

/**
 * @author Angle Function:热门/最新适配器
 */
public class HotGridAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_hot;
	private String surplus;// 剩余

	public HotGridAdapter(Context context, List<BaseJson> list_hot) {
		this.context = context;
		this.list_hot = list_hot;
	}

	@Override
	public int getCount() {
		return list_hot.size();
	}

	@Override
	public Object getItem(int position) {
		return list_hot.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void insertData(List<BaseJson> list_add) {
		list_hot.addAll(list_add);
	}
	public List<BaseJson> getAllData() {
		return list_hot;

	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.grid_item_hot, parent, false);
		}
		BaseJson hot = list_hot.get(position);
		final ImageView img_hot_pic = BaseAdapterHelper.get(convertView,
				R.id.img_hot_pic);
		TextView tv_hot_say = BaseAdapterHelper.get(convertView,
				R.id.tv_hot_say);
		TextView tv_hot_nickname = BaseAdapterHelper.get(convertView,
				R.id.tv_hot_nickname);
		TextView tv_hot_buy = BaseAdapterHelper.get(convertView,
				R.id.tv_hot_buy);
		TextView tv_hot_receive = BaseAdapterHelper.get(convertView,
				R.id.tv_hot_receive);
		String url = hot.getVideoimg();
		ImageUtil.loadImage(
				ImageUtil.getCacheImgPath()
						+ url.substring(url.lastIndexOf("com/") + 4,
								url.lastIndexOf(".")).replace("/", "-"), url,
				new ImageCallback() {

					@Override
					public void loadImage(Bitmap bitmap, String imagePath) {
						// TODO Auto-generated method stub
						if (bitmap != null) {
							img_hot_pic.setImageBitmap(bitmap);
						}
					}
				});

		tv_hot_say.setText(hot.getIntro());
		tv_hot_nickname.setText(hot.getName());
		tv_hot_buy.setText(hot.getBuynums() + "人购买");
		tv_hot_receive.setText(hot.getReviewnums() + "");

		return convertView;
	}

}
