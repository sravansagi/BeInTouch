package com.sravan.and.beintouch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sravan.and.beintouch.R;
import com.sravan.and.beintouch.bean.BeInTouchContact;
import com.sravan.and.beintouch.bean.CallEntry;

import java.util.ArrayList;

/**
 * Created by HP on 6/22/2017.
 */


public class DetailContactHistoryAdapter extends
        RecyclerView.Adapter<DetailContactHistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<CallEntry> callEntries;
    String contactName;

    public DetailContactHistoryAdapter(Context context, ArrayList<CallEntry> callEntries, String contactName) {
        this.context = context;
        this.callEntries = callEntries;
        this.contactName = contactName;
    }

    public ArrayList<CallEntry> getCallEntries() {
        return callEntries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_list_item, parent, false);
        DetailContactHistoryAdapter.ViewHolder vh = new DetailContactHistoryAdapter.ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = contactName;
        holder.contactName.setText(name);
        holder.contactLastInteracted.setText(BeInTouchContact.getLastInteraction(callEntries
                                .get(position)
                                .getDate()));
        Glide.with(context).load(R.drawable.ic_contact_thumbnail)
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (callEntries != null){
            return callEntries.size();
        }
        else {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView contactLastInteracted;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.contactname);
            contactLastInteracted = (TextView) itemView.findViewById(R.id.contactlastinteraction);
            imageView = (ImageView) itemView.findViewById(R.id.contactavatar);
        }
    }
}