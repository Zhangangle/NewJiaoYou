package com.zmv.zf.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hihi.jy.R;
import com.zmv.zf.bean.BaseJson;

/**
 * @author
 */
public class DetailListAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_detail;

	public DetailListAdapter(Context context, List<BaseJson> list_detail) {
		this.context = context;
		this.list_detail = list_detail;
	}

	@Override
	public int getCount() {
		return list_detail.size();
	}

	@Override
	public Object getItem(int position) {
		return list_detail.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void insertData(List<BaseJson> list_add) {
		list_detail.addAll(list_add);
	}

	public List<BaseJson> getAllData() {
		return list_detail;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_say, parent, false);
		}

		return convertView;
	}

}
