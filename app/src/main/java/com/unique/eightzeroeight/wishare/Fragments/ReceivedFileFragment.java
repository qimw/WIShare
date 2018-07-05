package com.unique.eightzeroeight.wishare.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unique.eightzeroeight.wishare.Activities.AppContext;
import com.unique.eightzeroeight.wishare.Adapters.FileReceivedAdapter;
import com.unique.eightzeroeight.wishare.Entities.FileInfo;
import com.unique.eightzeroeight.wishare.R;
import com.unique.eightzeroeight.wishare.Utils.FileUtils;
import com.unique.eightzeroeight.wishare.Utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ReceivedFileFragment extends Fragment {


    @Bind(R.id.file_list)
    RecyclerView recyclerView;
    @Bind(R.id.nofile)
    TextView noFile;
    public ReceivedFileFragment() {

        // Required empty public constructor
    }


    public static ReceivedFileFragment newInstance(String param1, String param2) {
        ReceivedFileFragment fragment = new ReceivedFileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_received_file, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        File file = new File(FileUtils.getRootDirPath() + "/fileReceived");
        if (!file.exists()) {
            boolean result = file.mkdir();
            if (!result) {
                ToastUtils.show(AppContext.getAppContext(), "创建接收文件夹失败");
            }
            recyclerView.setVisibility(View.GONE);
        } else {
            Log.i("ReceivedFileFragment", "hello");
            File[] files = file.listFiles();
            if (files.length != 0) {
                noFile.setVisibility(View.GONE);
                ArrayList<FileInfo> list = new ArrayList<>();
                for (File f : files) {
                    FileInfo info = new FileInfo();
                    info.setSizeDesc(FileUtils.getFileSize(f.length()));
                    info.setName(f.getName());
                    list.add(info);
                    Log.i("ReceivedFileFragment", info.getName() + info.getSizeDesc());
                }

                FileReceivedAdapter adapter = new FileReceivedAdapter(list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setVisibility(View.GONE);
            }



        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


}
