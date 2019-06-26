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
import com.ethernet.app.global.EventMessage;
import com.ethernet.app.global.GlobalBus;
import com.ethernet.app.utility.Constant;

import org.greenrobot.eventbus.Subscribe;


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
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
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
    public void clearStack() {
        //Here we are clearing back stack fragment entries
        int backStackEntry = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        }

        //Here we are removing all the fragment that are shown here
        if (getActivity().getSupportFragmentManager().getFragments() != null && getActivity().getSupportFragmentManager().getFragments().size() > 0) {
            for (int i = 0; i < getActivity().getSupportFragmentManager().getFragments().size(); i++) {
                Fragment mFragment = getActivity().getSupportFragmentManager().getFragments().get(i);
                if (mFragment != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                }
            }
        }
    }
    private void loadFuelFragment(){
      addFragment(new FuelFragment());
    }
    private void loadHorizontalFragment(){
        replaceFragment(new HorizontalAddFragment());
    }
    private void loadPaymentFragment(){
        replaceFragment(new PaymentFragment());
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
    @Subscribe
    public void getEventMessage(EventMessage message) {
        String data = message.getEventMessage();
        if(data.equals(Constant.LOAD_PAYMENT_SCREEN)){
            loadPaymentFragment();
        }

    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }

}
