package com.sravan.and.beintouch.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.bean.BeInTouchContact;

import timber.log.Timber;


public class ContactDetailFragment extends Fragment {

    BeInTouchContact beInTouchContact;

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
        return rootView;
    }
}
