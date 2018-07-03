package com.unique.eightzeroeight.wishare.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.unique.eightzeroeight.wishare.Adapters.ViewPagerAdapter;
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
    private Fragment webTransfer;
    private Fragment lanTransfer;
    private Fragment fileFrag;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        fragments = new ArrayList<>();

        //init fragments
        webTransfer = new WebTransferFragment();
        lanTransfer = new LANTransferFragment();
        fileFrag = new ReceivedFileFragment();

        fragments.add(webTransfer);
        fragments.add(lanTransfer);
        fragments.add(fileFrag);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this, fragments);
        viewPager.setAdapter(viewPagerAdapter);

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
