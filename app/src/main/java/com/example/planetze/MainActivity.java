package com.example.planetze;

import android.os.Bundle;

public class MainActivity extends BaseActivity {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.template;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}


