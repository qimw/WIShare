package com.unique.eightzeroeight.wishare.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unique.eightzeroeight.wishare.Activities.AppContext;
import com.unique.eightzeroeight.wishare.Entities.FileInfo;
import com.unique.eightzeroeight.wishare.Entities.WifiInfo;
import com.unique.eightzeroeight.wishare.FragmentChangeListener;
import com.unique.eightzeroeight.wishare.R;
import com.unique.eightzeroeight.wishare.Utils.ApMgr;
import com.unique.eightzeroeight.wishare.Utils.ClassifyUtils;
import com.unique.eightzeroeight.wishare.Utils.Constant;
import com.unique.eightzeroeight.wishare.Utils.FileUtils;
import com.unique.eightzeroeight.wishare.Utils.NetUtils;
import com.unique.eightzeroeight.wishare.Utils.TextUtils;
import com.unique.eightzeroeight.wishare.Utils.WifiAPBroadcastReceiver;
import com.unique.eightzeroeight.wishare.Utils.WifiMgr;
import com.unique.eightzeroeight.wishare.micro_server.AndroidMicroServer;
import com.unique.eightzeroeight.wishare.micro_server.DownloadResUriHandler;
import com.unique.eightzeroeight.wishare.micro_server.IOStreamUtils;
import com.unique.eightzeroeight.wishare.micro_server.ImageResUriHandler;
import com.unique.eightzeroeight.wishare.micro_server.IndexResUriHandler;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebTransferFragment extends Fragment {

    private FragmentChangeListener listener;

    private static final String TAG = "WebTransferFragment";
    /**
     * Topbar相关UI
     */

    @Bind(R.id.tv_tip_1)
    TextView tv_tip_1;
    @Bind(R.id.tv_tip_2)
    TextView tv_tip_2;
    @Bind(R.id.wifi_qrcode)
    ImageView code;
    WifiAPBroadcastReceiver mWifiAPBroadcastReceiver;

    public static String hotspotIpAddr;

    /**
     * android 网页传服务器
     */
    AndroidMicroServer mAndroidMicroServer = null;

    private boolean mIsInitialized = false;
    private WifiInfo info;
    public WebTransferFragment() {
        // Required empty public constructor
    }

    public static WebTransferFragment newInstance(String param1, String param2) {
        WebTransferFragment fragment = new WebTransferFragment();

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
        View view = inflater.inflate(R.layout.fragment_web_transfer, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



    public void setFragmentChangeListener(FragmentChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 初始化
     */
    private void init() {

        //1.初始化热点
//        WifiMgr.getInstance(getContext()).disableWifi();
//        if (ApMgr.isApOn(getContext())) {
//            ApMgr.disableAp(getContext());
//        }

        mWifiAPBroadcastReceiver = new WifiAPBroadcastReceiver() {
            @Override
            public void onWifiApEnabled() {
                Log.i(TAG, "======>>>onWifiApEnabled !!!");
                if (!mIsInitialized) {
//                    mUdpServerRuannable = createSendMsgToFileSenderRunnable();
//                    AppContext.MAIN_EXECUTOR.execute(mUdpServerRuannable);
                    try {
                        Log.i(TAG, "======>>>before server open");
                        AppContext.MAIN_EXECUTOR.execute(createServer());
                        mIsInitialized = true;
                        Log.i(TAG, "======>>>after server open");

                    } catch (Exception e) {
                        e.printStackTrace();
                        mIsInitialized = false;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(WifiAPBroadcastReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        getActivity().registerReceiver(mWifiAPBroadcastReceiver, filter);

//        ApMgr.isApOn(getContext()); // check Ap state :boolean
//        ApMgr.openAp(getActivity(), new ApMgr.OpenApCallback() {
//            @Override
//            public void callBack(WifiInfo info) {
//                WebTransferFragment.this.info = info;
//                Log.i(TAG, info.toString());
//                Log.i(TAG, "wifi start init");
//                initUI();
//            }
//        }); // change Ap state :boolean
        info = new WifiInfo(null, null);
        initUI();
        try {
            AppContext.MAIN_EXECUTOR.execute(createServer());
        } catch (Exception e) {
            e.printStackTrace();
        }



//        tv_tip_1.setText(getResources().getString(R.string.tip_web_transfer_first_tip).replace("{hotspot}", ssid));
    }

    /**
     * 初始化UI
     */
    private void initUI() {


        String normalColor = "#ff000000";
        String highlightColor = "#1467CD";
//        <font color=\'#ff0000\'>【题】</font>
        String ssid = TextUtils.isNullOrBlank(android.os.Build.DEVICE) ? Constant.DEFAULT_SSID : android.os.Build.DEVICE;
        String tip1 = getContext().getResources().getString(R.string.tip_web_transfer_first_tip).replace("{hotspot}",
                "SSID:" + info.getSsid() + "<br>PWD:" + info.getPwd());
        String[] tip1StringArray = tip1.split("\\n");
        Spanned tip1Spanned = Html.fromHtml("<font color='" + normalColor + "'>" + tip1StringArray[0].trim() + "</font><br>"
                + "<font color='" + normalColor + "'>" + tip1StringArray[1].trim() + "</font><br>"
                + "<font color='" + highlightColor + "'>" + tip1StringArray[2].trim() + "</font><br>"
                + "<font color='" + normalColor + "'>" + tip1StringArray[3].trim() + "</font><br>");
        tv_tip_1.setText(tip1Spanned);
        //设置二维码
        Bitmap img = CodeUtils.createImage(info.getSsid() + " " + info.getPwd(), 280, 280, null);
        code.setImageBitmap(img);

        String tip2 = getContext().getResources().getString(R.string.tip_web_transfer_second_tip);
        String[] tip2StringArray = tip2.split("\\n");
        Spanned tip2Spanned = Html.fromHtml("<font color='" + normalColor + "'>" + tip2StringArray[0].trim() + "</font><br>"
                + "<font color='" + normalColor + "'>" + tip2StringArray[1].trim() + "</font><br>"
                + "<font color='" + highlightColor + "'>" + tip2StringArray[2].trim() + "</font><br>"
                + "<font color='" + normalColor + "'>" + tip2StringArray[3].trim() + "</font><br>");
        tv_tip_2.setText(tip2Spanned);
    }



    public void onBackPressed() {

        if (mWifiAPBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mWifiAPBroadcastReceiver);
            mWifiAPBroadcastReceiver = null;
        }

        closeServer();

        //关闭热点
        ApMgr.disableAp(getContext());

        //清楚所选中的文件
        AppContext.getAppContext().getFileInfoMap().clear();

    }

    /**
     * 创建一个AndroidMicroServer
     *
     * @return
     * @throws Exception
     */
    public Runnable createServer() throws Exception {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    // 确保热点开启之后获取得到IP地址
                    //String hotspotIpAddr = WifiMgr.getInstance(getContext()).getHotspotLocalIpAddress();
                    int address = ((WifiManager) AppContext.getAppContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getDhcpInfo().ipAddress;

                    hotspotIpAddr = ((address & 0xFF)
                            + "." + ((address >> 8) & 0xFF)
                            + "." + ((address >> 16) & 0xFF)
                            + "." + ((address >> 24) & 0xFF));
                    int count = 0;
//                    while (hotspotIpAddr.equals(Constant.DEFAULT_UNKOWN_IP) && count < Constant.DEFAULT_TRY_TIME) {
//                        Thread.sleep(1000);
//                        hotspotIpAddr = WifiMgr.getInstance(getContext()).getIpAddressFromHotspot();
//                        Log.i(TAG, "receiver serverIp ----->>>" + hotspotIpAddr);
//                        count++;
//                    }

                    // 即使热点wifi的IP地址也是无法连接网络 所以采取此策略
                    count = 0;
                    while (!NetUtils.pingIpAddress(hotspotIpAddr) && count < Constant.DEFAULT_TRY_TIME) {
                        Thread.sleep(500);
                        Log.i(TAG, "try to ping ----->>>" + hotspotIpAddr + " - " + count);
                        count++;
                    }
                    Log.i(TAG, hotspotIpAddr);
                } catch (Exception e) {
                    //maybe not get the hotspot ip
                }
                mAndroidMicroServer = new AndroidMicroServer(Constant.DEFAULT_MICRO_SERVER_PORT);
                mAndroidMicroServer.resgisterResUriHandler(new MyIndexResUriHandler(getActivity(), AppContext.getAppContext().getFileInfoMap()));
                mAndroidMicroServer.resgisterResUriHandler(new ImageResUriHandler(getActivity()));
                mAndroidMicroServer.resgisterResUriHandler(new DownloadResUriHandler(getActivity()));
                mAndroidMicroServer.start();
            }
        };

    }


    /**
     * 关闭Android微服务器
     */
    private void closeServer() {
        if (mAndroidMicroServer != null) {
            mAndroidMicroServer.stop();
            mAndroidMicroServer = null;
        }
    }


    static class MyIndexResUriHandler extends IndexResUriHandler {

        public static final String DOWNLOAD_PREFIX = "http://" + hotspotIpAddr +":4766/download/";
        public static final String IMAGE_PREFIX = "http://" + hotspotIpAddr + ":4766/image/";
        public static final String DEFAULT_IMAGE_PATH = "http://" + hotspotIpAddr + ":4766/image/logo.png";

        Activity sActivity;
        Map<String, FileInfo> sFileInfoMap = null;


        public MyIndexResUriHandler(Activity activity) {
            super(activity);
            this.sActivity = activity;
        }

        public MyIndexResUriHandler(Activity activity, Map<String, FileInfo> fileMap) {
            super(activity);
            this.sActivity = activity;
            this.sFileInfoMap = fileMap;
        }


        @Override
        public String convert(String indexHtml) {
            StringBuilder allFileListInfoHtmlBuilder = new StringBuilder();
            int count = this.sFileInfoMap.size();
            indexHtml = indexHtml.replaceAll("\\{app_avatar\\}", DEFAULT_IMAGE_PATH);
            indexHtml = indexHtml.replaceAll("\\{app_path\\}", DOWNLOAD_PREFIX);
            indexHtml = indexHtml.replaceAll("\\{app_name\\}", this.sActivity.getResources().getString(R.string.app_name));
            String ssid = TextUtils.isNullOrBlank(android.os.Build.DEVICE) ? Constant.DEFAULT_SSID : android.os.Build.DEVICE;
            indexHtml = indexHtml.replaceAll("\\{file_share\\}", ssid);
            indexHtml = indexHtml.replaceAll("\\{file_count\\}", String.valueOf(count));

            List<FileInfo> apkInfos = ClassifyUtils.filter(this.sFileInfoMap, FileInfo.TYPE_APK);
            List<FileInfo> jpgInfos = ClassifyUtils.filter(this.sFileInfoMap, FileInfo.TYPE_JPG);
            List<FileInfo> mp3Infos = ClassifyUtils.filter(this.sFileInfoMap, FileInfo.TYPE_MP3);
            List<FileInfo> mp4Infos = ClassifyUtils.filter(this.sFileInfoMap, FileInfo.TYPE_MP4);

            try {
                String apkInfosHtml = getClassifyFileInfoListHtml(apkInfos, FileInfo.TYPE_APK);
                String jpgInfosHtml = getClassifyFileInfoListHtml(jpgInfos, FileInfo.TYPE_JPG);
                String mp3InfosHtml = getClassifyFileInfoListHtml(mp3Infos, FileInfo.TYPE_MP3);
                String mp4InfosHtml = getClassifyFileInfoListHtml(mp4Infos, FileInfo.TYPE_MP4);

                allFileListInfoHtmlBuilder.append(apkInfosHtml);
                allFileListInfoHtmlBuilder.append(jpgInfosHtml);
                allFileListInfoHtmlBuilder.append(mp3InfosHtml);
                allFileListInfoHtmlBuilder.append(mp4InfosHtml);
                indexHtml = indexHtml.replaceAll("\\{file_list_template\\}", allFileListInfoHtmlBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return indexHtml;
        }

        /**
         * 获取指定文件类型的的html字符串
         *
         * @param fieInfos
         * @throws IOException
         */
        private String getFileInfoListHtml(List<FileInfo> fieInfos) throws IOException {
            StringBuilder sb = new StringBuilder();
            for (FileInfo fileInfo : fieInfos) {
                String fileInfoHtml = IOStreamUtils.inputStreamToString(sActivity.getAssets().open(Constant.NAME_FILE_TEMPLATE));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_avatar\\}", IMAGE_PREFIX + FileUtils.getFileName(fileInfo.getFilePath()));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_name\\}", FileUtils.getFileName(fileInfo.getFilePath()));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_size\\}", FileUtils.getFileSize(fileInfo.getSize()));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_path\\}", DOWNLOAD_PREFIX + FileUtils.getFileName(fileInfo.getFilePath()));

                sb.append(fileInfoHtml);
            }

            return sb.toString();
        }

        /**
         * 获取大类别的Html字符串
         *
         * @param fileInfos
         * @param type
         * @return
         * @throws IOException
         */
        private String getClassifyFileInfoListHtml(List<FileInfo> fileInfos, int type) throws IOException {
            if (fileInfos == null || fileInfos.size() <= 0) {
                return "";
            }

            String classifyHtml = IOStreamUtils.inputStreamToString(sActivity.getAssets().open(Constant.NAME_CLASSIFY_TEMPLATE));

            String className = "";
            switch (type) {
                case FileInfo.TYPE_APK: {
                    className = sActivity.getResources().getString(R.string.str_apk_desc);
                    break;
                }
                case FileInfo.TYPE_JPG: {
                    className = sActivity.getResources().getString(R.string.str_jpeg_desc);
                    break;
                }
                case FileInfo.TYPE_MP3: {
                    className = sActivity.getResources().getString(R.string.str_mp3_desc);
                    break;
                }
                case FileInfo.TYPE_MP4: {
                    className = sActivity.getResources().getString(R.string.str_mp4_desc);
                    break;
                }

            }
            classifyHtml = classifyHtml.replaceAll("\\{class_name\\}", className);
            classifyHtml = classifyHtml.replaceAll("\\{class_count\\}", String.valueOf(fileInfos.size()));
            classifyHtml = classifyHtml.replaceAll("\\{file_list\\}", getFileInfoListHtml(fileInfos));

            return classifyHtml;
        }
    }

}
