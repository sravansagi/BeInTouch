package com.sravan.and.beintouch.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.adapters.DetailContactHistoryAdapter;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.bean.CallEntry;
import com.sravan.and.beintouch.tasks.RetrieveCallLogsforSelectedContact;
import com.sravan.and.beintouch.utility.Utilities;

import java.util.ArrayList;

import timber.log.Timber;


public class ContactDetailFragment extends Fragment implements RetrieveCallLogsforSelectedContact.FragmentCallback {

    BeInTouchContact beInTouchContact;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<CallEntry> callEntries;
    TextView contactDetailEmptyView;
    DetailContactHistoryAdapter detailContactHistoryAdapter;
    private Parcelable mLayoutManagerSavedState;

    public ContactDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("CONTACTDETAILS")) {
                callEntries = savedInstanceState.getParcelableArrayList("CONTACTDETAILS");
            }
            if (savedInstanceState.containsKey("DETAILLAYOUTPOSITION")) {
                mLayoutManagerSavedState = savedInstanceState.getParcelable("DETAILLAYOUTPOSITION");
            }

        }
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            beInTouchContact = intent.getParcelableExtra(Intent.EXTRA_TEXT);
            if (beInTouchContact != null) {
                CollapsingToolbarLayout collapsingToolbar =
                        (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
                collapsingToolbar.setTitle(beInTouchContact.getName());
                boolean isRightToLeft = getResources().getBoolean(R.bool.is_right_to_left);

                if (isRightToLeft) {
                    collapsingToolbar.setExpandedTitleGravity(Gravity.RIGHT | Gravity.BOTTOM);
                    collapsingToolbar.setCollapsedTitleGravity(Gravity.RIGHT);
                }

                ImageView contactPhotoView = (ImageView) rootView.findViewById(R.id.backdrop);
                if ((beInTouchContact.getPhotoID() != null && beInTouchContact.getPhotoID().length() > 0)) {
                    Glide.with(getContext())
                            .load(beInTouchContact.getPhotoID())
                            .into(contactPhotoView);
                } else if (beInTouchContact.getContactThumbnailPhotoID() != null && beInTouchContact.getContactThumbnailPhotoID().length() > 0) {
                    Glide.with(getContext())
                            .load(beInTouchContact.getContactThumbnailPhotoID())
                            .into(contactPhotoView);
                }
                contactDetailEmptyView = (TextView) rootView.findViewById(R.id.contacts_detail_empty_textview);
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.contact_detail_recyclerview);
                layoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                if (callEntries != null && callEntries.size() > 0) {
                    onTaskDone(callEntries);
                } else {
                    if (Utilities.checkPermission(getContext())) {
                        RetrieveCallLogsforSelectedContact retrieveCallLogsforSelectedContact = new RetrieveCallLogsforSelectedContact(getContext(), this);
                        retrieveCallLogsforSelectedContact.execute(beInTouchContact);
                    }
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setContentDescription("Call " + beInTouchContact.getName());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beInTouchContact.getPhoneNumber() != null) {
                    Intent callContactIntent = new Intent(Intent.ACTION_VIEW);
                    PackageManager packageManager = getContext().getPackageManager();
                    if (callContactIntent.resolveActivity(packageManager) != null) {
                        Utilities.logFirebaseEvent(getContext(), getResources().getString(R.string.call_contact_event));
                        callContactIntent.setData(Uri.parse("tel:" + beInTouchContact.getPhoneNumber()));
                        startActivity(callContactIntent);
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.no_application_to_make_phone_call),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.problem_opening_selected_contact),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onTaskDone(ArrayList<CallEntry> callEntries) {
        {
            detailContactHistoryAdapter = new DetailContactHistoryAdapter(getContext(),
                    callEntries, beInTouchContact.getName());
            mRecyclerView.setAdapter(detailContactHistoryAdapter);

            if (detailContactHistoryAdapter.getItemCount() == 0) {
                contactDetailEmptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                contactDetailEmptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecyclerView.getVisibility() == View.VISIBLE
                && mLayoutManagerSavedState != null
                && layoutManager != null) {
            layoutManager.onRestoreInstanceState(mLayoutManagerSavedState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mRecyclerView != null && detailContactHistoryAdapter != null && detailContactHistoryAdapter.getItemCount() > 0) {
            outState.putParcelableArrayList("CONTACTDETAILS", detailContactHistoryAdapter.getCallEntries());
            outState.putParcelable("DETAILLAYOUTPOSITION", mRecyclerView.getLayoutManager().onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }
}
