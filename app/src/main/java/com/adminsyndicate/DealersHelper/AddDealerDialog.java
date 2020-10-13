package com.adminsyndicate.DealersHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.adminsyndicate.R;

import java.util.HashMap;

public class AddDealerDialog extends AppCompatDialogFragment {

    EditText name;
    EditText email;
    EditText password;
    EditText phoneNumber;
    EditText address;
    EditText osLimit;

    String existingName;
    String existingEmail;
    String existingPassword;
    String existingPhoneNumber;
    String existingAddress;
    String id;
    String existingOsLimit;

    String positive;

    String company;
    String sales;


    public AddDealerDialog(String existingName, String existingEmail, String existingPassword, String existingPhoneNumber, String existingAddress, String existingOsLimit, String id, String company, String sales) {
        this.existingName = existingName;
        this.existingEmail = existingEmail;
        this.existingPassword = existingPassword;
        this.existingPhoneNumber = existingPhoneNumber;
        this.existingAddress = existingAddress;
        this.existingOsLimit = existingOsLimit;
        this.id = id;
        this.company = company;
        this.sales = sales;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_dealers, null);


        name = view.findViewById(R.id.name_edit);
        email = view.findViewById(R.id.email_edit);
        password = view.findViewById(R.id.password_edit);
        phoneNumber = view.findViewById(R.id.phone_number_edit);
        address = view.findViewById(R.id.address_edit);
        osLimit = view.findViewById(R.id.os_edit);

        if(existingName!=null && existingEmail!=null && existingPassword!=null && existingPhoneNumber!=null && existingAddress!=null && existingOsLimit != null){
            name.setText(existingName);
            email.setText(existingEmail);
            password.setText(existingPassword);
            phoneNumber.setText(existingPhoneNumber);
            address.setText(existingAddress);
            osLimit.setText(existingOsLimit);
            positive = "Save";
        }
        else{
            positive = "Add";
        }

        builder.setView(view)
                .setTitle("Add Dealer Person")
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
                        final String enteredOsLimit = osLimit.getText().toString();

                        final HashMap<String, Object> dealer = new HashMap<>();
                        dealer.put("name",enteredName);
                        dealer.put("email",enteredEmail);
                        dealer.put("password",enteredPassword);
                        dealer.put("phoneNumber",enteredPhoneNumber);
                        dealer.put("address",enteredAddress);
                        dealer.put("role","dealer");
                        dealer.put("company", company);
                        dealer.put("sales",sales);
                        dealer.put("osLimit",enteredOsLimit);

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
                        else if(TextUtils.isEmpty(enteredOsLimit)){
                            Toast.makeText(getActivity(), "Enter a outstanding limit", Toast.LENGTH_SHORT).show();
                        }
                        else if(existingName!=null && existingEmail!=null && existingPassword!=null && existingPhoneNumber!=null && existingAddress!=null && existingOsLimit!= null) {
                            final FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Users").document(id).update(dealer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collection("Companies").document(company).collection("sales").document(sales)
                                            .collection("dealers").document(id).update(dealer).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                            db.collection("Users").add(dealer).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    db.collection("Users").whereEqualTo("name",enteredName).whereEqualTo("email",enteredEmail)
                                            .whereEqualTo("phoneNumber",enteredPhoneNumber).whereEqualTo("password",enteredPassword)
                                            .whereEqualTo("address",enteredAddress).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            String createdId = documentSnapshot.getId();

                                            db.collection("Companies").document(company).collection("sales").document(sales)
                                                    .collection("dealers").document(createdId).set(dealer).addOnCompleteListener(new OnCompleteListener<Void>() {
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

}
