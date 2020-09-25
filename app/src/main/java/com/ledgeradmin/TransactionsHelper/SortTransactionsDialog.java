package com.ledgeradmin.TransactionsHelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ledgeradmin.R;

import java.util.Calendar;

public class SortTransactionsDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {

    Button starDate;
    Button endDate;

    String type;
    String existingStart;
    String existingEnd;

    String positive;
    String negative;

    public SortTransactionsDialog(String existingStart, String existingEnd) {
        this.existingStart = existingStart;
        this.existingEnd = existingEnd;
    }


    public interface OnDatesSelected{
        void sendInput(String startDate, String endDate);
    }

    public OnDatesSelected onDatesSelected;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_sort_transaction, null);

        starDate = view.findViewById(R.id.startDate);
        endDate = view.findViewById(R.id.endDate);

        if(existingStart!=null && existingEnd!=null){
            starDate.setText(existingStart);
            endDate.setText(existingEnd);
            negative = "Clear";
        }
        else {
            negative = "Cancel";
        }

        starDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                type = "1";
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                type = "2";
            }
        });

        builder.setView(view)
                .setTitle("Sort Transactions")
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(negative.equals("Clear")){
                            starDate.setText("Select start date");
                            endDate.setText("select end date");
                            onDatesSelected.sendInput(null,null);
                        }
                    }
                })
                .setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String start = starDate.getText().toString();
                        String end = endDate.getText().toString();

                        if(start.equals("Select start date")){
                            Toast.makeText(getActivity(),"select a start date",Toast.LENGTH_SHORT).show();
                        }
                        else if(end.equals("Select end date")){
                            Toast.makeText(getActivity(),"select a end date",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            onDatesSelected.sendInput(start,end);
                        }
                    }
                });

        return builder.create();
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONDAY),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String selectedDate = dayOfMonth + "/" + month + "/" + year;

        switch (type){
            case "1":
                starDate.setText(selectedDate);
                break;
            case "2":
                endDate.setText(selectedDate);
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            onDatesSelected = (OnDatesSelected) getTargetFragment();
        }catch (ClassCastException e){

        }
    }
}
