package com.example.myhike;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myhike.db.DatabaseHelper;
import com.example.myhike.db.entity.HikeData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CreateUpdateHikeActivity extends AppCompatActivity {
    TextInputEditText name, location, date, length, description;
    CheckBox parking_available;
    AutoCompleteTextView level_difficulty;
    int hikeId;
    TextView itemLevel;
    private DatePickerDialog.OnDateSetListener dateListener;
    TextInputLayout nameLayout, lengthLayout, locationLayout, dateLayout, levelLayout;
    Calendar calendar;
    Button save;
    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_hike);

        setting();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hikeId = extras.getInt("hikeId", -1);
        }

        Log.i("ok", "Check hikeId: " + hikeId);
        if (hikeId != -1) {
            HikeData hike = db.getHike(hikeId);
            name.setText(hike.getNameOfHike());
            date.setText(hike.getDate());
            location.setText(hike.getLocation());
            length.setText(String.valueOf(hike.getLengthOfHike()));
            level_difficulty.setText(hike.getLevelDifficulty());
            setDropDown();
            parking_available.setChecked(hike.isParkingAvailable());
            description.setText(hike.getDescription());
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditHikes();
            }
        });
    }

    private void setting() {
        name = findViewById(R.id.nameEditText);
        location = findViewById(R.id.locationEditText);
        date = findViewById(R.id.dateEditText);
        length = findViewById(R.id.lengthEditText);
        description = findViewById(R.id.descriptionEditText);
        parking_available = findViewById(R.id.checkBoxParking);
        save = findViewById(R.id.submitButton);
        level_difficulty = findViewById(R.id.levelEditText);
        itemLevel = findViewById(R.id.levelTextView);

        nameLayout = findViewById(R.id.nameContainer);
        lengthLayout = findViewById(R.id.lengthContainer);
        dateLayout = findViewById(R.id.dateContainer);
        locationLayout = findViewById(R.id.locationContainer);
        levelLayout = findViewById(R.id.levelContainer);

        textFocusListener(name, nameLayout, null);
        textFocusListener(date, dateLayout, null);
        textFocusListener(location, locationLayout, null);
        textFocusListener(length, lengthLayout, null);
        textFocusListener(null, levelLayout, level_difficulty);


        // Setup datePicker
        setDatePicker();

        setDropDown();
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
        List<String> levelTypes = new ArrayList<>();
        levelTypes.add("Easy");
        levelTypes.add("Intermediate");
        levelTypes.add("Difficult");
        db = new DatabaseHelper(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_row, levelTypes);
        level_difficulty.setAdapter(adapter);

        level_difficulty.setOnClickListener(view -> level_difficulty.showDropDown());
    }
    private void addAndEditHikes() {
        if (areFieldsValid()) {
            HikeData hike = createHikeObject();

            if (hikeId != -1) {
                UpdateHike(hike);
            } else {
                CreateHike(hike);
            }
        }
        else {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private HikeData createHikeObject() {
        return new HikeData(hikeId,
                name.getText().toString(),
                Integer.parseInt(length.getText().toString()),
                level_difficulty.getText().toString(),
                parking_available.isChecked() ? 1 : 0,
                location.getText().toString(),
                date.getText().toString(),
                description.getText().toString());
    }

    private void UpdateHike(HikeData hike) {
        db.updateHike(hikeId, hike);
        Intent intent = new Intent(CreateUpdateHikeActivity.this, HikeDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("hikeId", hikeId);
        intent.putExtras(bundle);
        startActivity(intent);
        Toast.makeText(this, "Hike " + name.getText().toString() + " updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void CreateHike(HikeData hike) {
        db.insertHike(hike);
        Toast.makeText(this, "Hike " + name.getText().toString() + " added", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void textFocusListener(TextInputEditText editText, TextInputLayout layout, AutoCompleteTextView autoText) {

        if(editText != null){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    layout.setHelperText(validInputEditText(editText));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
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
        boolean isNameValid = validInputEditText(name) == null;
        boolean isDateValid = isDateValid(date.getText().toString());
        boolean isLocationValid = validInputEditText(location) == null;
        boolean isLengthValid = validInputEditText(length) == null;
        boolean isLevelValid = validInputAutoText(level_difficulty) == null;

        return isNameValid && isDateValid && isLocationValid && isLengthValid && isLevelValid;
    }

    private String validInputEditText(TextInputEditText editText) {
        String check = editText.getText().toString();
        if (check.isEmpty()) {
            return "required";
        }
        return null;
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
