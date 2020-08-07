package com.example.androidweightdemos;



import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme_content);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ImageView iv = findViewById(R.id.iv_fish);
//        iv.setImageDrawable(new FishDrawable());
    }
}