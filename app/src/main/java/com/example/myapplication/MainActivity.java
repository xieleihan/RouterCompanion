package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.api.Request;
import com.example.myapplication.ui.OpenwrtContainer;
import com.example.myapplication.utils.LoadImageTask;
import com.example.myapplication.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private LinearLayout rootLayout;

    private EditText inputPassword; // 输入框
    private CheckBox rememberPassword; // 记住密码复选框
    private Button enterButton; //

    // SharedPreferences文件名
    private static final String PREF_NAME = "login_prefs";
    private static final String TEMP_PREF_NAME = "temp_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(R.id.root_layout);
        // 发起请求获取封面背景
        String imgUrl = Request.getPicBackground(); // 你定义的接口
        new LoadImageTask(rootLayout).execute(imgUrl);

        // 点击按钮跳转
        initViews();
        loadSavedData();
        setupClickListener();

        String gatewayIp = NetworkUtils.getGatewayIP(this);
        Log.d("GatewayIP", "当前网关 IP: " + gatewayIp);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.enter_button), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        inputPassword = findViewById(R.id.input_openwrt_password);
        rememberPassword = findViewById(R.id.remember_password);
        enterButton = findViewById(R.id.enter_button);
    }

    private void loadSavedData() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedPassword = preferences.getString("password", "");
        boolean isRemember = preferences.getBoolean("remember", false);

        if (isRemember && !savedPassword.isEmpty()) {
            inputPassword.setText(savedPassword);
            rememberPassword.setChecked(true);
        }
    }

    private void setupClickListener() {
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = inputPassword.getText().toString();
                boolean remember = rememberPassword.isChecked();

                if (remember) {
                    // 永久存储
                    savePasswordPermanently(password);
                } else {
                    // 临时存储
                    savePasswordTemporarily(password);
                }

                // 跳转到下一个页面
                Intent intent = new Intent(MainActivity.this, OpenwrtContainer.class);
                startActivity(intent);
            }
        });
    }

    private void savePasswordPermanently(String password) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", password);
        editor.putBoolean("remember", true);
        editor.apply();
    }

    private void savePasswordTemporarily(String password) {
        SharedPreferences preferences = getSharedPreferences(TEMP_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("temp_password", password);
        editor.putLong("save_time", System.currentTimeMillis());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清除临时数据
        clearTempData();
    }

    private void clearTempData() {
        SharedPreferences preferences = getSharedPreferences(TEMP_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}