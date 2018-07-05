package com.unique.eightzeroeight.wishare.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;

import com.unique.eightzeroeight.wishare.Adapters.ViewPagerAdapter;
import com.unique.eightzeroeight.wishare.FragmentChangeListener;
import com.unique.eightzeroeight.wishare.Fragments.FileChooseFragment;
import com.unique.eightzeroeight.wishare.Fragments.LANTransferFragment;
import com.unique.eightzeroeight.wishare.Fragments.ReceivedFileFragment;
import com.unique.eightzeroeight.wishare.Fragments.WebTransferFragment;
import com.unique.eightzeroeight.wishare.R;
import com.unique.eightzeroeight.wishare.Utils.FileUtils;
import com.unique.eightzeroeight.wishare.network.base.BaseUserData;
import com.unique.eightzeroeight.wishare.network.base.DeviceData;
import com.unique.eightzeroeight.wishare.network.base.RequestSearchData;
import com.unique.eightzeroeight.wishare.network.client.ClientConfig;
import com.unique.eightzeroeight.wishare.network.client.SearchClient;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Fragment> fragments;
    private FileChooseFragment fileChoose;
    private LANTransferFragment lanTransfer;
    private ReceivedFileFragment fileReceived;
    private WebTransferFragment webTransfer;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;


    private CopyOnWriteArrayList<DeviceData> mDeviceList = new CopyOnWriteArrayList<>();
    private Handler mHandler;
    private SearchClient searchClient;
    private static final int MESSAGE_SEARCH_START = 1;
    private static final int MESSAGE_SEARCH_FINISH = 2;
    private static final int MESSAGE_SEARCH_DEV = 3;
    private static final int SHOW_SEARCH_REQUEST = 4;
    private static final int START_BE_SEARCH = 5;
    private static final int END_BE_SEARCH = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        fragments = new ArrayList<>();

        //init fragments
        fileChoose = FileChooseFragment.newInstance(FileChooseFragment.WEB_TRANSFER);
        lanTransfer = new LANTransferFragment();
        fileReceived = new ReceivedFileFragment();
        webTransfer = new WebTransferFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("list", mDeviceList);
        lanTransfer.setArguments(bundle);

        fragments.add(lanTransfer);
        fragments.add(fileChoose);
        fragments.add(fileReceived);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this, fragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        //选择文件与网页传输的切换监听
        fileChoose.setFragmentChangeListener(new FragmentChangeListener() {
            @Override
            public void onChangeFragment() {
                viewPagerAdapter.setChangeFragment(true);
                fragments.set(1, webTransfer);
                viewPagerAdapter.notifyDataSetChanged();
                Log.i("MainActivity", "change to web transfer");
            }
        });

        webTransfer.setFragmentChangeListener(new FragmentChangeListener() {
            @Override
            public void onChangeFragment() {
                viewPagerAdapter.setChangeFragment(true);
                fragments.set(1, fileChoose);
                viewPagerAdapter.notifyDataSetChanged();
                Log.i("MainActivity", "change to file select");
            }
        });

        PageNavigationView tab = (PageNavigationView) findViewById(R.id.tab);


        NavigationController navigationController = tab.material()
                .addItem(R.drawable.phone, "手机传")
                .addItem(R.drawable.laptop, "网页传")
                .addItem(R.drawable.file, "已接收")
                .build();
        navigationController.setupWithViewPager(viewPager);
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                if (index != old) {
                    viewPager.setCurrentItem(index,true);
                }
                //选中时触发
            }

            @Override
            public void onRepeat(int index) {
                //重复选中时触发
            }
        });

        init();
    }

    private class MyHandler extends Handler {
        private WeakReference<MainActivity> ref;

        MyHandler(MainActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = ref.get();
            switch (msg.what) {
                case MESSAGE_SEARCH_START:
                    mDeviceList.clear();
                    Toast.makeText(MainActivity.this, "开始搜索", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_SEARCH_FINISH:
                    Toast.makeText(MainActivity.this, "结束搜索", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_SEARCH_DEV:
                    lanTransfer.requestNotifyData();
                    break;
                case SHOW_SEARCH_REQUEST:
                    for (RequestSearchData d : lanTransfer.getRequests()) {
                        String show = d.toString();
                        Log.d("MainActivity", "handleMessage: " + d);
                    }
                    break;
                case START_BE_SEARCH:
                    Toast.makeText(MainActivity.this, "开启被发现功能，请稍等", Toast.LENGTH_SHORT).show();
                    break;
                case END_BE_SEARCH:
                    Toast.makeText(MainActivity.this, "关闭被发现功能", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void init() {

        mHandler = new MyHandler(this);

        ClientConfig.setAskFunc(1);
        searchClient = new SearchClient(1024) {
            @Override
            public void onSearchStart() {
                mHandler.sendEmptyMessage(MESSAGE_SEARCH_START);
            }

            @Override
            public void onSearchDev(BaseUserData dev) {
                if (!mDeviceList.contains(dev)) {
                    mDeviceList.add((DeviceData) dev);
                }
                mHandler.sendEmptyMessage(MESSAGE_SEARCH_DEV);
            }

            @Override
            protected void onSearchFinish() {
                mHandler.sendEmptyMessage(MESSAGE_SEARCH_FINISH);
            }

            @Override
            public void printLog(String msg) {
                Log.i("LANDiscover", "client " + msg);
            }
        };

        if (searchClient.isOpen()) {
            Toast.makeText(this, "已经开启搜索，请稍等", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public List<DeviceData> getList() {
        return mDeviceList;
    }

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchClient.startSearch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (searchClient != null) {
            searchClient.close();
        }
        mDeviceList.clear();
    }
}
