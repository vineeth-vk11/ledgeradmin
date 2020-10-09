package com.ledgeradmin.SalesHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledgeradmin.R;
import com.ledgeradmin.TransactionsHelper.SortTransactionsDialog;

import java.util.HashMap;

public class AddSalesDialog extends AppCompatDialogFragment {

    EditText name;
    EditText email;
    EditText password;
    EditText phoneNumber;
    EditText address;

    String existingName;
    String existingEmail;
    String existingPassword;
    String existingPhoneNumber;
    String existingAddress;
    String id;

    String positive;

    String company;

    Button upload;

    public AddSalesDialog(String existingName, String existingEmail, String existingPassword, String existingPhoneNumber, String existingAddress, String id, String company) {
        this.existingName = existingName;
        this.existingEmail = existingEmail;
        this.existingPassword = existingPassword;
        this.existingPhoneNumber = existingPhoneNumber;
        this.existingAddress = existingAddress;
        this.id = id;
        this.company = company;
    }

    public interface OnSales{
        void sendInput(String isAdded, String isEdited);
    }

    public OnSales onSales;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_sales, null);

        name = view.findViewById(R.id.name_edit);
        email = view.findViewById(R.id.email_edit);
        password = view.findViewById(R.id.password_edit);
        phoneNumber = view.findViewById(R.id.phone_number_edit);
        address = view.findViewById(R.id.address_edit);
        upload = view.findViewById(R.id.upload);

        if(existingName!=null && existingEmail!=null && existingPassword!=null && existingPhoneNumber!=null && existingAddress!=null){
            name.setText(existingName);
            email.setText(existingEmail);
            password.setText(existingPassword);
            phoneNumber.setText(existingPhoneNumber);
            address.setText(existingAddress);
            positive = "Save";
        }
        else{
            positive = "Add";
        }

        builder.setView(view)
                .setTitle("Add Sales Person")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String enteredName = name.getText().toString();
                        final String enteredEmail = email.getText().toString();
                        final String enteredPassword = password.getText().toString();
                        final String enteredPhoneNumber = phoneNumber.getText().toString();
                        final String enteredAddress = address.getText().toString();

                        final HashMap<String, Object> sales = new HashMap<>();
                        sales.put("name",enteredName);
                        sales.put("email",enteredEmail);
                        sales.put("password",enteredPassword);
                        sales.put("phoneNumber",enteredPhoneNumber);
                        sales.put("address",enteredAddress);
                        sales.put("role","sales");
                        sales.put("company", company);

                        if(TextUtils.isEmpty(enteredName)){
                            Toast.makeText(getActivity(), "Enter the name", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(enteredEmail)){
                            Toast.makeText(getActivity(), "Enter the email", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(enteredPassword)){
                            Toast.makeText(getActivity(), "Enter the password", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(enteredPhoneNumber)){
                            Toast.makeText(getActivity(), "Enter the phone number", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(enteredAddress)){
                            Toast.makeText(getActivity(), "Enter the address", Toast.LENGTH_SHORT).show();
                        }
                        else if(!enteredEmail.contains("@")){
                            Toast.makeText(getActivity(), "Enter a correct email", Toast.LENGTH_SHORT).show();
                        }
                        else if(enteredPhoneNumber.length()!=10){
                            Toast.makeText(getActivity(), "Enter a correct phone number", Toast.LENGTH_SHORT).show();
                        }
                        else if(existingName!=null && existingEmail!=null && existingPassword!=null && existingPhoneNumber!=null && existingAddress!=null) {
                            final FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Users").document(id).set(sales).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collection("Companies").document(company).collection("sales").document(id).update(sales).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                                }
                            });
                        }
                        else{
                            final FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Users").add(sales).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    db.collection("Users").whereEqualTo("name",enteredName).whereEqualTo("email",enteredEmail)
                                            .whereEqualTo("phoneNumber",enteredPhoneNumber).whereEqualTo("password",enteredPassword)
                                            .whereEqualTo("address",enteredAddress).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            String createdId = documentSnapshot.getId();

                                            db.collection("Companies").document(company).collection("sales").document(createdId).set(sales).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            onSales = (OnSales) getTargetFragment();
        }catch (ClassCastException e){

        }
    }

}
