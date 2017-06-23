package com.sravan.and.beintouch.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sravan.and.beintouch.R;

public class ContactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contact_detail_activity,new ContactDetailFragment())
                .commit();
        }
}
