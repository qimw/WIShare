package com.unique.eightzeroeight.wishare.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.unique.eightzeroeight.wishare.Entities.FileInfo;
import com.unique.eightzeroeight.wishare.FragmentChangeListener;
import com.unique.eightzeroeight.wishare.Fragments.FileChooseFragment;
import com.unique.eightzeroeight.wishare.Fragments.FileInfoFragment;
import com.unique.eightzeroeight.wishare.R;
import com.unique.eightzeroeight.wishare.Utils.SeletedFileListChangedBroadcastReceiver;
import com.unique.eightzeroeight.wishare.Utils.ShowSelectedFileInfoDialog;

import java.util.Collection;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileChooseActivity extends AppCompatActivity {

    String TAG = "FileChooseFragment";
    // TODO: Rename and change types of parameters
    private String typeCode;

    private FileChooseFragment.OnFragmentInteractionListener mListener;
    private FragmentChangeListener listener;
    /**
     * 选中文件列表的对话框
     */
    ShowSelectedFileInfoDialog mShowSelectedFileInfoDialog;

    /**
     * 更新文件列表的广播
     */
    SeletedFileListChangedBroadcastReceiver mSeletedFileListChangedBroadcastReceiver = null;


    /**
     * 获取文件的请求码
     */
    public static final int REQUEST_CODE_GET_FILE_INFOS = 200;


    /**
     * BottomBar相关UI
     */
    @Bind(R.id.btn_selected)
    Button btn_selected;
    @Bind(R.id.btn_next)
    Button btn_next;

    /**
     * 其他UI
     */
    @Bind(R.id.tab_layout)
    TabLayout tab_layout;
    @Bind(R.id.view_pager)
    ViewPager view_pager;


    /**
     * 应用，图片，音频， 视频 文件Fragment
     */
    FileInfoFragment mCurrentFragment;
    FileInfoFragment mApkInfoFragment;
    FileInfoFragment mJpgInfoFragment;
    FileInfoFragment mMp3InfoFragment;
    FileInfoFragment mMp4InfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_file_choose);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        AppContext.getAppContext().getFileInfoMap().clear();
        //Android6.0 requires android.permission.READ_EXTERNAL_STORAGE
        //TODO
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GET_FILE_INFOS);
        } else {
            initData();//初始化数据
        }
    }

    private void initData() {
        mApkInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_APK);
        mJpgInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_JPG);
        mMp3InfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP3);
        mMp4InfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP4);
        mCurrentFragment = mApkInfoFragment;

        String[] titles = getResources().getStringArray(R.array.array_res);
        view_pager.setAdapter(new FileChooseActivity.ResPagerAdapter(getSupportFragmentManager(), titles));
        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                <item>应用</item>
//                <item>图片</item>
//                <item>音乐</item>
//                <item>视频</item>
                if (position == 0) { //应用

                } else if (position == 1) { //图片

                } else if (position == 2) { //音乐

                } else if (position == 3) { //视频

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        view_pager.setOffscreenPageLimit(4);

        tab_layout.setTabMode(TabLayout.MODE_FIXED);
        tab_layout.setupWithViewPager(view_pager);

        setSelectedViewStyle(false);

        mShowSelectedFileInfoDialog = new ShowSelectedFileInfoDialog(this);

        mSeletedFileListChangedBroadcastReceiver = new SeletedFileListChangedBroadcastReceiver() {
            @Override
            public void onSeletecdFileListChanged() {
                //TODO udpate file list
                update();
                Log.i(TAG, "======>>>udpate file list");
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SeletedFileListChangedBroadcastReceiver.ACTION_CHOOSE_FILE_LIST_CHANGED);
        AppContext.getAppContext().registerReceiver(mSeletedFileListChangedBroadcastReceiver, intentFilter);
    }

    /**
     * 更新选中文件列表的状态
     */
    private void update() {
        if (mApkInfoFragment != null) mApkInfoFragment.updateFileInfoAdapter();
        if (mJpgInfoFragment != null) mJpgInfoFragment.updateFileInfoAdapter();
        if (mMp3InfoFragment != null) mMp3InfoFragment.updateFileInfoAdapter();
        if (mMp4InfoFragment != null) mMp4InfoFragment.updateFileInfoAdapter();

        //更新已选中Button
        getSelectedView();
    }

    @OnClick({R.id.btn_selected, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_selected: {
//                btn_selected.setEnabled(false);
//                new ShowSelectedFileInfoDialog(getContext()).show();
                if (mShowSelectedFileInfoDialog != null) {
                    mShowSelectedFileInfoDialog.show();
                }
                break;
            }
            case R.id.btn_next: {
                if (AppContext.getAppContext().getFileInfoMap().size() > 1) {
                    Toast.makeText(this, "只能选择一个文件！", Toast.LENGTH_SHORT).show();
                } else if (AppContext.getAppContext().getFileInfoMap().size() == 0) {
                    Toast.makeText(this, "请选择文件", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(FileChooseActivity.this, SendActivity.class);
                    Collection<FileInfo> collection = AppContext.getAppContext().getFileInfoMap().values();
                    FileInfo info = collection.iterator().next();
                    intent.putExtra("fileName", info.getName());
                    intent.putExtra("path", info.getFilePath());
                    intent.putExtra("size", info.getSize());
                    startActivity(intent);
                }
                break;
            }
        }
    }

    /**
     * 获取选中文件的View
     *
     * @return
     */
    public View getSelectedView() {
        //获取SelectedView的时候 触发选择文件
        if (AppContext.getAppContext().getFileInfoMap() != null && AppContext.getAppContext().getFileInfoMap().size() > 0) {
            setSelectedViewStyle(true);
            int size = AppContext.getAppContext().getFileInfoMap().size();
            btn_selected.setText(getResources().getString(R.string.str_has_selected_detail, size));
        } else {
            setSelectedViewStyle(false);
            btn_selected.setText(getResources().getString(R.string.str_has_selected));
        }
        return btn_selected;
    }

    /**
     * 设置选中View的样式
     *
     * @param isEnable
     */
    private void setSelectedViewStyle(boolean isEnable) {
        if (isEnable) {
            btn_selected.setEnabled(true);
            btn_selected.setBackgroundResource(R.drawable.selector_bottom_text_common);
            btn_selected.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            btn_selected.setEnabled(false);
            btn_selected.setBackgroundResource(R.drawable.shape_bottom_text_unenable);
            btn_selected.setTextColor(getResources().getColor(R.color.darker_gray));
        }
    }

    public void setFragmentChangeListener(FragmentChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 资源的PagerAdapter
     */
    class ResPagerAdapter extends FragmentPagerAdapter {
        String[] sTitleArray;

        public ResPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ResPagerAdapter(FragmentManager fm, String[] sTitleArray) {
            this(fm);
            this.sTitleArray = sTitleArray;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) { //应用
                mCurrentFragment = mApkInfoFragment;
            } else if (position == 1) { //图片
                mCurrentFragment = mJpgInfoFragment;
            } else if (position == 2) { //音乐
                mCurrentFragment = mMp3InfoFragment;
            } else if (position == 3) { //视频
                mCurrentFragment = mMp4InfoFragment;
            }
            return mCurrentFragment;
        }

        @Override
        public int getCount() {
            return sTitleArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sTitleArray[position];
        }
    }
}
