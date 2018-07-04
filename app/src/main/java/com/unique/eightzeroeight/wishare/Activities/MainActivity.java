package com.unique.eightzeroeight.wishare.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.unique.eightzeroeight.wishare.Adapters.ViewPagerAdapter;
import com.unique.eightzeroeight.wishare.FragmentChangeListener;
import com.unique.eightzeroeight.wishare.Fragments.FileChooseFragment;
import com.unique.eightzeroeight.wishare.Fragments.LANTransferFragment;
import com.unique.eightzeroeight.wishare.Fragments.ReceivedFileFragment;
import com.unique.eightzeroeight.wishare.Fragments.WebTransferFragment;
import com.unique.eightzeroeight.wishare.R;

import java.util.ArrayList;

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
                .addItem(android.R.drawable.ic_menu_camera, "相机")
                .addItem(android.R.drawable.ic_menu_compass, "位置")
                .addItem(android.R.drawable.ic_menu_search, "搜索")
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
    }



}
