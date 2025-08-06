package com.example.myapplication.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide; // 导入 Glide
import com.example.myapplication.R;
import com.example.myapplication.model.BannerResponse; // 导入模型类
import com.google.gson.Gson; // 导入 Gson

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    // 替换为您的实际 API URL
    private static final String API_URL = "https://apis.netstart.cn/bcomic/Banner";

    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView textViewTitle;
    private OkHttpClient client;
    private Gson gson;

    public HomeFragment() {
        // 需要一个公共的无参构造函数
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化 OkHttp 客户端和 Gson
        client = new OkHttpClient();
        gson = new Gson();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        imageView = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progressBar);
        textViewTitle = view.findViewById(R.id.textViewTitle); // Find the title TextView

        // Start the network request
        fetchBannerData();
    }

    private void fetchBannerData() {
        // Show progress bar
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Create request
        Request request = new Request.Builder()
                .url(API_URL) // 使用您定义的 API URL
                .build();

        // Enqueue the call (asynchronous)
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network request failed", e);
                // Handle failure on UI thread
                if (getActivity() != null) { // Check if fragment is still attached
                    getActivity().runOnUiThread(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(getContext(), "Network request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected response code: " + response);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(getContext(), "Unexpected response code: " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                    }
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    Log.d(TAG, "Response JSON: " + jsonData);

                    // Parse JSON using Gson
                    BannerResponse bannerResponse = gson.fromJson(jsonData, BannerResponse.class);

                    if (bannerResponse.getCode() == 0 && bannerResponse.getData() != null && !bannerResponse.getData().isEmpty()) {
                        // Get the first item from the list as an example
                        // You might want to implement a banner carousel later
                        List<com.example.myapplication.model.BannerItem> dataList = bannerResponse.getData();
                        if (!dataList.isEmpty()) {
                            com.example.myapplication.model.BannerItem firstBanner = dataList.get(0);

                            // Update UI on the main thread
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    // Load image using Glide
                                    if (imageView != null && firstBanner.getImg() != null && !firstBanner.getImg().isEmpty()) {
                                        // Trim whitespace from URL if necessary
                                        String imageUrl = firstBanner.getImg().trim();
                                        Glide.with(HomeFragment.this)
                                                .load(imageUrl)
                                                .placeholder(R.drawable.ic_launcher_foreground) // Optional placeholder
                                                .into(imageView);
                                    }
                                    // Update title TextView
                                    if (textViewTitle != null) {
                                        textViewTitle.setText(firstBanner.getTitle() != null ? firstBanner.getTitle() : "No Title");
                                    }
                                });
                            }
                        } else {
                            Log.w(TAG, "Data list is empty");
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    Toast.makeText(getContext(), "No banner data received", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    } else {
                        Log.e(TAG, "API returned error or no data. Code: " + bannerResponse.getCode() + ", Msg: " + bannerResponse.getMsg());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);
                                }
                                Toast.makeText(getContext(), "API Error: " + bannerResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing JSON", e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(getContext(), "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                } finally {
                    response.close(); // Important: Close the response body
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel any ongoing requests if needed (OkHttp handles this reasonably well by default)
        // You might want to use tags with OkHttp calls for more control
    }
}