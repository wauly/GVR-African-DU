package com.ethernet.app.mainscreen.fragmnet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethernet.app.R;
import com.ethernet.app.global.BaseFragment;


public class HorizontalPaymentFuelFragment extends BaseFragment {

    private static final String TAG = HorizontalPaymentFuelFragment.class.getSimpleName();

    public HorizontalPaymentFuelFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_horizontal_payment_fuel, container, false);

        initView(view);
        setListener(view);
        loadFuelFragment();
        loadHorizontalFragment();


        return view;
    }

    @Override
    public void initView(View view) {



    }

    @Override
    public void setListener(View view) {

    }
    private void loadFuelFragment(){
      addFragment(new FuelFragment());
    }
    private void loadHorizontalFragment(){
        replaceFragment(new HorizontalAddFragment());
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.horizontal_and_payment_frame_layout, fragment);
        fragmentTransaction.commit();
    }
    private void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fuel_frame_layout, fragment);
        fragmentTransaction.commit();
    }

}
