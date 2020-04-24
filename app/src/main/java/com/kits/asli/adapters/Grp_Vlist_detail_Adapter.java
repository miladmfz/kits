package com.kits.asli.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.asli.R;
import com.kits.asli.activity.GrpActivity;
import com.kits.asli.model.DatabaseHelper;
import com.kits.asli.model.GoodGroup;

import java.util.ArrayList;

public class Grp_Vlist_detail_Adapter extends RecyclerView.Adapter<Grp_Vlist_detail_Adapter.GoodGroupViewHolder> {

    private Context mContext;
    private ArrayList<GoodGroup> GoodGroups;
    private Intent intent;
    private DatabaseHelper dbh;

    public Grp_Vlist_detail_Adapter(ArrayList<GoodGroup> GoodGroups, Context mContext) {
        this.mContext = mContext;
        this.GoodGroups = GoodGroups;
        this.dbh = new DatabaseHelper(mContext);

    }

    @NonNull
    @Override
    public GoodGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grp_v_list_detail, parent, false);
        return new GoodGroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GoodGroupViewHolder holder, int position) {
        final GoodGroup GoodGroupView = GoodGroups.get(position);


        holder.grpname.setText(GoodGroupView.getName());
        Log.e("id", "" + GoodGroupView.getGoodGroupCode());
        Log.e("id", "" + GoodGroupView.getName());


        if (dbh.getAllGroups("", GoodGroupView.getGoodGroupCode()).size() == 0) {
            holder.grpname.setIconSize(1);

        } else
            holder.grpname.setIconSize(50);


        holder.grpname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(mContext, GrpActivity.class);
                intent.putExtra("id", GoodGroupView.getGoodGroupCode());
                intent.putExtra("title", GoodGroupView.getName());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return GoodGroups.size();
    }

    class GoodGroupViewHolder extends RecyclerView.ViewHolder {

        private MaterialButton grpname;
        MaterialCardView rltv;

        GoodGroupViewHolder(View itemView) {
            super(itemView);
            grpname = itemView.findViewById(R.id.grp_vlist_detail_name);
            rltv = itemView.findViewById(R.id.grp_vlist_detail);
        }
    }
}
