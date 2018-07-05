package com.unique.eightzeroeight.wishare.Activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unique.eightzeroeight.wishare.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveActivity extends AppCompatActivity {

    private String ip;
    private int port;
    private String fileName;
    private long size;
    private Button acceptButton;
    private Socket socket;
    private BufferedInputStream bis;
    private TextView tvFileName;
    private ProgressBar progressBar;
    private TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        port = intent.getIntExtra("port", 0);
        size = Long.parseLong(intent.getStringExtra("size"));
        fileName = intent.getStringExtra("fileName");
        acceptButton = findViewById(R.id.btn_accept);
        tvFileName = findViewById(R.id.tv_filename);
        progressBar = findViewById(R.id.send_progress);
        status = findViewById(R.id.tv_transmit_state);
        tvFileName.setText(fileName);
        progressBar.setProgress(0);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        long sizeReceived = 0;
                        try {
                            socket = new Socket(ip, 23333);
                            bis = new BufferedInputStream(socket.getInputStream());
                            String path = Environment.getExternalStorageDirectory() + "/fileReceived/";
                            File file = new File(path + fileName);
                            FileOutputStream fos = new FileOutputStream(file);
                            byte[] buf = new byte[1024];
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText("接收中......");
                                }
                            });
                            while (socket.isConnected() && !socket.isClosed()) {
                                int res = bis.read(buf, 0, 1024);
                                if (res > 0) {
                                    fos.write(buf, 0, res);
                                    sizeReceived += res;
                                }

                                if (res < 0) {
                                    break;
                                }
                                final long finalSizeReceived = sizeReceived;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress((int)(finalSizeReceived * 100 / size));
                                    }
                                });
                            }
                            fos.close();
                            bis.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText("接收完成");
                                }
                            });
                        } catch (IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText("初始化socket失败");
                                }
                            });
                        }
                    }
                }.start();
            }
        });
    }
}
