package com.chengning.fenghuo.base;

import java.util.List;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 使用getAdapter进行相应的数据更新操作（比如getAdapter().notifyItemRemoved(pos)）;
 * @author Administrator
 *
 */
public abstract class BaseRecylerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    protected Activity mContext;
    protected List mList;
    private RecyclerView mRecyclerView;

    /**
     * 构造器
     *
     * @param context
     * @param list
     * @param itemLayoutId
     */
    protected BaseRecylerViewAdapter(Activity context, List list, RecyclerView recyclerview) {
        this.mContext = context;
        this.mList = list;
        this.mRecyclerView = recyclerview;
    }
    
    public Activity getContext(){
		return mContext;
	}
    
    public RecyclerView.Adapter getAdapter() {
    	return mRecyclerView.getAdapter();
    }
    
    private void onBindAdvanceViewHolder(BaseRecyclerViewHolder holder, int i) {
        handleLayout(holder, mList.get(i), i);
    }

    /**
     * 设置每个页面显示的内容
     *
     * @param holder itemHolder
     * @param item   每一Item显示的数据
     */
    public abstract void handleLayout(BaseRecyclerViewHolder holder, Object item, int position);
    
    public abstract int buildLayoutId();


    /**
     * 创建ViewHolder
     *
     * @param parent   RecycleView对象
     * @param viewType view类型
     * @return Holder对象
     */
    public BaseRecyclerViewHolder onCreateAdvanceViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(buildLayoutId(), null);
        return new BaseRecyclerViewHolder(v, this);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return onCreateAdvanceViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {

        onBindAdvanceViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
    	if (mList != null) {
            return mList.size();
        }
        return 0;
    }
    
    public void setList(List list){
		this.mList = list;
	}
	
	public List getList(){
		return mList;
	}

    protected OnItemClickListener itemOnListener;


    public OnItemClickListener getItemClickListener() {
        return itemOnListener;
    }

    /**
     * 设置点击事件监听器
     *
     * @param listener 监听器对象
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemOnListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }
}
