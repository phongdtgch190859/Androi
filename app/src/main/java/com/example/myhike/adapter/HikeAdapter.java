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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myhike.CreateUpdateHikeActivity;
import com.example.myhike.HikeDetailActivity;
import com.example.myhike.MainActivity;
import com.example.myhike.db.DatabaseHelper;
import com.example.myhike.db.entity.HikeData;
import com.example.myhike.R;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeViewHolder> {
    public List<HikeData> hikeDataList;
    private Context context;
    private Activity activity;
    private DatabaseHelper db;

    public HikeAdapter(Context context, List<HikeData> hikeDataList, Activity activity) {
        this.context = context;
        this.hikeDataList = hikeDataList;
        this.activity = activity;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hike_row, parent, false);
        return new HikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HikeViewHolder holder, final int position) {
        HikeData hikeData = hikeDataList.get(position);
        holder.textNameOfHike.setText(hikeData.getNameOfHike());
        holder.textLengthOfHike.setText(String.valueOf(hikeData.getLengthOfHike() + "km"));
        holder.textLevelDifficulty.setText(hikeData.getLevelDifficulty());
        holder.textParkingAvailable.setText(hikeData.isParkingAvailable() ? "Yes" : "No");
        holder.textLocation.setText(hikeData.getLocation());
        holder.textDate.setText(hikeData.getDate());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confimnDelete(hikeData.getId());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    HikeData hikeData = hikeDataList.get(adapterPosition);
                    Intent intent = new Intent(context, HikeDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("hikeId", hikeData.getId());
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, 1);
                }
            }
        });
    }
    void confimnDelete(int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteHike(id);
                Intent intent = new Intent(context, MainActivity.class);
                activity.startActivityForResult(intent, 1);
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
        return hikeDataList.size();
    }

    public class HikeViewHolder extends RecyclerView.ViewHolder {
        public TextView textNameOfHike;
        public TextView textLengthOfHike;
        public TextView textLevelDifficulty;
        public TextView textParkingAvailable;
        public TextView textLocation;
        public TextView textDate;
        public TextView delete;


        public HikeViewHolder(@NonNull View itemView) {
            super(itemView);
            textNameOfHike = itemView.findViewById(R.id.text_hike_name);
            textLengthOfHike = itemView.findViewById(R.id.text_hike_length);
            textLevelDifficulty = itemView.findViewById(R.id.textHikeLevel);
            textParkingAvailable = itemView.findViewById(R.id.textHikeParking);
            textLocation = itemView.findViewById(R.id.text_hike_location);
            textDate = itemView.findViewById(R.id.text_hike_date);
            delete = itemView.findViewById(R.id.delete_hike);
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            itemView.setAnimation(translate_anim);
        }
    }
}
