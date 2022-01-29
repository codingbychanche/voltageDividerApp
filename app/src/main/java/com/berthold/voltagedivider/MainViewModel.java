package com.berthold.voltagedivider;

import android.content.Context;
import android.content.res.Resources;

import androidx.core.text.HtmlCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {


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
    // This string holds a text which is meant to be displayed inside
    // of the protocol fragment. E.g. the result of a calculation, an error....
    //
    public MutableLiveData<String> protokollOutput;
    public MutableLiveData<String> getProtokollOutput(){
        if (protokollOutput==null) {
            protokollOutput = new MutableLiveData<String>();
            protokollOutput.setValue(HTMLTools.makeHeader("Protokolierung......"));

        }
        return protokollOutput;
    }

    /**
     * Returns the version from the app's Google Play store listing...
     *
     * @param c
     * @return A String containing the version tag.
     */
    public String getAppVersionfromGooglePlay(Context c) {
        String latest;
        VersionChecker vc = new VersionChecker();

        try {
            latest = vc.execute().get();
        } catch (Exception e) {
            latest = "-";
        }
        return latest;
    }

}
