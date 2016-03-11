package com.zmv.zf.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wbvideo.dm.R;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.utils.ImageLoader;
import com.zmv.zf.utils.ImageLoader.Type;
import com.zmv.zf.view.CircleImageView;

/**
 * 
 */
public class ShareListAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_share;
	private ImageLoader mImageLoader;
	public ShareListAdapter(Context context, List<BaseJson> list_share) {
		this.context = context;
		this.list_share = list_share;mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
	}

	@Override
	public int getCount() {
		return list_share.size();
	}

	@Override
	public Object getItem(int position) {
		return list_share.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void insertData(List<BaseJson> list_add) {
		list_share.addAll(list_add);
	}

	public List<BaseJson> getAllData() {
		return list_share;

	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_share, parent, false);
		}
		try {

			final ImageView img_share_pic = BaseAdapterHelper.get(convertView,
					R.id.img_share_bigicon);
			TextView tv_share_online = BaseAdapterHelper.get(convertView,
					R.id.tv_share_online);
			TextView tv_share_recevie = BaseAdapterHelper.get(convertView,
					R.id.tv_share_recevie);
			TextView tv_share_like = BaseAdapterHelper.get(convertView,
					R.id.tv_share_like);
			TextView tv_share_message = BaseAdapterHelper.get(convertView,
					R.id.tv_share_message);
			TextView tv_share_name = BaseAdapterHelper.get(convertView,
					R.id.tv_share_name);
			TextView tv_share_age = BaseAdapterHelper.get(convertView,
					R.id.tv_share_age);
			TextView tv_share_say = BaseAdapterHelper.get(convertView,
					R.id.tv_share_say);
			TextView tv_share_fans = BaseAdapterHelper.get(convertView,
					R.id.tv_share_fans);
			TextView tv_share_videos = BaseAdapterHelper.get(convertView,
					R.id.tv_share_videos);
			TextView tv_share_plays = BaseAdapterHelper.get(convertView,
					R.id.tv_share_plays);
			final CircleImageView cimg_share_icon = BaseAdapterHelper.get(
					convertView, R.id.cimg_share_icon);

			tv_share_online.setText(list_share.get(pos).getOnline());
			tv_share_recevie.setText(list_share.get(pos).getReviewnums() + "");
			tv_share_like.setText(list_share.get(pos).getLikenum() + "");
			tv_share_name.setText(list_share.get(pos).getName());
			tv_share_age.setText(list_share.get(pos).getAge()+"岁，"
					+ list_share.get(pos).getAddress());
			tv_share_say.setText(list_share.get(pos).getIntro());
			tv_share_fans.setText(list_share.get(pos).getFans() + "人关注");
			tv_share_videos.setText(list_share.get(pos).getVideonums() + "个视频");
			tv_share_plays.setText(list_share.get(pos).getPlaynums() + "次播放");
			if (list_share.get(pos).getMsgnum() > 999)
				tv_share_message.setText("999+人私信");
			else
				tv_share_message.setText(list_share.get(pos).getMsgnum()
						+ "人私信");
			mImageLoader.loadImage(list_share.get(pos).getBigicon(), img_share_pic, true);
//			ImageUtil.loadImage(
//					ImageUtil.getCacheImgPath()
//							+ list_share
//									.get(pos)
//									.getBigicon()
//									.substring(
//											list_share.get(pos).getBigicon()
//													.lastIndexOf("/") + 1,
//											list_share.get(pos).getBigicon()
//													.lastIndexOf(".")),
//					list_share.get(pos).getBigicon(), new ImageCallback() {
//
//						@Override
//						public void loadImage(Bitmap bitmap, String imagePath) {
//							// TODO Auto-generated method stub
//							if (bitmap != null) {
//								img_share_pic.setImageBitmap(bitmap);
//							}
//						}
//					});
			String url = list_share.get(pos).getIcon();
			mImageLoader.loadImage(url, cimg_share_icon, true);
//			ImageUtil.loadImage(
//					ImageUtil.getCacheImgPath()
//							+ url.substring(url.lastIndexOf("/") + 1,
//									url.lastIndexOf(".")), url,
//					new ImageCallback() {
//
//						@Override
//						public void loadImage(Bitmap bitmap, String imagePath) {
//							// TODO Auto-generated method stub
//							if (bitmap != null) {
//								cimg_share_icon.setImageBitmap(bitmap);
//							}
//						}
//					});

		} catch (Exception e) {
			// TODO: handle exception
		}

		return convertView;
	}

}
