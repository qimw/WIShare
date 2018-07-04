package com.unique.eightzeroeight.wishare.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by viseator on 2016/12/20.
 * Wudi
 * viseator@gmail.com
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Fragment> fragments;
    private FragmentManager fragmentManager;
    //控制从file choose fragment跳转到web transfer fragment
    private boolean changeFragment = false;

    public ViewPagerAdapter(FragmentManager fm, Context context, List<Fragment> fragments) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        String fragmentTag = fragment.getTag();
        if (changeFragment) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(fragment);
            fragment = fragments.get(1);
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commit();
            changeFragment = false;
        }
        return fragment;
    }

    public boolean isChangeFragment() {
        return changeFragment;
    }

    public void setChangeFragment(boolean changeFragment) {
        this.changeFragment = changeFragment;
    }
}
