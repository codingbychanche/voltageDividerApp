package com.berthold.voltagedivider.FragmentDivider;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.berthold.voltagedivider.Locale;

import java.util.ArrayList;
import java.util.List;

import VoltageDiv.Divider;
import VoltageDiv.DividerResult;
import VoltageDiv.DividerResults;

/**
 * This viewModel constitutes on distinct solution for a
 * voltage divider calculated.
 */
public class FragmentDividerModel extends ViewModel {

    // Locale
    public Locale loc;

    //
    // Index of the solution currently shown
    //
    private int indexOfSolutionCurrentlyShown;

    //
    // The result....
    //
    public DividerResults result;

    //
    // This is an unique identifier for each thread
    // trying to find a solution for a voltage divider.
    public long timestampOfLastCalc, timeStampSolAvailable;

    //
    // The found solution.
    //
    public MutableLiveData<String> currentSolutionShown;
    public MutableLiveData<String> getCurrentSolutionShown() {
        if (currentSolutionShown == null)
            currentSolutionShown = new MutableLiveData<String>();
        return currentSolutionShown;
    }

    //
    // Number of solutions found and index of the solution currently displayed...
    //
    public MutableLiveData<String> numberOfSolAndIndexOfCurrentlyShown;
    public MutableLiveData<String> getNumberOfSolAndIndexOfCurrentlyShown() {
        if (numberOfSolAndIndexOfCurrentlyShown == null) {
            numberOfSolAndIndexOfCurrentlyShown = new MutableLiveData<String>();
            numberOfSolAndIndexOfCurrentlyShown.setValue("0");
            indexOfSolutionCurrentlyShown = 0;
        }
        return numberOfSolAndIndexOfCurrentlyShown;
    }

    //
    // Values for checkboxes showing whether an E-series should be excluded or not
    //
    public MutableLiveData<Boolean> exE3;
    public MutableLiveData<Boolean> doExcludeE3(){
        if (exE3==null) {
            exE3 = new MutableLiveData<>();
            exE3.setValue(false);
        }
        return exE3;
    }
    public MutableLiveData<Boolean> exE6;
    public MutableLiveData<Boolean> doExcludeE6(){
        if (exE6==null) {
            exE6 = new MutableLiveData<>();
            exE6.setValue(false);
        }
        return exE6;
    }
    public MutableLiveData<Boolean> exE12;
    public MutableLiveData<Boolean> doExcludeE12(){
        if (exE12==null) {
            exE12 = new MutableLiveData<>();
            exE12.setValue(false);
        }
        return exE12;
    }
    public MutableLiveData<Boolean> exE24;
    public MutableLiveData<Boolean> doExcludeE24(){
        if (exE24==null) {
            exE24= new MutableLiveData<>();
            exE24.setValue(false);
        }
        return exE24;
    }
    public MutableLiveData<Boolean> exE48;
    public MutableLiveData<Boolean> doExcludeE48(){
        if (exE48==null) {
            exE48 = new MutableLiveData<>();
            exE48.setValue(false);
        }
        return exE48;
    }
    public MutableLiveData<Boolean> exE96;
    public MutableLiveData<Boolean> doExcludeE96(){
        if (exE96==null) {
            exE96= new MutableLiveData<>();
            exE96.setValue(false);
        }
        return exE96;
    }


    // The UI
    //
    // Input fields
    //
    public String vIn, vOut;

    /**
     * Finds the solution for an voltage divider.
     *
     * @param vIn_V
     * @param vOut_V
     */
    public void solveDividerForR1AndR2(String vIn_V, String vOut_V, long timestamp) {

        // Unique idendifier for the last thread started....
        timestampOfLastCalc = timestamp;

        //
        // Find solution for the given voltage divider...
        //
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    double vIn = Double.valueOf(vIn_V);
                    double vOut = Double.valueOf(vOut_V);

                    indexOfSolutionCurrentlyShown = 0;
                    numberOfSolAndIndexOfCurrentlyShown.postValue(loc.getSearchingText());

                    List<Integer> exclude=new ArrayList<>();

                    int e3=0;
                    int e6=0;
                    int e12=0;
                    int e24=0;
                    int e48=0;
                    int e96=0;

                    if (exE3.getValue())
                        e3=3;
                    if (exE6.getValue())
                        e6=6;
                    if(exE12.getValue())
                        e12=12;
                    if(exE24.getValue())
                        e24=24;
                    if (exE48.getValue())
                        e48=48;
                    if (exE96.getValue())
                        e96=96;

                    exclude.add(e3);
                    exclude.add(e6);
                    exclude.add(e12);
                    exclude.add(e24);
                    exclude.add(e48);
                    exclude.add(e96);

                    result = Divider.findResistors(vIn, vOut,exclude);

                    // If a previous calc. is in progress, this prevents that the earliest result found
                    // is overwritten.
                    if (timestamp == timestampOfLastCalc) {
                        timeStampSolAvailable = System.currentTimeMillis();

                        if (result.hasResult()) {
                            currentSolutionShown.postValue(
                                    buildSolutiontext(
                                            result.getListOfResults().get(0))); // Best Result always on Top! Todo Why does result.getBest... not work????

                            numberOfSolAndIndexOfCurrentlyShown.postValue(
                                    buildNumberOfSolFoundAndIndexOfCurrent(result));
                        } else {
                            currentSolutionShown.postValue(loc.getNoSolutionFound());
                            numberOfSolAndIndexOfCurrentlyShown.postValue("0");
                        }
                    }
                } catch (NumberFormatException e) {
                }
            }
        });
        t.start();
    }

    /**
     * Shows the next available solution-
     */
    public void getAndShowNextSolution() {
        if (result!=null) {
            if (indexOfSolutionCurrentlyShown < result.getListOfResults().size() - 1 && result.getListOfResults().size() != 0)
                indexOfSolutionCurrentlyShown++;

            DividerResult r = null;
            //if (result != null) {
                r = result.getListOfResults().get(indexOfSolutionCurrentlyShown);
                String nextSolution = buildSolutiontext(r);
                currentSolutionShown.postValue(nextSolution);
                numberOfSolAndIndexOfCurrentlyShown.postValue(buildNumberOfSolFoundAndIndexOfCurrent(result));
            //}
        }
    }

    /**
     * Shows the previous available solution.
     */
    public void getPreviousSolution() {
        if (result!=null){
            if (indexOfSolutionCurrentlyShown > 0)
                indexOfSolutionCurrentlyShown--;

            DividerResult r = null;
            //if (result != null) {
                r = result.getListOfResults().get(indexOfSolutionCurrentlyShown);
                String nextSolution = buildSolutiontext(r);
                currentSolutionShown.postValue(nextSolution);
                numberOfSolAndIndexOfCurrentlyShown.postValue(buildNumberOfSolFoundAndIndexOfCurrent(result));
            //}
        }
    }

    /**
     * Builds a human readable text for a result of a calculated voltage divider.
     *
     * @param r Instance of {@link DividerResult}.
     * @return Human readable text of the result of a voltage divider calculation.
     */
    public String buildSolutiontext(DividerResult r) {

        StringBuilder solution = new StringBuilder();

        try {
            solution.append("<b><u>Vin=" + result.getInputVoltage_V() + "V     Vout=" + result.getOutputVoltage_V() + "V</b></u><br>");

            if (result.hasResult()) {

                double durationOfCalcInSeconds = (timeStampSolAvailable - timestampOfLastCalc) / 1000;

                solution.append(
                        "R1=" + r.getR1_V() + " Ohm (E" + r.getR1FoundInSeries() + ")<br>" +
                                "R2=" + r.getR2_V() + " Ohm (E" + r.getR2FoundInSeries() + ")<br>" +
                                "<i>Vout=" + r.getvOutCalc_V() + " V     Error:" +
                                r.getActualErrorInOutputVoltage_P() + "%</i><br>");
                solution.append("<p>");
            } else
                solution.append(loc.getNoSolutionFound());
        } catch (NumberFormatException e) {
        }
        return solution.toString();
    }

    /**
     * Additional info about the current solution.
     *
     * @param r An instance of {@link DividerResults}.
     * @return Human readable text showing the total number of results found
     * for the calculated divider and the index of the solution currently shown....
     */
    public String buildNumberOfSolFoundAndIndexOfCurrent(DividerResults r) {
        return (loc.getShowingText() + " " + indexOfSolutionCurrentlyShown + "/" + (r.getListOfResults().size() - 1));
    }
}
