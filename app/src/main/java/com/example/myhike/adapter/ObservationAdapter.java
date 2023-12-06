package com.example.myhike.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhike.CreateUpdateHikeActivity;
import com.example.myhike.CreateUpdateObservationActivity;
import com.example.myhike.HikeDetailActivity;
import com.example.myhike.MainActivity;
import com.example.myhike.R;
import com.example.myhike.db.DatabaseHelper;
import com.example.myhike.db.entity.HikeData;
import com.example.myhike.db.entity.ObservationData;

import java.util.ArrayList;
import java.util.List;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {
    private ArrayList<ObservationData> observationDataList;
    private Context context;
    private Activity activity;
    DatabaseHelper db;

    public ObservationAdapter(Context context,  ArrayList<ObservationData> observationDataList, Activity activity) {
        this.context = context;
        this.observationDataList = observationDataList;
        this.activity = activity;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ObservationAdapter.ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.observation_row, parent, false);
        return new ObservationAdapter.ObservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationAdapter.ObservationViewHolder holder, final int position) {
        ObservationData data= observationDataList.get(position);
        holder.type.setText(data.getType());
        holder.date.setText(data.getDate());
        holder.comment.setText(data.getComment());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("obId", data.getId());
                bundle.putInt("hikeId", data.getHike_id());
                Intent intent = new Intent(context, CreateUpdateObservationActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, 3);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confimnDelete(data.getId(), data.getHike_id());


            }
        });

    }
    void confimnDelete(int id, int hikeId){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(context, HikeDetailActivity.class);
                bundle.putInt("obId", -1);
                bundle.putInt("hikeId", hikeId);
                intent.putExtras(bundle);
                db.deleteObservation(id);
                activity.startActivityForResult(intent, 2);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return observationDataList.size();
    }

    public class ObservationViewHolder extends RecyclerView.ViewHolder {
        public TextView type;
        public TextView comment;
        public TextView date;
        public ImageView edit;
        public ImageView delete;


        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.textViewType);
            date = itemView.findViewById(R.id.textViewDataObservation);
            comment = itemView.findViewById(R.id.textViewComment);
            edit = itemView.findViewById(R.id.ic_edit);
            delete = itemView.findViewById(R.id.ic_delete);
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            itemView.setAnimation(translate_anim);
        }
    }
}
