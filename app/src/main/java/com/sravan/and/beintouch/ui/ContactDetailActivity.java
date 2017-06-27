package com.sravan.and.beintouch.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sravan.and.beintouch.R;

public class ContactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        /*
        The below is to over come the calling of fragment onCreateView two times
        https://stackoverflow.com/a/19100627/4471346
         */
        if(savedInstanceState != null)
            return;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contact_detail_activity,new ContactDetailFragment())
                .commit();
        }
}
