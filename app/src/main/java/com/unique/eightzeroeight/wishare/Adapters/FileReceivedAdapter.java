package com.unique.eightzeroeight.wishare.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unique.eightzeroeight.wishare.Entities.FileInfo;
import com.unique.eightzeroeight.wishare.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wqm on 18-7-5.
 */

public class FileReceivedAdapter extends RecyclerView.Adapter<FileReceivedAdapter.ViewHolder> {
    private ArrayList<FileInfo> list;
    public FileReceivedAdapter(ArrayList<FileInfo> list) {
        this.list = list;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.size.setText(list.get(position).getSizeDesc());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView size;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            size = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }
}
