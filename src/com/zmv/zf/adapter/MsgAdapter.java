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

import com.xfsf.cc.R;
import com.zmv.zf.bean.BaseJson;
import com.zmv.zf.utils.ImageUtil;
import com.zmv.zf.utils.ImageUtil.ImageCallback;

public class MsgAdapter extends BaseAdapter {
	List<BaseJson> list_msg;
	Context context;

	public MsgAdapter(Context context, List<BaseJson> list_msg) {
		this.context = context;
		this.list_msg = list_msg;
	}

	@Override
	public int getCount() {
		return list_msg.size();
	}

	@Override
	public Object getItem(int position) {
		return list_msg.get(position);
	}

	public void insertData(List<BaseJson> list_add) {
		list_msg.addAll(list_add);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_inbox_main, parent, false);
		}

		final ImageView img_icon = BaseAdapterHelper.get(convertView,
				R.id.img_inbox_listicon);
		TextView tv_inbox_listname = BaseAdapterHelper.get(convertView,
				R.id.tv_inbox_listname);
		TextView tv_inbox_listmsg = BaseAdapterHelper.get(convertView,
				R.id.tv_inbox_listmsg);
		TextView tv_inbox_nums = BaseAdapterHelper.get(convertView,
				R.id.tv_inbox_nums);

		try {
			tv_inbox_listname.setText(list_msg.get(pos).getName());
			tv_inbox_listmsg.setText(list_msg.get(pos).getMsg());
			if (list_msg.get(pos).getMsgnum() == 0)
				tv_inbox_nums.setVisibility(View.GONE);
			else
				tv_inbox_nums.setText(list_msg.get(pos).getMsgnum()+"");
			String url = list_msg.get(pos).getIcon();

			ImageUtil.loadImage(
					ImageUtil.getCacheImgPath()
							+ url.substring(url.lastIndexOf("com/") + 4,
									url.lastIndexOf(".")).replace("/", "-"), url,
					new ImageCallback() {

						@Override
						public void loadImage(Bitmap bitmap, String imagePath) {
							// TODO Auto-generated method stub
							if (bitmap != null) {
								img_icon.setImageBitmap(bitmap);
							}
						}
					});
		} catch (Exception e) {
			// TODO: handle exception

//			Log.e("图片加载", e.toString());
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
//			Log.e("图片加载", "OutOfMemoryError");
		}
		return convertView;
	}

}
