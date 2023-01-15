package com.berthold.voltagedivider.Main;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.berthold.voltagedivider.Locale;

public class MainViewModel extends ViewModel {

    //
    // Locale
    //
    public Locale loc;

    //
    // The current fragment shown inside the container (e.g. find resistor, divider wtc......)
    //
    public MutableLiveData<String> currentGFragmentShown;
    public MutableLiveData<String> getCurrentGFragmentShown() {
        if (currentGFragmentShown == null) {
            currentGFragmentShown = new MutableLiveData<String>();
            currentGFragmentShown.setValue("FindResistor");
        }
        return currentGFragmentShown;
    }

    //
    // This ist the list holding the protocol
    // entries.
    //
    public MutableLiveData<ProtocolData> protocolData;
    public MutableLiveData<ProtocolData> getProtocol(){
        if (protocolData==null){
            protocolData=new MutableLiveData();
            protocolData.setValue(new ProtocolData(loc.protocolStartText,ProtocolData.IS_INFO));
        }
        return protocolData;
    }
}
