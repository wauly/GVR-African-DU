package com.ethernet.app.settingscreen.data;


import com.ethernet.app.settingscreen.model.BaudRateModel;
import com.ethernet.app.settingscreen.model.NozzleModel;

import java.util.ArrayList;

public class DataConstant {


    public DataConstant(){

    }


    public ArrayList<NozzleModel> getNozalOne(){

        ArrayList<NozzleModel> listOfNozzle = new ArrayList<>();
        NozzleModel model1 = new NozzleModel();
        model1.name = "Petrol";
        model1.isSelected = false;

        NozzleModel model2 = new NozzleModel();
        model2.name = "Diesel";
        model2.isSelected = false;

        NozzleModel model3 = new NozzleModel();
        model3.name = "Power39";
        model3.isSelected = false;

        listOfNozzle.add(model1);
        listOfNozzle.add(model2);
        listOfNozzle.add(model3);

        return listOfNozzle;
    }
    public ArrayList<NozzleModel> getNozalTwo(){

        ArrayList<NozzleModel> listOfNozzle = new ArrayList<>();
        NozzleModel model1 = new NozzleModel();
        model1.name = "Petrol";
        model1.isSelected = false;

        NozzleModel model2 = new NozzleModel();
        model2.name = "Diesel";
        model2.isSelected = false;

        NozzleModel model3 = new NozzleModel();
        model3.name = "Power39";
        model3.isSelected = false;

        listOfNozzle.add(model1);
        listOfNozzle.add(model2);
        listOfNozzle.add(model3);

        return listOfNozzle;
    }

    public ArrayList<BaudRateModel> getBaudRateOne(){

        ArrayList<BaudRateModel> listOfBaudRate = new ArrayList<>();
        BaudRateModel model1 = new BaudRateModel();
        model1.value = "115200";
        model1.isSelected = false;

        BaudRateModel model2 = new BaudRateModel();
        model2.value = "38400";
        model2.isSelected = false;

        listOfBaudRate.add(model1);
        listOfBaudRate.add(model2);

        return listOfBaudRate;
    }
    public ArrayList<BaudRateModel> getBaudRateTwo(){

        ArrayList<BaudRateModel> listOfBaudRate = new ArrayList<>();
        BaudRateModel model1 = new BaudRateModel();
        model1.value = "115200";
        model1.isSelected = false;

        BaudRateModel model2 = new BaudRateModel();
        model2.value = "38400";
        model2.isSelected = false;

        listOfBaudRate.add(model1);
        listOfBaudRate.add(model2);

        return listOfBaudRate;
    }
}
