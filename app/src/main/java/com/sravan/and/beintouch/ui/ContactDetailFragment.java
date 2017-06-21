package com.sravan.and.beintouch.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.bean.CallEntry;
import com.sravan.and.beintouch.utility.Utilities;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;


public class ContactDetailFragment extends Fragment {

    BeInTouchContact beInTouchContact;

    private static final String YOU = "You";
    private static final String CALLINITIATION_PIECHART_DESC= "Call Initiation";

    long incomingDuration = 0;
    long outgoingDuration = 0;

    ArrayList<CallEntry> callEntries = new ArrayList<CallEntry>();
    public static final int[] MATERIAL_COLORS = {
            rgb("#03A9F4"), rgb("#ff64c2f4")};

    private static final String[] CALLLOG_CONTACT_PROJECTION = {CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION};

    private static final String SELECTION_CALLLOG_CONTACT = CallLog.Calls.NUMBER + " LIKE ?";

    public ContactDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_contact_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getActivity().getIntent();
        if (intent!= null){
            beInTouchContact = intent.getParcelableExtra(Intent.EXTRA_TEXT);
            if(beInTouchContact != null){
                CollapsingToolbarLayout collapsingToolbar =
                        (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
                collapsingToolbar.setTitle(beInTouchContact.getName());
                boolean isRightToLeft = getResources().getBoolean(R.bool.is_right_to_left);

                if (isRightToLeft){
                    collapsingToolbar.setExpandedTitleGravity(Gravity.RIGHT | Gravity.BOTTOM);
                    collapsingToolbar.setCollapsedTitleGravity(Gravity.RIGHT);
                }

                ImageView contactPhotoView = (ImageView) rootView.findViewById(R.id.backdrop);
                if((beInTouchContact.getPhotoID()!= null && beInTouchContact.getPhotoID().length() > 0)){
                    Glide.with(getContext())
                            .load(beInTouchContact.getPhotoID())
                            .into(contactPhotoView);
                } else if(beInTouchContact.getContactThumbnailPhotoID()!= null && beInTouchContact.getContactThumbnailPhotoID().length() > 0){
                    Glide.with(getContext())
                            .load(beInTouchContact.getContactThumbnailPhotoID())
                            .into(contactPhotoView);
                }

                PieChart pieChart = (PieChart) rootView.findViewById(R.id.piechart);
                TextView contactDetailEmptyView = (TextView) rootView.findViewById(R.id.contacts_detail_empty_textview);

                if(Utilities.checkPermission(getContext())){
                    this.processCallLogData();
                    if(incomingDuration > 0 || outgoingDuration > 0){
                        contactDetailEmptyView.setVisibility(View.GONE);
                        pieChart.setVisibility(View.VISIBLE);
                        this.drawCallInitiationGraph(pieChart);
                    } else {
                        contactDetailEmptyView.setVisibility(View.VISIBLE);
                        pieChart.setVisibility(View.GONE);
                    }

                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beInTouchContact.getPhoneNumber() != null){
                    Intent callContactIntent =  new Intent(Intent.ACTION_VIEW);
                    PackageManager packageManager = getContext().getPackageManager();
                    if (callContactIntent.resolveActivity(packageManager) != null) {
                        callContactIntent.setData(Uri.parse("tel:"+ beInTouchContact.getPhoneNumber()));
                        startActivity(callContactIntent);
                    } else {
                        Toast.makeText(getContext(),"There is not application to make a phone call",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(),"There is some problem in opening the selected contact. Please try again",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    @SuppressWarnings("MissingPermission")
    private void processCallLogData() {
        String phoneNumberwithoutSpaces = beInTouchContact.getPhoneNumber().replaceAll(" ", "");
        String phoneNumberwithoutEncoding = phoneNumberwithoutSpaces.replace("\u202A", "").replace("\u202C", "");
        Cursor callLogofContact = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                CALLLOG_CONTACT_PROJECTION,
                SELECTION_CALLLOG_CONTACT,
                new String[]{"%" + phoneNumberwithoutEncoding +"%"},
                CallLog.Calls.DEFAULT_SORT_ORDER);
        for (callLogofContact.moveToFirst(); !callLogofContact.isAfterLast(); callLogofContact.moveToNext()) {
            CallEntry callEntry = new CallEntry();
            callEntry.setIncoming(callLogofContact.getInt(2));
            callEntry.setDate(callLogofContact.getLong(3));
            callEntry.setDuration(callLogofContact.getLong(4));
            callEntries.add(callEntry);
        }

        for (CallEntry entry:callEntries) {
            if(entry.getIncoming()){
                incomingDuration = incomingDuration + entry.getDuration();
            } else {
                outgoingDuration = outgoingDuration + entry.getDuration();
            }
        }
    }


    /**
     * The drawCallInitiationGraph method creates a pie chart showing the incoming and outgoing calls of a selected contact
     *
     * @param pieChart
     */
    private void drawCallInitiationGraph(PieChart pieChart){


        Description des = pieChart.getDescription();
        des.setEnabled(false);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHighlightPerTapEnabled(true);

        // Creating datas for the pie chart

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(incomingDuration, beInTouchContact.getName()));
        entries.add(new PieEntry(outgoingDuration, YOU));
        PieDataSet set = new PieDataSet(entries, CALLINITIATION_PIECHART_DESC);
        set.setColors(ColorTemplate.MATERIAL_COLORS);


        PieData data = new PieData(set);
        data.setDrawValues(false);
        pieChart.setData(data);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(60);

        // Updating the pirchart legend

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
