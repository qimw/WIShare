package com.unique.eightzeroeight.wishare.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unique.eightzeroeight.wishare.R;
import com.unique.eightzeroeight.wishare.network.base.DeviceData;

import java.util.List;

public class OnlineUserAdapter extends RecyclerView.Adapter<OnlineUserAdapter.Holder> {

    private List<DeviceData> list;
    private static final String TAG = "OnlineUserAdapter";
    private OnItemClickListener mItemClickListener;

    public OnlineUserAdapter(List<DeviceData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick((Integer) v.getTag());
                }
            }
        });
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.fileName.setText(list.get(position).getPkgName());
        holder.userName.setText(list.get(position).getIp());
        holder.itemView.setTag(position);
        Log.d(TAG, "onBindViewHolder: " + list.size());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView userName;
        TextView fileName;

        public Holder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_avatar);
            userName = itemView.findViewById(R.id.tv_name);
            fileName = itemView.findViewById(R.id.tv_filename);
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
