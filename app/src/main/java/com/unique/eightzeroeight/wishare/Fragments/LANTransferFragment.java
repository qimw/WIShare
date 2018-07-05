package com.unique.eightzeroeight.wishare.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unique.eightzeroeight.wishare.Activities.FileChooseActivity;
import com.unique.eightzeroeight.wishare.Activities.MainActivity;
import com.unique.eightzeroeight.wishare.Activities.ReceiveActivity;
import com.unique.eightzeroeight.wishare.Adapters.OnlineUserAdapter;
import com.unique.eightzeroeight.wishare.R;
import com.unique.eightzeroeight.wishare.network.base.DeviceData;
import com.unique.eightzeroeight.wishare.network.base.RequestSearchData;
import com.unique.eightzeroeight.wishare.network.server.SearchServer;
import com.unique.eightzeroeight.wishare.network.server.ServerConfig;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LANTransferFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<DeviceData> list;
    private OnlineUserAdapter adapter;
    private FloatingActionButton fab;
    private CopyOnWriteArrayList<RequestSearchData> requestList = new CopyOnWriteArrayList<>();

    private static final int SHOW_SEARCH_REQUEST = 4;
    private static final int START_BE_SEARCH = 5;
    private static final int END_BE_SEARCH = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lantransfer, container, false);
        recyclerView = view.findViewById(R.id.user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list = ((MainActivity) getActivity()).getList();
        Log.d("LANTransferFragment", "onCreateView: size of the list: " + list.size());
        adapter = new OnlineUserAdapter(list);
        adapter.setItemClickListener(new OnlineUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DeviceData data = list.get(position);
                Intent intent = new Intent(getActivity(), ReceiveActivity.class);
                intent.putExtra("ip", data.getIp());
                intent.putExtra("port", data.getPort());
                intent.putExtra("size", data.getDevId());
                intent.putExtra("fileName", data.getPkgName());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        fab = view.findViewById(R.id.add_file_fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FileChooseActivity.class);
                startActivityForResult(intent, 10086);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public List<RequestSearchData> getRequests() {
        return requestList;
    }

    public void requestNotifyData() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

}
