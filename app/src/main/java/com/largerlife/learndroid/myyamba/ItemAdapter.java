package com.largerlife.learndroid.myyamba;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

/**
 * Created by László Gálosi on 18/03/16
 */
public class ItemAdapter<T extends Object> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final Context mContext;
    protected final RecyclerView mRecyclerView;
    @LayoutRes protected final int mItemLayout;
    protected List<T> mDataset;
    protected int mCurrentItemId = 0;
    int mItemHeight;
    private boolean mItemClickable = true;
    private View.OnClickListener mCustomClickListener;

    public ItemAdapter(final Context context, final RecyclerView recyclerView,
          @LayoutRes final int itemLayout, List<T> dataSet) {
        mContext = context;
        mRecyclerView = recyclerView;
        mItemLayout = itemLayout;
        mDataset = dataSet;
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mItemLayout, parent, false);
        return new ItemViewHolderBase(view, mContext);
    }

    /*
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((Bindable) holder).bind(mDataset.get(position));
    }

    /**
     * @return the size of your dataset (invoked by the layout manager)
     */
    @Override public int getItemCount() {
        return mDataset.size();
    }

    public void add(int position, T item) {
        final int id = mCurrentItemId++;
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Object datatSetItem) {
        int position = mDataset.indexOf(datatSetItem);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAt(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public int itemLayout() {
        return mItemLayout;
    }

    public ItemAdapter itemHeight(final int itemHeight) {
        mItemHeight = itemHeight;
        return this;
    }

    public ItemAdapter itemIsClickable(final boolean itemClickable) {
        mItemClickable = itemClickable;
        return this;
    }

    public List<T> getDataset() {
        return mDataset;
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public boolean isItemClickable() {
        return mItemClickable;
    }

    public View.OnClickListener getCustomClickListener() {
        return mCustomClickListener;
    }

    public ItemAdapter setCustomClickListener(final View.OnClickListener customClickListener) {
        mCustomClickListener = customClickListener;
        return this;
    }
}
