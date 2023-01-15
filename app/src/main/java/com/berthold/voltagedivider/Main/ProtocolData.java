package com.berthold.voltagedivider.Main;

public class ProtocolData {
    private String  SolutionString;

   private int typeOfSolution;
   public final static int IS_INFO=0,IS_DIVIDER_RESULT=1,IS_RESISTOR_RESULT=2;

    public ProtocolData(String solutionString, int typeOfSolution) {
        this.SolutionString = solutionString;
        this.typeOfSolution = typeOfSolution;
    }

    public String getSolutionString() {
        return SolutionString;
    }

    public int getTypeOfSolution() {
        return typeOfSolution;
    }

    public void setTypeOfSolution(int typeOfSolution) {
        this.typeOfSolution = typeOfSolution;
    }
}

