package com.berthold.voltagedivider.FragmentResistor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.berthold.voltagedivider.Locale;

import java.util.ArrayList;
import java.util.List;

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


            List<Integer> exclude=new ArrayList<>();

            int e3=0;
            int e6=0;
            int e12=0;
            int e24=0;
            int e48=0;
            int e96=0;

            exclude.add(e3);
            exclude.add(e6);
            exclude.add(e12);
            exclude.add(e24);
            exclude.add(e48);
            exclude.add(e96);
            ResistorResult rFound=GetResistors.getRValueClosestTo(r,e,exclude);

            if (rFound.found()) {
                String solution = rFound.getFoundResistorValue_Ohms() + "&Omega;" + " E" + rFound.getESeries() + "   " + rFound.getActualError_P() + "%";
                resistorValuefoundInAnyOfTheESeries_Ohm.setValue(solution);
            } else
                resistorValuefoundInAnyOfTheESeries_Ohm.setValue(loc.getNoSolutionFound());

        } catch (NumberFormatException e){}
    }
}
