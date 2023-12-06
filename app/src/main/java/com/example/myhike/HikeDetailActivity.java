package com.example.myhike;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myhike.adapter.ObservationAdapter;
import com.example.myhike.db.DatabaseHelper;
import com.example.myhike.db.entity.HikeData;
import com.example.myhike.db.entity.ObservationData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HikeDetailActivity extends AppCompatActivity {

    TextView name, date, description, parking_available, level, length, location, no_data, obCount;
    ArrayList<ObservationData> observations = new ArrayList<>();

    FloatingActionButton btnAdd;
    RecyclerView rcvObservation;
    ObservationAdapter adapter;
    ImageView editHike,  empty_imageview;
    int hikeId = -1;
    boolean needToUpdateList = false;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_detail);
        setting();
    }
    public void setting(){
        name = findViewById(R.id.textViewName);
        date = findViewById(R.id.textViewDate);
        description = findViewById(R.id.textViewDescription);
        parking_available = findViewById(R.id.textViewParking);
        level = findViewById(R.id.textViewLevel);
        length = findViewById(R.id.textViewLength);
        location = findViewById(R.id.textViewLocation);
        editHike = findViewById(R.id.editHike);
        btnAdd = findViewById(R.id.fabAddOb);
        obCount = findViewById(R.id.textViewOb);
        empty_imageview = findViewById(R.id.empty_imageview_ob);
        no_data = findViewById(R.id.no_ob);
        db = new DatabaseHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hikeId = extras.getInt("hikeId", -1);
        }

        HikeData hike = null;
        Log.i("avsa", "HikeDetail: " +hikeId);

        if(hikeId != -1){

            hike = db.getHike(hikeId);
            name.setText(hike.getNameOfHike());
            date.setText(hike.getDate());
            location.setText(hike.getLocation());
            length.setText(String.valueOf(hike.getLengthOfHike()) + "km");
            level.setText(hike.getLevelDifficulty());
            parking_available.setText(hike.isParkingAvailable() ? "Yes": "No");
            description.setText(hike.getDescription());
            rcvObservation = findViewById(R.id.recyclerViewObservation);
            obCount.setText(String.valueOf(observations.size()));


            updateObList();
            adapter = new ObservationAdapter(this, observations, this);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            rcvObservation.setLayoutManager(layoutManager);
            rcvObservation.setItemAnimator(new DefaultItemAnimator());
            rcvObservation.setAdapter(adapter);

            editHike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HikeDetailActivity.this, CreateUpdateHikeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("hikeId", hikeId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("obId", -1);
                bundle.putInt("hikeId", hikeId);
                Intent intent = new Intent(HikeDetailActivity.this, CreateUpdateObservationActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            needToUpdateList = true;
        }
    }
    private void updateObList() {
        observations.clear();
        observations.addAll(db.getAllObservationsByHikeId(hikeId));
        obCount.setText(String.valueOf(observations.size()));
        if (needToUpdateList) {
            adapter.notifyDataSetChanged();
            needToUpdateList = false;
        }

        if (observations.size() == 0) {
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 1);
        finish();
    }
}