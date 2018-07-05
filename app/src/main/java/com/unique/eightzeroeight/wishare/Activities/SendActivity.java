package com.unique.eightzeroeight.wishare.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unique.eightzeroeight.wishare.R;
import com.unique.eightzeroeight.wishare.network.base.DeviceData;
import com.unique.eightzeroeight.wishare.network.base.RequestSearchData;
import com.unique.eightzeroeight.wishare.network.server.SearchServer;
import com.unique.eightzeroeight.wishare.network.server.ServerConfig;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SendActivity extends AppCompatActivity {

    private long size;
    private String fileName;
    private String path;

    private TextView tvFileName;
    private ProgressBar progressBar;
    private TextView status;

    private ServerSocket serverSocket;
    private SearchServer searchServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        fileName = getIntent().getStringExtra("fileName");
        path = getIntent().getStringExtra("path");
        size = getIntent().getLongExtra("size", 0);


        searchServer = new SearchServer(1024) {
            @Override
            public void printLog(String log) {
                Log.d("LANTransfer", log);
            }

            @Override
            public void onReceiveSearchReq(RequestSearchData data) {
            }
        };

        initView();
        boardcastFile(fileName, size);
        startSocket();
    }

    private void startSocket() {
        try {
            serverSocket = new ServerSocket(23333);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        while (true) {
                            final Socket socket = serverSocket.accept();
                            new SocketThread(socket).start();
                        }
                    } catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                status.setText("传输失败");
                            }
                        });
                    }
                }
            }.start();
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("socket建立失败");
                }
            });
        }
    }

    class SocketThread extends Thread {
        private Socket socket;
        public SocketThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("正在传输中......");
                }
            });
            long bytesRead = 0;
            byte[] buf = new byte[1024];
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
                OutputStream os = socket.getOutputStream();
                while (bis.available() > 0) {
                    int count = bis.read(buf, 0, 1024);
                    if (count > 0) {
                        os.write(buf, 0, count);
                        bytesRead += count;
                    }
                    final long finalBytesRead = bytesRead;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress((int) (finalBytesRead * 100 / size));
                        }
                    });
                }

                bis.close();
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("传输失败");
                    }
                });
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("传输完成");
                }
            });
        }
    }

    private void boardcastFile(String pkgName, long size) {
        ServerConfig.setFunc(1);
        DeviceData deviceData = new DeviceData();
        deviceData.setDevId("" + size);
        deviceData.setFunc(1);
        deviceData.setServiceName("" + size);
        deviceData.setPkgName(pkgName);
        ServerConfig.setDeviceData(deviceData);
        searchServer.init();
    }


    private void initView() {
        tvFileName = findViewById(R.id.tv_filename);
        progressBar = findViewById(R.id.send_progress);
        status = findViewById(R.id.tv_transmit_state);

        tvFileName.setText(fileName);
        progressBar.setProgress(0);
        status.setText("初始化socket...");
    }


    @Override
    public void onPause() {
        super.onPause();
        if (searchServer != null) {
            searchServer.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchServer != null && !searchServer.isOpen()) {
            searchServer.init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
