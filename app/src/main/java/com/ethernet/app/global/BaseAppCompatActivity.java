package com.ethernet.app.global;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseAppCompatActivity extends AppCompatActivity {

    public abstract void initView();
    public abstract void setListener();
    protected Context myContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = getApplication();
    }

}
