package com.ethernet.app.mainscreen.fragmnet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ethernet.app.R;
import com.ethernet.app.global.BaseFragment;
import com.ethernet.app.global.EventMessage;
import com.ethernet.app.global.GlobalBus;
import com.ethernet.app.global.PreferenceManager;
import com.ethernet.app.mainscreen.model.FuelDataModel;
import com.ethernet.app.utility.Constant;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;


public class FuelFragment extends BaseFragment {

    private static final String TAG = FuelFragment.class.getSimpleName();
    //views
    private TextView litersTextView;
    private TextView priceTextView;
    private TextView ppuTextView;
    private TextView pmsOrAgoTextView;
    //variables
    private static String currentDisplayVol = "";
    private String fuelType,fpType;
    private int sleepTime;

    public FuelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //receiver = new BroadCastReceiverInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel, container, false);

        fuelType = PreferenceManager.getStringForKey(getContext(), Constant.DU_Setting.FUEL_TYPE, Constant.IS_EMPTY);
        fpType = PreferenceManager.getStringForKey(getContext(), Constant.DU_Setting.FP_TYPE, Constant.IS_EMPTY);
        sleepTime = PreferenceManager.getIntForKey(getContext(), Constant.DU_Setting.SLEEP_TIME, 30000);

        initView(view);
        setListener(view);

        if (!fuelType.isEmpty()) {
            pmsOrAgoTextView.setText(fuelType);
            if (fuelType.equalsIgnoreCase("PMS")) {
                pmsOrAgoTextView.setBackgroundResource(R.color.red);
            } else if (fuelType.equalsIgnoreCase("AGO")) {
                pmsOrAgoTextView.setBackgroundResource(R.color.colorAccent);
            }
        }

        GlobalBus.getBus().register(this);

        return view;
    }

    @Override
    public void initView(View view) {
        pmsOrAgoTextView = view.findViewById(R.id.pms_or_ago_text_view);
        litersTextView = view.findViewById(R.id.liters_text_view);
        priceTextView = view.findViewById(R.id.price_text_view);
        ppuTextView = view.findViewById(R.id.ppu_text_view);


    }

    @Override
    public void setListener(View view) {

    }

    @Subscribe
    public void getEventMessage(EventMessage message) {
        //Write code to perform action after event is received.
        String data = message.getEventMessage();
        try {
            Log.d(TAG, "onReceive() called");
            if (data != null && !data.isEmpty()) {
                JSONObject jObj = new JSONObject(data);
                if (jObj.has("Operation")) {
                    FuelDataModel model = new FuelDataModel();
                    model.operation = (jObj.getString("Operation"));
                    model.currentDispSale = (jObj.getString("CurrDispSale"));
                    model.currentDispVol = (jObj.getString("CurrDispVol"));
                    model.ppu = (jObj.getString("ppu"));
                    model.fp = (jObj.getString("fp"));
                    setPmsAndAgoPrice(model);
                }


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setPmsAndAgoPrice(FuelDataModel model) {
        if (model.operation != null) {
            switch (model.operation) {
                case Constant.Operation.CURRENT_DISPENSE:

                    if (!fpType.isEmpty() && fpType.equalsIgnoreCase(model.fp)) {
                        litersTextView.setText(model.currentDispVol);
                        priceTextView.setText(model.currentDispSale);
                        ppuTextView.setText(model.ppu);
                        //needToStop(model.currentDispVol);
                    }
                    break;
            }
        }
    }

    private void needToStop(String currDisplayVol) {

        if (currentDisplayVol.equals(currDisplayVol)
                && !currentDisplayVol.equals("0.00000")
                && !currentDisplayVol.isEmpty()) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {

                     GlobalBus.getBus().post(new EventMessage(Constant.STOP_FUELING));

            }, 10000);


        } else {
            Log.e(TAG, "NOT NEED TO GO");
        }

        currentDisplayVol = currDisplayVol;
    }

    @Override
    public void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.

    }

}
