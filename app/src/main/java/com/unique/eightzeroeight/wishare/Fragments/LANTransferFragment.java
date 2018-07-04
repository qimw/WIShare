package com.unique.eightzeroeight.wishare.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unique.eightzeroeight.wishare.Activities.MainActivity;
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
    private SearchServer searchServer;
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
        recyclerView.setAdapter(adapter);
        fab = view.findViewById(R.id.add_file_fab);
        ServerConfig.setFunc(1);
        DeviceData deviceData = new DeviceData();
        deviceData.setDevId("aaa111");
        deviceData.setFunc(1);
        deviceData.setServiceName("aa");
        deviceData.setPkgName("com.udp.tvdevice123");
        ServerConfig.setDeviceData(deviceData);

        searchServer = new SearchServer(1024) {
            @Override
            public void printLog(String log) {
                Log.d("LANTransfer", log);
            }

            @Override
            public void onReceiveSearchReq(RequestSearchData data) {
                requestList.add(data);
                ((MainActivity) getActivity()).getHandler().sendEmptyMessage(SHOW_SEARCH_REQUEST);
            }
        };

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).getHandler().sendEmptyMessage(START_BE_SEARCH);
                searchServer.init();
            }
        });

        return view;
    }

    public List<RequestSearchData> getRequests() {
        return requestList;
    }

    public void requestNotifyData() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        searchServer.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchServer != null && !searchServer.isOpen()) {
            searchServer.init();
        }
    }

}
