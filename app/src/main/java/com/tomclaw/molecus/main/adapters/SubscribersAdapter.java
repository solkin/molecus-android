package com.tomclaw.molecus.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.tomclaw.molecus.main.views.SubscriberView;
import com.tomclaw.molecus.main.views.SubscriberView_;
import com.tomclaw.molecus.molecus.dto.UserInfo;

import java.util.List;

/**
 * Created by solkin on 03/03/16.
 */
public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.SubscriberViewHolder> {

    private Context context;
    private List<UserInfo> subscribers;

    public SubscribersAdapter(Context context, List<UserInfo> subscribers) {
        this.context = context;
        this.subscribers = subscribers;
    }

    public void setSubscribers(List<UserInfo> userInfoList) {
        this.subscribers = userInfoList;
    }

    @Override
    public SubscriberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SubscriberView view = SubscriberView_.build(context);
        return new SubscriberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubscriberViewHolder holder, int position) {
        holder.bind(subscribers.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return subscribers.size();
    }

    public static class SubscriberViewHolder extends RecyclerView.ViewHolder {
        private SubscriberView subscriberView;

        public SubscriberViewHolder(SubscriberView itemView) {
            super(itemView);
            this.subscriberView = itemView;
        }

        public void bind(UserInfo userInfo) {
            subscriberView.bindUserInfo(userInfo);
        }
    }
}
