package com.zmv.zf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zjy.qqjy.zm.R;
import com.zmv.zf.utils.ImageLoader;
import com.zmv.zf.utils.ImageLoader.Type;

/**
 * @author
 */
public class PicGridAdapter extends BaseAdapter {

	private Context context;
	private String[] list_pic;
	private ImageLoader mImageLoader;
	public PicGridAdapter(Context context, String[] list_pic) {
		this.context = context;
		this.list_pic = list_pic;mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	}

	@Override
	public int getCount() {
		return list_pic.length;
	}

	@Override
	public Object getItem(int position) {
		return list_pic[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public String[] getAllData() {
		return list_pic;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.grid_item_pic, parent, false);
		}
		final ImageView img_icon = BaseAdapterHelper.get(convertView,
				R.id.img_per_pic);
		String url = list_pic[position];
		mImageLoader.loadImage(url, img_icon, true);
//		ImageUtil.loadImage(
//				ImageUtil.getCacheImgPath()
//						+ url.substring(url.lastIndexOf("com/") + 4,
//								url.lastIndexOf(".")).replace("/", "-"), url,
//				new ImageCallback() {
//
//					@Override
//					public void loadImage(Bitmap bitmap, String imagePath) {
//						// TODO Auto-generated method stub
//						if (bitmap != null) {
//							img_icon.setImageBitmap(bitmap);
//						}
//					}
//				});
		return convertView;
	}
}
