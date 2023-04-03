package com.berthold.voltagedivider.FragmentResistor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.berthold.voltagedivider.Locale;
import com.berthold.voltagedivider.Main.ProtocolData;

import java.util.ArrayList;
import java.util.List;

import VoltageDiv.GetResistors;
import VoltageDiv.ResistorResult;
import VoltageDiv.ResistorResults;

public class FragmentFindResistorModel extends ViewModel {

    //
    // Locale
    //
    public Locale loc;

    //
    // The input
    //
    public String resistorToBeFoundInput, errorInput;

    //
    // The found solution.
    //
    public MutableLiveData<ProtocolData> resistorValuefoundInAnyOfTheESeries_Ohm;

    public MutableLiveData<ProtocolData> getResistorValuefoundInAnyOfTheESeries_Ohm() {
        if (resistorValuefoundInAnyOfTheESeries_Ohm == null)
            resistorValuefoundInAnyOfTheESeries_Ohm = new MutableLiveData<ProtocolData>();
        return resistorValuefoundInAnyOfTheESeries_Ohm;
    }

    /**
     * Try to find the matching standard value in any of the series.
     *
     * @param resistorValue_Ohm
     * @param tolerableErrorIn_P
     */
    public void findResistor(String resistorValue_Ohm, String tolerableErrorIn_P) {
        try {
            double r = Double.valueOf(resistorValue_Ohm);
            double e = Double.valueOf(tolerableErrorIn_P);

            List<Integer> exclude = new ArrayList<>();

            int e3 = 0;
            int e6 = 0;
            int e12 = 0;
            int e24 = 0;
            int e48 = 0;
            int e96 = 0;

            exclude.add(e3);
            exclude.add(e6);
            exclude.add(e12);
            exclude.add(e24);
            exclude.add(e48);
            exclude.add(e96);
            ResistorResults listOfResistorsFound = GetResistors.getRValueClosestTo(r, e, exclude);


            if (listOfResistorsFound.hasNoSolution()) {
                resistorValuefoundInAnyOfTheESeries_Ohm.setValue(new ProtocolData(loc.getNoSolutionFound(), ProtocolData.IS_RESISTOR_RESULT));

            } else {
                // toDo: Resistors library, in ResistorResult, add global method to determine max and min resistance
                ResistorResult bestMatchingResistor = listOfResistorsFound.getBestMatchingResistor();
                double maxR = (1 + bestMatchingResistor.getSeriesSpecificErrorMargin() / 100) * bestMatchingResistor.getFoundResistorValue_Ohms();
                double minR = (1 - bestMatchingResistor.getSeriesSpecificErrorMargin() / 100) * bestMatchingResistor.getFoundResistorValue_Ohms();

                String solution = loc.getResistorNominalText() + "=" + bestMatchingResistor.getFoundResistorValue_Ohms() + "&Omega;" + " E" + bestMatchingResistor.getESeries() +
                        "   (" + bestMatchingResistor.getActualError_P() + "%)<hr>" +
                        loc.geteSeriesErrorMarginText() + " +/-" + bestMatchingResistor.getSeriesSpecificErrorMargin() + "%<br>" +
                        loc.getResistanceMaxText() + "=" + maxR + " &Omega;<br>" +
                        loc.getResistanceMinText() + "=" + minR + " &Omega;<br>";

                resistorValuefoundInAnyOfTheESeries_Ohm.setValue(new ProtocolData(solution, ProtocolData.IS_RESISTOR_RESULT));
            }
        } catch (NumberFormatException e) {
        }
    }
}
