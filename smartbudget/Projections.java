package com.raneshprasad.smartbudget;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Projections extends Fragment {

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    public Projections() {
        // Required empty public constructor
    }
    int counter = 0;
    double amount = 0;
    TextView disp;
    TextView amountText;
    EditText acceptAmount;
    Button input;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_projections, container, false);

        disp = v.findViewById(R.id.textViewDisplay);
        amountText = v.findViewById(R.id.textViewCalcAmount);
        acceptAmount = v.findViewById(R.id.editTextAmountPrediction);
        input = v.findViewById(R.id.buttonPredict);

        amountText.setText("");
        disp.setText("");

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //first = true;
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            String amountVal = postSnapshot.child("b").getValue().toString();
                            Double amountValDub = Double.parseDouble(amountVal);

                            if(amountVal.equals("0") ){
                                //amount = amountValDub;
                                //break;
                                counter = 0;
                            }

                            counter+=2;


                            amount = amountValDub;

                            Log.d("Amounts are", "$ " + amount);
                        }

                        String inputAmt = acceptAmount.getText().toString();
                        Double acceptDob = Double.parseDouble(inputAmt);

                        disp.setText("The Time Duration is Approximately");

                        Double report = (acceptDob*(counter/amount));

                        if(report < 60){

                            if(Math.round(report*1) == 1){
                                amountText.setText(Math.round(report*1) + " second");
                            }else{
                                amountText.setText(Math.round(report*1) + " seconds");
                            }

                        }

                        if(report >= 60 && report < 3600){
                            if(Math.round((report/60) * 1) == 1){
                                amountText.setText(Math.round((report / 60) * 1) + " minute");
                            }else {
                                amountText.setText(Math.round((report / 60) * 1) + " minutes");
                            }
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
            }
        });









        return v;
    }


}
