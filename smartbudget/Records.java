package com.raneshprasad.smartbudget;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class Records extends Fragment {

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    boolean first = true;
    double initialTime = 0;
    int c = 0;
    double prevVal = 0;
    boolean inTheArray = false;
    ArrayList<Integer> starterVal = new ArrayList<>();
    int tally = 0;
    String date = "";
    int checkCounter = 0;
    boolean intital = true;
    String budget = "";
    ArrayList<DisplayRecords> records = new ArrayList<>();
    //int i = 0;
    public Records() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        records.clear();
        final ArrayList<Double> time = new ArrayList<>();
        final ArrayList<Double> electricity = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_records, container, false);
        final RecyclerView recView = v.findViewById(R.id.recycleRecords);





        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //first = true;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    checkCounter++;
                }
                if(first) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String val = postSnapshot.child("a").getValue().toString();
                        Double valDob = Double.parseDouble(val);

                        String amountVal = postSnapshot.child("b").getValue().toString();
                        Double amountValDub = Double.parseDouble(amountVal);


                        if (amountValDub == 0) {
                            //skipArray += c;
                        /*for(int i = 0; i < starterVal.size(); i++){
                            if(starterVal.get(i) == c){
                                Log.d("I broke", "I broke");
                                inTheArray = true;
                                break;
                            }
                        }*/
                            //if(!inTheArray) {
                            starterVal.add(c);
                            //}
                            //inTheArray = false;
                            //series.clearCursorModeCache();

                        }
                        prevVal = amountValDub;
                        c++;
                    }
                    first = false;
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //for(i = 0; i < starterVal.size(); i++) {

        first = true;


        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevKey) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                if(intital) {
                    recView.clearOnScrollListeners();
                    records.clear();
                    electricity.clear();
                    time.clear();
                    //Log.d("I happened", "I happened");
                    /*String dateStr = "04/05/2010";

                    SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateObj = null;
                    try {
                        dateObj = curFormater.parse(dateStr);
                    } catch (java.text.ParseException e) {
                        Log.d("Status: ", e.getMessage());
                    }
                    SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");

                    String newDateStr = postFormater.format(dateObj);

                    DisplayRecords d = new DisplayRecords(newDateStr, electricity, time, starterVal.get(i));

                    records.add(d);


                    Log.d("My status", starterVal.get(i) + "");

                    //clear a bunch of stuff

                    time.clear();
                    electricity.clear();*/
                    //}


                    //Log.d("")

                    //for(int i = 0; i < starterVal.size() - 1; i++){

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //Log.d("CheckCounter size", checkCounter + "");

                        if ((postSnapshot.child("b").getValue().toString().equals("0") && tally > 0) || tally == checkCounter - 1/*tally >= starterVal.get(i) && tally < starterVal.get(i + 1)*/) {


                            String dateStr = "04/05/2010";

                            SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
                            Date dateObj = null;
                            try {
                                dateObj = curFormater.parse(dateStr);
                            } catch (java.text.ParseException e) {
                                Log.d("Status: ", e.getMessage());
                            }
                            SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");

                            String newDateStr = postFormater.format(dateObj);

                            DisplayRecords d = new DisplayRecords(date, new ArrayList<Double>(electricity), new ArrayList<Double>(time), 0, starterVal, budget);

                            records.add(new DisplayRecords(date, new ArrayList<Double>(electricity), new ArrayList<Double>(time), 0, starterVal, budget));


                            //Log.d("final value", electricity + "");
                            time.clear();
                            electricity.clear();

                            if(tally == checkCounter - 1){
                                tally = 0;
                            }

                        }
                        date = postSnapshot.child("ts").getValue().toString().substring(0, postSnapshot.child("ts").getValue().toString().indexOf("T"));
                        Double budgetDoub = Double.parseDouble(postSnapshot.child("b").getValue().toString());
                        Long longBudgetDoub = Math.round(budgetDoub*1);
                        budget = longBudgetDoub + "";
                        //Log.d("Desc: ", postSnapshot.child("b").getValue().toString());
                        String localValElec = postSnapshot.child("b").getValue().toString();

                        Double localDoubleElec = Double.parseDouble(localValElec);
                        electricity.add(localDoubleElec);

                        String localValTime = postSnapshot.child("a").getValue().toString();
                        Double localDoubleTime = Double.parseDouble(localValTime);
                        if (first) {
                            initialTime = localDoubleTime;
                            first = false;
                        }
                        time.add(localDoubleTime - initialTime);
                        //}
                        tally++;

                    }
                    //tally = 0;
                    for(int r = 0; r < records.size()/2; r++){

                        Collections.swap(records, r, records.size() - 1 - r);
                    }
                    ArrayList<DisplayRecords> finalRecords = new ArrayList<>();
                    for(int i = 0; i < records.size(); i++){
                        finalRecords.add(records.get(i));
                        if(i == 2){
                            break;
                        }
                    }
                    //}
                    //records.clear();
                    for (int i = 0; i < records.size(); i++) {
                        Log.d("Record val: " + records.get(i).date, records.get(i).electricityBudget.toString());
                    }

                    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    recView.setLayoutManager(llm);
                    recView.setAdapter(new MyAdapterRecords(finalRecords));
                    intital = false;

                    //records.clear();
                    electricity.clear();
                    time.clear();
                }



                    //Log.d("My status", starterVal.get(i) + "");

                    //clear a bunch of stuff


                //}



                //records.clear();
            }






        });

        /*recView.setAdapter(new MyAdapterRecords(records));
        //}
        //records.clear();
        for (int i = 0; i < records.size(); i++) {
            Log.d("Record val: " + records.get(i).date, records.get(i).electricityBudget.toString());
        }

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(llm);
        intital = false;

        //records.clear();
        electricity.clear();
        time.clear();*/
        /*recView.setAdapter(new MyAdapterRecords(records));
        //}


        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(llm);*/

        return v;
    }


}
