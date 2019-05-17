package com.ethernet.app.global;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;


public abstract class BaseFragment extends Fragment {

    public abstract void initView(View view);
    public abstract void setListener(View view);
    protected Context myContext;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myContext = getContext();
    }
}
