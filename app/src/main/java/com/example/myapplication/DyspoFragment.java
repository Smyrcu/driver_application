package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static com.example.myapplication.R.drawable.ic_baseline_error;
import static java.util.Calendar.*;
import static java.util.Calendar.DAY_OF_MONTH;

public class DyspoFragment extends Fragment {
    EditText DateText, FromTimeText, ToTimeText;
    Button SendDyspoButton;
    CheckBox FullDayCheckBox;
    String fullDate;
    private int day, month, year,hour, minute;
//    private Calendar calendar;
    long millis = System.currentTimeMillis();
    Date date = new Date(millis);
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    User actualuser;
    String user;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dyspo,container, false);
        DateText = view.findViewById(R.id.Date);
        FromTimeText = view.findViewById(R.id.FromTime);
        ToTimeText = view.findViewById(R.id.ToTime);
        SendDyspoButton = view.findViewById(R.id.SendDyspoButton);
        FullDayCheckBox = view.findViewById(R.id.FullDayCheckBox);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        fullDate = format.format(date);
        String[] parts = fullDate.split(" ");
        String actualDate = parts[0];
        String actualTime = parts[1];

        DateText.setText(actualDate);

        String[] dateParts = actualDate.split("/");
        day = Integer.parseInt(dateParts[0]);
        month = Integer.parseInt(dateParts[1]);
        year = Integer.parseInt(dateParts[2]);


        String[] timeParts = actualTime.split(":");
        hour = Integer.parseInt(timeParts[0]);
        minute = Integer.parseInt(timeParts[1]);

        FromTimeText.setText(hour + ":" + minute);
        ToTimeText.setText(hour+1 + ":" + minute);

        DateText.setOnClickListener(v -> DateFromDialog(day, month, year));


        FullDayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    FromTimeText.setEnabled(false);
                    FromTimeText.setText("8:00");
                    ToTimeText.setEnabled(false);
                    ToTimeText.setText("1:00");
                }else{
                    FromTimeText.setEnabled(true);
                    ToTimeText.setEnabled(true);
                }
            }
        });

        SendDyspoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dyspoDate, dyspoStart, dyspoEnd;

                if (DateText.getText() == null){
                    DateText.setError("Field cannot be empty!", Drawable.createFromPath(String.valueOf(ic_baseline_error)));
                    return;

                }
                if (FromTimeText.getText() == null){
                    FromTimeText.setError("Field cannot be empty!", Drawable.createFromPath(String.valueOf(ic_baseline_error)));
                    return;
                }
                if (ToTimeText.getText() == null){
                    ToTimeText.setError("Field cannot be empty!", Drawable.createFromPath(String.valueOf(ic_baseline_error)));
                    return;
                }

                user = actualuser.getUserFullName();
                dyspoDate = DateText.getText().toString().trim();
                dyspoStart = FromTimeText.getText().toString().trim();
                dyspoEnd = ToTimeText.getText().toString().trim();
                firebaseDatabase.getReference().child(dyspoDate).child(user).setValue(dyspoStart,dyspoEnd);

            }
        });

        return view;
    }

//    private void DateToDialog(int aday, int amonth, int ayear) {
//        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> ToDateText.setText(dayOfMonth + "/" + month + "/" + year);
//        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), listener, ayear, amonth-1, aday);
//        datePickerDialog.show();
//    }

    private void DateFromDialog(int aday, int amonth, int ayear) {

        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> DateText.setText(dayOfMonth + "/" + month + "/" + year);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), listener, ayear, amonth-1, aday);
        datePickerDialog.show();
    }
}
