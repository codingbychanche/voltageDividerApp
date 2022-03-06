package com.berthold.voltagedivider.FragmentResistor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.berthold.voltagedivider.Locale;

import VoltageDiv.GetResistors;
import VoltageDiv.ResistorResult;

public class FragmentFindResistorModel extends ViewModel {

    //
    // Locale
    //
    public Locale loc;

    //
    // The input
    //
    public String resistorToBeFoundInput,errorInput;

    //
    // The found solution.
    //
    public MutableLiveData<String> resistorValuefoundInAnyOfTheESeries_Ohm;
    public MutableLiveData<String> getResistorValuefoundInAnyOfTheESeries_Ohm(){
        if(resistorValuefoundInAnyOfTheESeries_Ohm ==null)
            resistorValuefoundInAnyOfTheESeries_Ohm =new MutableLiveData<String>();
        return resistorValuefoundInAnyOfTheESeries_Ohm;
    }

    /**
     * Try to find the matching standard value in any of the series.
     *
     * @param resistorValue_Ohm
     * @param tolerableErrorIn_P
     */
    public void findResistor(String resistorValue_Ohm, String tolerableErrorIn_P){
        try{
            double r=Double.valueOf(resistorValue_Ohm);
            double e=Double.valueOf(tolerableErrorIn_P);
            ResistorResult rFound=GetResistors.getRValueClosestTo(r,e);

            if (rFound.found()) {
                String solution = rFound.getFoundResistorValue_Ohms() + " Ohm" + " E" + rFound.getBelongsToESeries() + "   " + rFound.getActualError_P() + "%";
                resistorValuefoundInAnyOfTheESeries_Ohm.setValue(solution);
            } else
                resistorValuefoundInAnyOfTheESeries_Ohm.setValue(loc.getNoSolutionFound());

        } catch (NumberFormatException e){}
    }
}