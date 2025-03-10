package com.chengning.yiqikantoutiao.data.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.chengning.common.base.BaseListDA;
import com.chengning.common.util.SerializeUtil;
import com.chengning.yiqikantoutiao.data.bean.ChannelItemBean;
import com.chengning.yiqikantoutiao.db.provider.dbContent.table_channel_item;

import java.util.ArrayList;
import java.util.List;

public class ChannelItemListDA extends BaseListDA<ChannelItemBean> {

	private static ChannelItemListDA mInst;

	public static ChannelItemListDA getInst(Context con) {
		
        if (mInst == null) {
            synchronized (ChannelItemListDA.class) {
                if (mInst == null) {
                    mInst = new ChannelItemListDA(con);
                }
            }
        }
		return mInst;
	}

	private ChannelItemListDA(Context con) {
		super(con, table_channel_item.CONTENT_URI);
	}

	@Override
	public String buildDeleteWhere(ChannelItemBean t) {
		return null;
	}

	@Override
	public String[] buildDeleteSelectionArgs(ChannelItemBean t) {
		return null;
	}

	@Override
	public ContentValues buildInsertValues(ChannelItemBean bean) {
		ContentValues values = new ContentValues();
		values.put("tid", bean.getTid());
		values.put("nickname", bean.getNickname());
		values.put("dateline", bean.getDateline());
		values.put("replys", bean.getReplys());
		values.put("title", bean.getTitle());
		values.put("image_list", bean.getImage_list());
		values.put("is_subscribe", bean.getIs_subscribe());
		values.put("is_slide", bean.getIs_slide());
		values.put("local_channel", bean.getLocal_channel());
		values.put("local_page", bean.getLocal_page());
		values.put("type", bean.getShow_type());
		values.put("image_arr", SerializeUtil.serialize(bean.getImage_arr()));
		values.put("images", bean.getImages());
		values.put("url", bean.getUrl());
		values.put("jump_type", bean.getJump_type());
		values.put("jump_id", bean.getJump_id());
		values.put("is_special", bean.getIs_special());
		values.put("ch_name", bean.getCh_name());
		values.put("uid", bean.getUid());
		values.put("is_part", bean.getIs_part());
		values.put("face", bean.getFace());
		values.put("is_follow", bean.getIs_follow());
		values.put("ad_place_id", bean.getAd_place_id());
		values.put("category", bean.getCategory());
		values.put("content_type", bean.getContent_type());
		values.put("redirecturl", bean.getRedirecturl());
		values.put("list_from", bean.getList_from());
		values.put("tz_nav", bean.getTz_nav());
		values.put("videoid", bean.getVideoid());
		return values;
	}

	@Override
	public ChannelItemBean buildQueryValues(Cursor cursor) {
		ChannelItemBean bean = new ChannelItemBean();
		bean.setTid(cursor.getString(cursor.getColumnIndex("tid")));
		bean.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
		bean.setDateline(cursor.getString(cursor.getColumnIndex("dateline")));
		bean.setReplys(cursor.getInt(cursor.getColumnIndex("replys")));
		bean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		bean.setImage_list(cursor.getString(cursor.getColumnIndex("image_list")));
		bean.setLocal_channel(cursor.getString(cursor.getColumnIndex("local_channel")));
		bean.setIs_subscribe(cursor.getInt(cursor.getColumnIndex("is_subscribe")));
		bean.setIs_slide(cursor.getInt(cursor.getColumnIndex("is_slide")));
		bean.setLocal_page(cursor.getInt(cursor.getColumnIndex("local_page"))); 
		bean.setShow_type(cursor.getInt(cursor.getColumnIndex("type")));
		ArrayList<String> image_arr = SerializeUtil.deSerialize(cursor.getString(cursor.getColumnIndex("image_arr")), ArrayList.class);
		bean.setImage_arr(image_arr);
		bean.setImages(cursor.getString(cursor.getColumnIndex("images")));
		bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
		bean.setJump_type(cursor.getString(cursor.getColumnIndex("jump_type")));
		bean.setJump_id(cursor.getString(cursor.getColumnIndex("jump_id")));
		bean.setIs_special(cursor.getInt(cursor.getColumnIndex("is_special")));
		bean.setCh_name(cursor.getString(cursor.getColumnIndex("ch_name")));
		bean.setUid(cursor.getString(cursor.getColumnIndex("uid")));
		bean.setIs_part(cursor.getInt(cursor.getColumnIndex("is_part")));
		bean.setFace(cursor.getString(cursor.getColumnIndex("face")));
		bean.setIs_follow(cursor.getInt(cursor.getColumnIndex("is_follow")));
		bean.setCategory(cursor.getString(cursor.getColumnIndex("category")));
		bean.setAd_place_id(cursor.getString(cursor.getColumnIndex("ad_place_id")));
		bean.setContent_type(cursor.getString(cursor.getColumnIndex("content_type")));
		bean.setRedirecturl(cursor.getString(cursor.getColumnIndex("redirecturl")));
		bean.setList_from(cursor.getString(cursor.getColumnIndex("list_from")));
		bean.setTz_nav(cursor.getString(cursor.getColumnIndex("tz_nav")));
		bean.setVideoid(cursor.getString(cursor.getColumnIndex("videoid")));
		return bean;
	}
	
	public void setChannelAndPage(List<ChannelItemBean> list, String channel, int page){
		if(list != null){
			for(ChannelItemBean b : list){
				b.setLocal_channel(channel);
				b.setLocal_page(page);
			}
		}
	}
	
	public void clearChannel(String channel){
		deleteTarget("local_channel = ?",
				new String[]{channel});
	}
	
	public void insertChannelItemList(List<ChannelItemBean> list, String channel, int page){
		deleteTarget("local_channel = ? and local_page = ?",
				new String[]{channel, String.valueOf(page)});
		insertList(list);
	}
	
	public List<ChannelItemBean> queryChannelItemList(String channel, int page){
		List<ChannelItemBean> list = queryTarget("local_channel = ? and local_page = ?",
				new String[]{channel, String.valueOf(page)}, null);
		return list;
	}

}
