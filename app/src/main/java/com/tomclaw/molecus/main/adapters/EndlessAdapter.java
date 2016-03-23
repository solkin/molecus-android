package com.tomclaw.molecus.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by solkin on 03/03/16.
 */
public abstract class EndlessAdapter<T, H extends EndlessAdapter.ItemViewHolder<T>> extends RecyclerView.Adapter<H> {

    private final List<T> tList = new ArrayList<>();
    private boolean isMoreItemsAvailable = false;
    private EndlessAdapterListener listener;

    private int staticItemsCount = 0;
    private int latestInvokedPosition = -1;

    private Context context;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_PROGRESS = 1;

    public EndlessAdapter(Context context, EndlessAdapterListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        return tList.size() + (isMoreItemsAvailable ? TYPE_PROGRESS : TYPE_NORMAL);
    }

    public T getItem(int position) {
        if (isMoreItemsAvailable && position == tList.size()) {
            return null;
        }
        return tList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (isMoreItemsAvailable && position == tList.size()) ? TYPE_PROGRESS : TYPE_NORMAL;
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_NORMAL ? createNormalHolder(parent) : createProgressHolder(parent);
    }

    public abstract H createNormalHolder(ViewGroup parent);

    public abstract H createProgressHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(H holder, int position) {
        if (getItemViewType(position) == TYPE_PROGRESS) {
            onShowProgressItem(position);
            holder.bindProgress();
        } else {
            holder.bindNormal(getItem(position));
        }
    }

    private void onShowProgressItem(int position) {
        if (position > latestInvokedPosition) {
            listener.onLoadMoreItems(tList.size() - staticItemsCount);
            latestInvokedPosition = position;
        }
    }

    public void appendStaticItem(T t) {
        tList.add(staticItemsCount++, t);
    }

    public void appendItem(T t) {
        tList.add(t);
    }

    public void setItems(Collection<T> t) {
        tList.clear();
        tList.addAll(t);
    }

    public void appendItems(Collection<T> t) {
        tList.addAll(t);
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(tList);
    }

    public void setMoreItemsAvailable(boolean isMoreItemsAvailable) {
        this.isMoreItemsAvailable = isMoreItemsAvailable;
    }

    public int getStaticItemsCount() {
        return staticItemsCount;
    }

    public boolean isEmpty() {
        return tList.isEmpty();
    }

    public interface EndlessAdapterListener {
        void onLoadMoreItems(int offset);
    }

    public static abstract class ItemViewHolder<T> extends RecyclerView.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public void bindNormal(T item) {

        }

        public void bindProgress() {

        }
    }
}
