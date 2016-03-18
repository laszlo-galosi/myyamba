package com.largerlife.learndroid.myyamba;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by László Gálosi on 18/03/16
 */
public class ItemViewHolderBase extends RecyclerView.ViewHolder
      implements Bindable {

    View mMainWidget;
    View mItemView;
    Context mContext;
    protected Object mDataItem;

    @Override public void bind(final Object dataItem) {
        Log.d("bind", dataItem.toString());
        mDataItem = dataItem;
    }

    @Override public Object getDatatItem() {
        return mDataItem;
    }

    protected ItemViewHolderBase(final View itemView, Context context) {
        super(itemView);
        mItemView = itemView;
        mContext = context;
    }

    /**
     * Constructor
     *
     * @param itemView the parent view of this recycled item
     * @param widgetId the main widget of this item which can be as simple as {@link TextView} or
     * a complex custom view. Represents the main widget which the user interacts with.
     */
    protected ItemViewHolderBase(final View itemView, @IdRes int widgetId, Context context) {
        super(itemView);
        mItemView = itemView;
        mMainWidget = mItemView.findViewById(widgetId);
        mContext = context;
    }
}
