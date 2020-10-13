package com.adminsyndicate.TransactionsHelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.adminsyndicate.R;

import java.util.Calendar;
import java.util.HashMap;

public class AddTransactionDialog extends AppCompatDialogFragment  implements DatePickerDialog.OnDateSetListener{

    Button date;
    RadioGroup radioGroup;
    RadioButton selectedType;

    EditText particular;
    EditText amount;
    EditText voucher;

    String company;
    String sales;
    String dealer;
    String id;
    String existingParticular;
    String existingAmount;
    String existingDate;
    String existingType;
    String existingVoucher;

    String type;

    String positive;

    RadioButton credit;
    RadioButton debit;

    CheckBox checkBox;

    String send;

    public interface OnWhatsAppChecked{
        void sendWhatsAppInput(String amount, String type, String send);
    }

    public AddTransactionDialog.OnWhatsAppChecked onWhatsAppChecked;

    public AddTransactionDialog(String company, String sales, String dealer, String id, String existingParticular, String existingAmount, String existingDate, String existingType, String existingVoucher) {
        this.company = company;
        this.sales = sales;
        this.dealer = dealer;
        this.id = id;
        this.existingParticular = existingParticular;
        this.existingAmount = existingAmount;
        this.existingDate = existingDate;
        this.existingType = existingType;
        this.existingVoucher = existingVoucher;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_add_transaction, null);

        credit = view.findViewById(R.id.credit);
        debit = view.findViewById(R.id.debit);

        particular = view.findViewById(R.id.particular_edit);
        amount = view.findViewById(R.id.amount_edit);
        radioGroup = view.findViewById(R.id.type);
        voucher = view.findViewById(R.id.voucher_edit);
        checkBox = view.findViewById(R.id.checkBox);

        date = view.findViewById(R.id.selectdate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    send = "yes";
                }
                else{
                    send = "no";
                }
            }
        });

        if(company!=null && sales!=null && id!=null && dealer!=null && existingParticular!=null
                && existingAmount!=null && existingDate!=null && existingType!=null && existingVoucher!= null){

            particular.setText(existingParticular);
            amount.setText(existingAmount);
            date.setText(existingDate);
            voucher.setText(existingVoucher);
            positive = "Save";
            checkBox.setVisibility(View.INVISIBLE);

            if(debit.getText().toString().equals(existingType)){
                debit.setChecked(true);
            }
            else if(credit.getText().toString().equals(existingType)) {
                credit.setChecked(true);
            }

        }
        else{
            positive = "Ok";
        }

        builder.setView(view)
                .setTitle("Add Transaction")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int amountType = radioGroup.getCheckedRadioButtonId();

                        if(amountType != -1) {
                            selectedType = view.findViewById(amountType);
                            type = selectedType.getText().toString();
                        }

                        String enteredParticular = particular.getText().toString();
                        final String enteredAmount = amount.getText().toString();
                        String selectedDate = date.getText().toString();
                        String enteredVoucher = voucher.getText().toString();

                        if(TextUtils.isEmpty(enteredParticular)){
                            Toast.makeText(getActivity(),"Enter particular",Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(enteredAmount)){
                            Toast.makeText(getActivity(),"Enter the amount",Toast.LENGTH_SHORT).show();
                        }
                        else if(selectedDate.equals("Select Date")){
                            Toast.makeText(getActivity(),"Select date",Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(enteredVoucher)){
                            Toast.makeText(getActivity(),"Enter the voucher",Toast.LENGTH_SHORT).show();
                        }
                        else if(amountType == -1){
                            Toast.makeText(getActivity(),"Select type",Toast.LENGTH_SHORT).show();
                        }
                        else if(positive.equals("Ok")){
                            HashMap<String, String> transaction = new HashMap<>();
                            transaction.put("type",type);
                            transaction.put("particular",enteredParticular);
                            transaction.put("amount",enteredAmount);
                            transaction.put("date",selectedDate);
                            transaction.put("voucher",enteredVoucher);

                            FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Companies").document(company).collection("sales").document(sales)
                                    .collection("dealers").document(dealer).collection("transactions")
                                    .add(transaction).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    onWhatsAppChecked.sendWhatsAppInput(enteredAmount, type,send);
                                }
                            });

                        }
                        else{

                            HashMap<String, String> transaction = new HashMap<>();
                            transaction.put("type",type);
                            transaction.put("particular",enteredParticular);
                            transaction.put("amount",enteredAmount);
                            transaction.put("date",selectedDate);
                            transaction.put("voucher",enteredVoucher);

                            FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Companies").document(company).collection("sales").document(sales)
                                    .collection("dealers").document(dealer).collection("transactions")
                                    .document(id).set(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
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
        int correctedMonth = month+1;
        String selectedDate = dayOfMonth + "/" + correctedMonth + "/" + year;
        date.setText(selectedDate);
    }

    private String getType(){
        int typeId = radioGroup.getCheckedRadioButtonId();
        selectedType = getView().findViewById(typeId);
        String type = selectedType.getText().toString();

        return type;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            onWhatsAppChecked = (AddTransactionDialog.OnWhatsAppChecked) getTargetFragment();
        }catch (ClassCastException e){

        }
    }
}
