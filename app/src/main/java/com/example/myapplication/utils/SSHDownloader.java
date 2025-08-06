package com.example.myapplication.utils;

import android.content.Context;
import android.util.Log;
import android.content.SharedPreferences;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SSHDownloader {

    private static final String TAG = "SSHDownloader";

    public static void downloadFileToTemp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("temp_config", Context.MODE_PRIVATE);
        String host = prefs.getString("ssh_host", null);
        String password = prefs.getString("ssh_password", null);

        if (host == null || password == null) {
            Log.e(TAG, "Host or password is missing from temporary storage.");
            return;
        }

        int port = 22;
        String user = "root";
        String remoteFile = "/path/to/config.json";

        Session session = null;
        Channel channel = null;

        try {
            // 临时文件路径
            File localFile = new File(context.getCacheDir(), "config.json");
            Log.d(TAG, "Local file path: " + localFile.getAbsolutePath());

            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            // 跳过 hostKey 检查
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftp = (ChannelSftp) channel;

            // 下载到 OutputStream
            try (OutputStream outputStream = new FileOutputStream(localFile)) {
                sftp.get(remoteFile, outputStream);
            }

            Log.d(TAG, "File downloaded successfully to: " + localFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "SSH/SFTP error", e);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}