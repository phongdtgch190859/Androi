package com.example.myhike;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.Toast;

import com.example.myhike.db.DatabaseHelper;

import com.example.myhike.db.entity.HikeData;
import com.example.myhike.db.entity.ObservationData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateUpdateObservationActivity extends AppCompatActivity {
    TextInputEditText date, comment;
    AutoCompleteTextView type;
    int hikeId =-1;
    int obId = -1;
    private DatePickerDialog.OnDateSetListener dateListener;
    TextInputLayout typeLayout, dateLayout;
    Calendar calendar;
    Button save;
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_observation);
        setting();
    }
    void setting(){
        type = findViewById(R.id.typeEditText);
        date = findViewById(R.id.dateObEditText);
        comment = findViewById(R.id.commentEditText);

        typeLayout = findViewById(R.id.typeContainer);
        dateLayout = findViewById(R.id.dateObContainer);

        save = findViewById(R.id.submitObButton);

        textFocusListener(date, dateLayout, null);
        textFocusListener(null, typeLayout, type);

        db = new DatabaseHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hikeId = extras.getInt("hikeId");
            obId = extras.getInt("obId");
        }
        setDatePicker();
        setDropDown();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditOb();
            }
        });
        if (obId!= -1) {
            ObservationData ob = db.getObservation(obId);
            date.setText(ob.getDate());
            type.setText(ob.getType());
            setDropDown();
            comment.setText(ob.getComment());
        }
    }
    void setDatePicker(){
        calendar = Calendar.getInstance();
        date.setOnClickListener(view -> {
            new DatePickerDialog(this, dateListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        dateListener = (datePicker, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
            date.setText(dateFormat.format(calendar.getTime()));
        };
    }
    void setDropDown(){
        List<String> obTypes = new ArrayList<>();
        obTypes.add("Flora");
        obTypes.add("Fauna");
        obTypes.add("Trail Conditions");
        obTypes.add("Weather Conditions");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_row, obTypes);
        type.setAdapter(adapter);
        type.setOnClickListener(view -> type.showDropDown());
    }
    private void addAndEditOb() {
        if(hikeId != -1){
            ObservationData ob = createObObject();
            if (areFieldsValid())
                if (obId != -1) {
                    UpdateOb(ob);
                } else {
                    CreateOb(ob);
                }
            else {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            }
        }
        else Toast.makeText(this, "Not found hike.", Toast.LENGTH_SHORT).show();
    }

    private ObservationData createObObject() {
        return new ObservationData(-1,
                type.getText().toString(),
                date.getText().toString(),
                comment.getText().toString(),
                hikeId);
    }

    private void UpdateOb(ObservationData ob) {
        db.updateObservation(obId, ob);
        Intent intent = new Intent(this, HikeDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("hikeId", hikeId);
        intent.putExtras(bundle);
        startActivity(intent);
        Toast.makeText(this, "Hike " + type.getText().toString() + " updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void CreateOb(ObservationData ob) {
        db.insertObservation(ob);
        Toast.makeText(this, "Observation " + type.getText().toString() + " added", Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        bundle.putInt("hikeId", hikeId);
        Intent intent = new Intent(this, HikeDetailActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
        finish();
    }

    private void textFocusListener(TextInputEditText editText, TextInputLayout layout, AutoCompleteTextView autoText) {

        if (autoText != null) {
            autoText.setOnItemClickListener((adapterView, view, i, l) -> {
                layout.setHelperText(validInputAutoText(autoText));
            });
        }
        if (editText == date) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    layout.setHelperText(isDateValid(s.toString()) ? null : "required");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }
    private boolean areFieldsValid() {

        boolean isDateValid = isDateValid(date.getText().toString());

        boolean isLevelValid = validInputAutoText(type) == null;

        return isDateValid && isLevelValid;
    }


    private String validInputAutoText(AutoCompleteTextView autoText) {
        String check = autoText.getText().toString();
        if (check.isEmpty()) {
            return "     required";
        }
        return null;
    }
    private boolean isDateValid(String dateValue) {
        return !dateValue.isEmpty();
    }
    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, HikeDetailActivity.class);
        bundle.putInt("hikeId", hikeId);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }
}