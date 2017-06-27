package com.sravan.and.beintouch.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.bean.CallEntry;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

/**
 * Created by HP on 6/22/2017.
 */


public class DetailContactHistoryAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<CallEntry> callEntries;
    String contactName;
    private static long incomingCalls;
    private static long outgoingCalls;
    private static final String YOU = "You";
    private static final String CALLINITIATION_PIECHART_DESC= "Call Initiation";

    public static final int[] MATERIAL_COLORS = {
            rgb("#0288D1"), rgb("#64C2F4")};

    private static final int GRAPH = 1;
    private static final int HISTORY = 2;


    public DetailContactHistoryAdapter(Context context, ArrayList<CallEntry> callEntries, String contactName) {
        this.context = context;
        this.callEntries = callEntries;
        this.contactName = contactName;
        incomingCalls = 0;
        outgoingCalls = 0;
    }

    public ArrayList<CallEntry> getCallEntries() {
        return callEntries;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View itemView;
        switch (viewType){
            case GRAPH:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contacts_list_graph, parent, false);
                vh = new DetailContactHistoryAdapter.ViewHolderGraph(itemView);
                break;
            case HISTORY:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contacts_list_item, parent, false);
                vh = new DetailContactHistoryAdapter.ViewHolderHistory(itemView);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case GRAPH:
                ViewHolderGraph viewHolderGraph = (ViewHolderGraph) holder;
                updateIncomingOutgoingCalls(callEntries);
                drawCallInitiationGraph(viewHolderGraph.pieChart);
                break;
            case HISTORY:
                ViewHolderHistory viewHolderHistory = (ViewHolderHistory) holder;
                String name = contactName;
                viewHolderHistory.contactName.setText(name);
                viewHolderHistory.contactLastInteracted.setText(BeInTouchContact.getLastInteractedHistory(callEntries
                        .get(position-1)
                        .getDate()));
                Glide.with(context).load(R.drawable.ic_call_log)
                        .into(viewHolderHistory.imageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (callEntries != null && callEntries.size() > 0){
            return callEntries.size() + 1;
        }
        else {
            return 0;
        }
    }


    public class ViewHolderHistory extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactLastInteracted;
        public ImageView imageView;

        public ViewHolderHistory(View itemView) {
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.contactname);
            contactLastInteracted = (TextView) itemView.findViewById(R.id.contactlastinteraction);
            imageView = (ImageView) itemView.findViewById(R.id.contactavatar);
        }
    }

    public class ViewHolderGraph extends RecyclerView.ViewHolder {
        public PieChart pieChart;

        public ViewHolderGraph(View itemView) {
            super(itemView);
            pieChart = (PieChart) itemView.findViewById(R.id.piechart);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return GRAPH;
        } else {
            return HISTORY;
        }
    }

    private static void updateIncomingOutgoingCalls(ArrayList<CallEntry> callEntries){
        for (CallEntry entry: callEntries ) {
            if(entry.getIncoming()){
                incomingCalls  = incomingCalls + entry.getDuration();
            } else {
                outgoingCalls = outgoingCalls + entry.getDuration();
            }
        }
    }

    private void drawCallInitiationGraph(PieChart pieChart){
        Description des = pieChart.getDescription();
        des.setEnabled(false);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHighlightPerTapEnabled(true);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(incomingCalls, contactName));
        entries.add(new PieEntry(outgoingCalls, YOU));
        PieDataSet set = new PieDataSet(entries, CALLINITIATION_PIECHART_DESC);
        set.setColors(MATERIAL_COLORS);
        PieData data = new PieData(set);
        data.setDrawValues(false);
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(60);
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        pieChart.invalidate();
    }
}