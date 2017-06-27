package com.sravan.and.beintouch.ui;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sravan.and.beintouch.adapters.DetailContactHistoryAdapter;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.bean.CallEntry;
import com.sravan.and.beintouch.tasks.RetrieveCallLogsforSelectedContact;
import com.sravan.and.beintouch.utility.Utilities;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;


public class ContactDetailFragment extends Fragment {

    BeInTouchContact beInTouchContact;
    ArrayList<CallEntry> callEntries = new ArrayList<CallEntry>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView contactDetailEmptyView;
    DetailContactHistoryAdapter detailContactHistoryAdapter;


    public ContactDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                contactDetailEmptyView = (TextView) rootView.findViewById(R.id.contacts_detail_empty_textview);
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.contact_detail_recyclerview);
                layoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                if(Utilities.checkPermission(getContext())){
                    RetrieveCallLogsforSelectedContact retrieveCallLogsforSelectedContact = new RetrieveCallLogsforSelectedContact(this);
                    retrieveCallLogsforSelectedContact.execute(beInTouchContact);
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
                        Utilities.logFirebaseEvent(getContext(), getResources().getString(R.string.call_contact_event));
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

    public void onCall(ArrayList<CallEntry> callEntries) {

        detailContactHistoryAdapter = new DetailContactHistoryAdapter(getContext(),
                callEntries, beInTouchContact.getName());
        mRecyclerView.setAdapter(detailContactHistoryAdapter);

        if(detailContactHistoryAdapter.getItemCount() == 0){
            contactDetailEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            contactDetailEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
