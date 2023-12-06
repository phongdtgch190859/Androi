package com.example.myhike;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myhike.adapter.HikeAdapter;
import com.example.myhike.db.DatabaseHelper;
import com.example.myhike.db.entity.HikeData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HikeAdapter hikeAdapter;
    private List<HikeData> hikeDataList = new ArrayList<>();
    private ArrayList<HikeData> filteredList = new ArrayList<>();
    TextInputEditText search;
    private DatabaseHelper db;
    ImageView empty_imageview, sort;
    TextView no_data;
    String action = "ASC";
    Button addHike;
    int hikeId = -1;

    boolean needToUpdateList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.HikeListRecylerView);
        addHike = findViewById(R.id.btnAddHike);
        search = findViewById(R.id.textSearch);
        sort = findViewById(R.id.sort);
        empty_imageview = findViewById(R.id.empty_imageview);
        no_data = findViewById(R.id.no_data);
        db = new DatabaseHelper(this);

        hikeAdapter = new HikeAdapter(this, filteredList, this);

        updateHikeList();

        addHike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("hikeId", -1);
                Intent intent = new Intent(MainActivity.this, CreateUpdateHikeActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hikeAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSortOrder();
                updateHikeList();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterHikeData(editable.toString().trim());
            }
        });
    }

    private void toggleSortOrder() {
        if ("ASC".equals(action)) {
            action = "DESC";
        } else {
            action = "ASC";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            needToUpdateList = true;
        }
    }

    private void updateHikeList() {
        if(needToUpdateList) {
            hikeDataList.clear();
            hikeDataList.addAll(db.getALLHikes(action));
            hikeAdapter.notifyDataSetChanged();
        }
        hikeDataList.clear();
        hikeDataList.addAll(db.getALLHikes(action));
        hikeAdapter.notifyDataSetChanged();
        filterHikeData(search.getText().toString());
        checkIfDataIsEmpty();
    }

    private void checkIfDataIsEmpty() {
        if (filteredList.isEmpty()) {
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterHikeData(String query) {
        filteredList.clear();
        for (HikeData hike : hikeDataList) {
            if (hike.getNameOfHike().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(hike);
            }
        }
        hikeAdapter.notifyDataSetChanged();
        checkIfDataIsEmpty();
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all Data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.refreshDatabase();
                needToUpdateList = true;
                updateHikeList();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }
}

