package com.raneshprasad.smartbudget;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterRecords extends RecyclerView.Adapter<MyAdapterRecords.ContactViewHolderTwo> {
    private List<DisplayRecords> contactList;
    private  ArrayList<Integer> numTimes = new ArrayList<>();
    boolean alreadyHappened  = false;

    public MyAdapterRecords(List<DisplayRecords> contactList) {
        this.contactList = contactList;
    }
    @Override
    public int getItemCount(){
        return contactList.size();
    }


    @Override
    public void onBindViewHolder(MyAdapterRecords.ContactViewHolderTwo contactViewHolderResource, int i) {



        /*for(int j = 0; j < numTimes.size(); j++){
            if(i == numTimes.get(j)){
                alreadyHappened = true;
                break;
            }
        }*/


        //if(!alreadyHappened) {
            DisplayRecords ci = contactList.get(i);

            contactViewHolderResource.mainDesc.setText("Date: " + ci.date);
            contactViewHolderResource.budgetText.setText("Budget: $" + ci.budget);
            //contactViewHolderResource.littleDesc.setText(ci.littleDesc);
            //Log.d("Here is the parent", ci.directory);
            //contactViewHolder.desc.setText(ci.description);
            //LineGraphSeries<DataPoint> seriesLocal = new LineGraphSeries<>();
            int counter = 0;
            //int skipArray = 0;


            //DataPoint obj = null;
            ArrayList<DataPoint> de = new ArrayList<>();

            Log.d("Comparison Val (size: " + ci.electricityBudget.size() + "):" + ci.date, ci.electricityBudget.toString());
            //Log.d("Time interval size", ci.timeInterval.size() + "");

            for (int i1 = 0; i1 < ci.electricityBudget.size(); i1++) {
                //double localTime = ci.timeInterval.get(i1);
                double localElec = ci.electricityBudget.get(i1);
                //Log.d("Local Elec", localElec + "");
                de.add(new DataPoint(i1*2, localElec));
                if(i1 == ci.electricityBudget.size() - 1){
                    contactViewHolderResource.timeText.setText("Time: "+(i1*2) + " sec");
                }

            }
            //Log.d("Stuff", " Happened");
            //try {
            DataPoint[] dataPoints = new DataPoint[de.size()];

            for (int i2 = 0; i2 < de.size(); i2++) {
                dataPoints[i2] = de.get(i2);
                Log.d("Data Point: " + ci.date, dataPoints[i2].getX() + "," + dataPoints[i2].getY());
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
            series.resetData(dataPoints);
            contactViewHolderResource.graph.addSeries(series);
            contactViewHolderResource.graph.getViewport().setScalable(true);
            GridLabelRenderer gridLabel = contactViewHolderResource.graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle("Time (sec)");
            gridLabel.setVerticalAxisTitle("Electricity Bill ($)");


        //}catch (java.lang.NullPointerException e){Log.d("Status", "Breh wtf");}
            //Log.d("Array content", dataPoints.length + "");
            for (int c = 0; c < dataPoints.length; c++) {
                //Log.d("Array content", dataPoints[c].getX() + "," + dataPoints[c].getY());
            }
            //de.clear();
            //contactList.clear();

            numTimes.add(i);

            numTimes.clear();

        //}
        //alreadyHappened = false;


    }

    @Override
    public MyAdapterRecords.ContactViewHolderTwo onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_records, viewGroup, false);

        return new MyAdapterRecords.ContactViewHolderTwo(itemView);
    }

    public static class ContactViewHolderTwo extends RecyclerView.ViewHolder{
        TextView mainDesc;
        ArrayList<Double> time;
        ArrayList<Double> energy;
        GraphView graph;
        TextView budgetText;
        TextView timeText;

        LineGraphSeries<DataPoint> series;
        DataPoint[] val;
        public ContactViewHolderTwo(View v){
            super(v);
            mainDesc = (TextView) v.findViewById(R.id.textViewDateRecords);
            budgetText = (TextView) v. findViewById(R.id.textViewBudget);
            timeText = v.findViewById(R.id.textViewTime);
            //littleDesc = (TextView) v.findViewById(R.id.textView_itemLittleDesc);

            graph = (GraphView) v.findViewById(R.id.graph);


            //series = new LineGraphSeries<DataPoint>();

            //graph.addSeries(series);
        }
    }
}

