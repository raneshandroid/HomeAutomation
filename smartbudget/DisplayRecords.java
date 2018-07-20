package com.raneshprasad.smartbudget;

import java.util.ArrayList;

public class DisplayRecords {

    String date = "";
    ArrayList<Double> electricityBudget = new ArrayList<>();
    ArrayList<Double> timeInterval = new ArrayList<>();
    ArrayList<Integer> index = new ArrayList<>();
    int starterVal = 0;
    String budget = "";
    public DisplayRecords(String date, ArrayList<Double> electricityBudget, ArrayList<Double> timeInterval, int starterVal, ArrayList<Integer> index, String budget){
        this.date = date;
        this.electricityBudget = electricityBudget;
        this.timeInterval = timeInterval;
        this.starterVal = starterVal;
        this.index = index;
        this.budget = budget;
    }
}
