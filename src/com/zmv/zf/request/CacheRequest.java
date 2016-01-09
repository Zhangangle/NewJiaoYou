package com.zmv.zf.request;

import android.content.Context;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.zmv.zf.cache.Cache;
import com.zmv.zf.common.Conf;
import com.zmv.zf.utils.NetworkUtils;

public class CacheRequest {

	public static void requestPOST(Context context, final String url,
			RequestParams params, final String key, final int expireTime,
			final CacheRequestCallBack cacheRequestCallBack) {
		HttpUtils http = new HttpUtils();
		final Cache cache = Cache.get(context);
		final String data = cache.getAsString(key);
		LogUtils.e("内存数据" + data);
		if (!NetworkUtils.checkNet(context)) {
			if (data != null) {
				cacheRequestCallBack.onData(data);
				// else
				// cacheRequestCallBack.onData("");
				return;
			}
		}
		if (data != null) {
			cacheRequestCallBack.onData(data);
			return;
		}

		http.send(HttpMethod.POST, Conf.URL + url, params,
				new RequestCallBack<String>() {

					@SuppressWarnings("unused")
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						if (data != null)
							cacheRequestCallBack.onFail(arg0, arg1, data);
						else
							cacheRequestCallBack.onFail(arg0, arg1, "");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						// cacheRequestCallBack.onData(arg0);
						cacheRequestCallBack.onData(arg0.result.toString());
						cache.put(key, arg0.result.toString(), expireTime);// 将数据存入缓存中
					}
				});
	}

	public static void requestGET(Context context, final String url,
			final RequestParams params, final String key, final int expireTime,
			final CacheRequestCallBack cacheRequestCallBack) {

		String data = null;
		final Cache cache = Cache.get(context);
		if (!key.equals("Not")) {
			data = cache.getAsString(key);
		}

		if (!NetworkUtils.checkNet(context)) {
			if (data != null) {
				cacheRequestCallBack.onData(data);
				// else
				// cacheRequestCallBack.onData("");
				return;
			}
		}
		if (data != null) {
			cacheRequestCallBack.onData(data);
			return;
		}
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(1000);
		http.send(HttpMethod.GET, Conf.URL + url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

						cacheRequestCallBack.onFail(arg0, arg1, "");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						cacheRequestCallBack.onData(arg0.result.toString());
						cache.put(key, arg0.result.toString(), expireTime);// 将数据存入缓存中
						// if(LruDiskCacheHTTP.containsKey(key)){
						//
						// }else{
						// LruDiskCacheHTTP.saveTask(key,
						// arg0.result.toString());
						// }

					}
				});
	}
}
