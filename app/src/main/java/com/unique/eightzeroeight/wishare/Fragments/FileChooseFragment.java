package com.unique.eightzeroeight.wishare.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unique.eightzeroeight.wishare.Activities.AppContext;
import com.unique.eightzeroeight.wishare.Entities.FileInfo;
import com.unique.eightzeroeight.wishare.FragmentChangeListener;
import com.unique.eightzeroeight.wishare.R;
import com.unique.eightzeroeight.wishare.Utils.SeletedFileListChangedBroadcastReceiver;
import com.unique.eightzeroeight.wishare.Utils.ShowSelectedFileInfoDialog;
import com.unique.eightzeroeight.wishare.Utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FileChooseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FileChooseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileChooseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String WEB_TRANSFER = "WEB";
    public static final String LAN_TRANSFER = "LAN";
    public static final String TYPE = "TYPE";

    String TAG = "FileChooseFragment";
    // TODO: Rename and change types of parameters
    private String typeCode;

    private OnFragmentInteractionListener mListener;
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
    public static final int  REQUEST_CODE_GET_FILE_INFOS = 200;



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


    public FileChooseFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FileChooseFragment newInstance(String typeCode) {
        FileChooseFragment fragment = new FileChooseFragment();
        Bundle args = new Bundle();
        args.putString(TYPE,typeCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.typeCode = getArguments().getString(TYPE, WEB_TRANSFER);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //AppContext.getAppContext().getFileInfoMap().clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_choose, container, false);
        ButterKnife.bind(this,view);
        init();

        return view;
    }

    /**
     * 初始化
     */
    private void init(){

        //Android6.0 requires android.permission.READ_EXTERNAL_STORAGE
        //TODO
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GET_FILE_INFOS);
        }else{
            initData();//初始化数据
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mApkInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_APK);
        mJpgInfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_JPG);
        mMp3InfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP3);
        mMp4InfoFragment = FileInfoFragment.newInstance(FileInfo.TYPE_MP4);
        mCurrentFragment = mApkInfoFragment;

        String[] titles = getResources().getStringArray(R.array.array_res);
        view_pager.setAdapter(new ResPagerAdapter(this.getChildFragmentManager(), titles));
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

        mShowSelectedFileInfoDialog = new ShowSelectedFileInfoDialog(getContext());

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
    private void update(){
        if(mApkInfoFragment != null) mApkInfoFragment.updateFileInfoAdapter();
        if(mJpgInfoFragment != null) mJpgInfoFragment.updateFileInfoAdapter();
        if(mMp3InfoFragment != null) mMp3InfoFragment.updateFileInfoAdapter();
        if(mMp4InfoFragment != null) mMp4InfoFragment.updateFileInfoAdapter();

        //更新已选中Button
        getSelectedView();
    }

    @OnClick({R.id.btn_selected, R.id.btn_next})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.btn_selected:{
//                btn_selected.setEnabled(false);
//                new ShowSelectedFileInfoDialog(getContext()).show();
                if(mShowSelectedFileInfoDialog != null){
                    mShowSelectedFileInfoDialog.show();
                }
                break;
            }
            case R.id.btn_next:{

                if(!AppContext.getAppContext().isFileInfoMapExist()){//不存在选中的文件
                    ToastUtils.show(getContext(), getContext().getString(R.string.tip_please_select_your_file));
                    return;
                }

                if(typeCode.equals(WEB_TRANSFER)){ //跳转到网页传
                    listener.onChangeFragment();
                    return;
                }
                //跳转到应用间传输
                listener.onChangeFragment();
                break;
            }
        }
    }

    /**
     * 获取选中文件的View
     * @return
     */
    public View getSelectedView(){
        //获取SelectedView的时候 触发选择文件
        if(AppContext.getAppContext().getFileInfoMap() != null && AppContext.getAppContext().getFileInfoMap().size() > 0 ){
            setSelectedViewStyle(true);
            int size = AppContext.getAppContext().getFileInfoMap().size();
            btn_selected.setText(getContext().getResources().getString(R.string.str_has_selected_detail, size));
        }else{
            setSelectedViewStyle(false);
            btn_selected.setText(getContext().getResources().getString(R.string.str_has_selected));
        }
        return btn_selected;
    }

    /**
     * 设置选中View的样式
     * @param isEnable
     */
    private void setSelectedViewStyle(boolean isEnable){
        if(isEnable){
            btn_selected.setEnabled(true);
            btn_selected.setBackgroundResource(R.drawable.selector_bottom_text_common);
            btn_selected.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
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
            if(position == 0){ //应用
                mCurrentFragment = mApkInfoFragment;
            }else if(position == 1){ //图片
                mCurrentFragment = mJpgInfoFragment;
            }else if(position == 2){ //音乐
                mCurrentFragment = mMp3InfoFragment;
            }else if(position == 3){ //视频
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
