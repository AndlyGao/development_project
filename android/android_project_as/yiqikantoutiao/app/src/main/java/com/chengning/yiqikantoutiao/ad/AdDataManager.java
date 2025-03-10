package com.chengning.yiqikantoutiao.ad;

import android.app.Activity;

import com.chengning.yiqikantoutiao.ad.AdDataDummy.BaiduNativeNetworkDummyListener;
import com.chengning.yiqikantoutiao.ad.AdDataDummy.NativeResponseDummy;
import com.chengning.yiqikantoutiao.util.Common;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class AdDataManager {
	
	private static final String TAG = AdDataManager.class.getSimpleName();
	
	private Activity mContext;

	HashMap<String, AdTree> mAdMap = new HashMap<String, AdTree>();
	HashMap<String, Boolean> mAdReqestMap = new HashMap<String, Boolean>();
	
	public static class AdData{
		NativeResponseDummy obj;
		AdDataListener listener;
	}
	
	public static class AdTree{
		List<NativeResponseDummy> data;
		TreeMap<Integer, AdData> map = new TreeMap<Integer, AdData>();
	}
	
	public AdDataManager(Activity activity){
		this.mContext = activity;
	}
	
	public synchronized void reset(){
		mAdMap = new HashMap<String, AdTree>();
		mAdReqestMap = new HashMap<String, Boolean>();
	}
	
	public synchronized void resetListenerMap(){
		for(String key : mAdMap.keySet()){
			AdTree tree = mAdMap.get(key);
			if(tree.map != null){
				tree.map.clear();
			}
		}
	}
	
	public synchronized void getData(String adPlaceId, int position, AdDataListener listener){
		if(!mAdMap.containsKey(adPlaceId)){
			mAdMap.put(adPlaceId, new AdTree());
		}
		AdTree tree = mAdMap.get(adPlaceId);
		if(!tree.map.containsKey(position)){
			AdData ad = new AdData();
			ad.listener = listener;
			tree.map.put(position, ad);
			if(!Common.isListEmpty(tree.data)){
				List<NativeResponseDummy> list = tree.data;
				int size = list.size();
				int index = 0;
				for(Integer key : tree.map.keySet()){
					AdData mad = tree.map.get(key);
					if(size > index){
						if(key == position && mad.listener != null){
							NativeResponseDummy obj = list.get(index);
							mad.obj = obj;
							mad.listener.onData(obj);
//							Log.e(TAG, "getData 1 key: " + key + ", title: " + obj.getTitle()+  ", obj: " + obj + ", ad: " + mad + ", index: " + index);
						}
					}else{
						break;
					}
					index++;
				}
			}
		}else{
			AdData ad = tree.map.get(position);
			ad.listener = listener;
			if(ad.obj != null){
//				Log.e(TAG, "getData 2 position: " + position + ", title: " + ad.obj.getTitle()+  ", obj: " + ad.obj + ", ad: " + ad);
				ad.listener.onData(ad.obj);
			}
		}
		if(!mAdReqestMap.containsKey(adPlaceId)){
			mAdReqestMap.put(adPlaceId, true);
			getDataBySDK(adPlaceId);
		}
	}
	
	public void getDataBySDK(final String adPlaceId){
		AdDataDummy.getDataBySDKDummy(mContext, adPlaceId, new BaiduNativeNetworkDummyListener() {
			
			@Override
			public void onNativeLoad(List<NativeResponseDummy> list) {
				if(Common.isListEmpty(list)){
					return;
				}
//				for(int i = 0; i < list.size(); i++){
//					// (debug)
//					NativeResponseDummy nr = list.get(i);
//					Log.e(TAG, "getData 0 onNativeLoad index: " + i + ", title: " + nr.getTitle()+  ", obj: " + nr);
//				}
				if(!mAdMap.containsKey(adPlaceId)){
					mAdMap.put(adPlaceId, new AdTree());
				}
				AdTree tree = mAdMap.get(adPlaceId);
				tree.data = list;
				if(tree.map.isEmpty()){
					return;
				}
				int size = list.size();
				int index = 0;
				for(Integer key : tree.map.keySet()){
					AdData ad = tree.map.get(key);
					if(size > index){
						if(ad.listener != null){
							NativeResponseDummy obj = list.get(index);
							ad.obj = obj;
							ad.listener.onData(obj);
						}
					}else{
						break;
					}
					index++;
				}
			}			
		});
	}
}
