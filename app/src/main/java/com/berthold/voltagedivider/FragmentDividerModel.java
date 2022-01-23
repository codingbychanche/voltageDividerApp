package com.berthold.voltagedivider;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import VoltageDiv.Divider;
import VoltageDiv.DividerResult;
import VoltageDiv.DividerResults;

/**
 * This viewModel constitutes on distinct solution for a
 * voltage divider calculated.
 */
public class FragmentDividerModel extends ViewModel {

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
    public long timestampOfLastCalc;

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

        double vIn = Double.valueOf(vIn_V);
        double vOut = Double.valueOf(vOut_V);

        // Unique idendifier for the last thread started....
        timestampOfLastCalc = timestamp;

        //
        // Find solution for the given voltage divider...
        //
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                currentSolutionShown.postValue("Suche....");

                // If a previous calc. is in progress, this prevents that the earliest result found
                // is overwritten.
                if (timestamp == timestampOfLastCalc) {

                    indexOfSolutionCurrentlyShown = 0;
                    result = Divider.findResistors(vIn, vOut);

                    currentSolutionShown.postValue(
                            buildSolutiontext(result.getSolutionWsmallestErrInOutputVoltage()));

                    numberOfSolAndIndexOfCurrentlyShown.postValue(
                            buildNumberOfSolFoundAndIndexOfCurrent(result));
                }
            }
        });
        t.start();
    }

    /**
     * Shows the next available solution-
     */
    public void getAndShowNextSolution() {
        if (indexOfSolutionCurrentlyShown < result.getListOfResults().size() && result.getListOfResults().size()!=0)
            indexOfSolutionCurrentlyShown++;

        DividerResult r = result.getListOfResults().get(indexOfSolutionCurrentlyShown);

        String nextSolution = buildSolutiontext(r);
        currentSolutionShown.postValue(nextSolution);
        numberOfSolAndIndexOfCurrentlyShown.postValue(buildNumberOfSolFoundAndIndexOfCurrent(result));
    }

    /**
     * Shows the previous available solution.
     */
    public void  getPreviousSolution(){
        if (indexOfSolutionCurrentlyShown > 0)
            indexOfSolutionCurrentlyShown--;

        DividerResult r = result.getListOfResults().get(indexOfSolutionCurrentlyShown);

        String nextSolution = buildSolutiontext(r);
        currentSolutionShown.postValue(nextSolution);
        numberOfSolAndIndexOfCurrentlyShown.postValue(buildNumberOfSolFoundAndIndexOfCurrent(result));
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
            solution.append("Vin=" + result.getInputVoltage_V() + "V     Vout=" + result.getOutputVoltage_V() + "V\n\n");

            if (result.hasResult()) {

                Long timstampSolAvailable = System.currentTimeMillis();

                double durationOfCalcInSeconds = (timstampSolAvailable - timestampOfLastCalc) / 1000;
                
                //DividerResult bestResult = result.getSolutionWsmallestErrInOutputVoltage();
                solution.append(
                        "R1=" + r.getR1_V() + " Ohm (E" + r.getR1FoundInSeries() + ")\n" +
                                "R2=" + r.getR2_V() + " Ohm (E" + r.getR2FoundInSeries() + ")\n" +
                                "Vout=" + r.getvOutCalc_V() + " V     Fehler:" +
                                r.getActualErrorInOutputVoltage_P() + "%\n\n " +
                                "Dauer:" + durationOfCalcInSeconds + " Sekunden");
                solution.append("\n");
            } else
                solution.append("Keine Lösung gefunden.");
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
        return ("Zeige:" + indexOfSolutionCurrentlyShown + "/" + r.getListOfResults().size());
    }
}
