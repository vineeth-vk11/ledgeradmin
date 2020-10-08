package com.ledgeradmin.CompaniesHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledgeradmin.R;

import java.util.HashMap;

public class ConfirmationDialog extends AppCompatDialogFragment {

    String type;
    String company;
    String sales;
    String dealer;
    String transaction;
    String head;

    public ConfirmationDialog(String type, String company, String sales, String dealer, String transaction, String head) {
        this.type = type;
        this.company = company;
        this.sales = sales;
        this.dealer = dealer;
        this.transaction = transaction;
        this.head = head;
    }

    EditText delete;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_confirmation, null);

        delete = view.findViewById(R.id.message);

        builder.setView(view)
                .setTitle("Type DELETE to confirm deletion")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(delete.getText().toString().equals("DELETE")){

                            final FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            if(type.equals("company")){

                                db.collection("Companies").document(company).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                            }

                            else if(type.equals("sales")){
                                db.collection("Companies").document(company).collection("sales").document(sales).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        db.collection("Users").document(sales).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }
                                });
                            }

                            else if(type.equals("dealer")){

                                db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers").document(dealer).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        db.collection("Users").document(dealer).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }
                                });
                            }

                            else if(type.equals("transaction")){
                                db.collection("Companies").document(company).collection("sales")
                                        .document(sales).collection("dealers").document(dealer)
                                        .collection("transactions").document(transaction).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }

                            else if(type.equals("head")){
                                db.collection("Heads").document(head)
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        db.collection("Users").document(head).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }
                                });
                            }
                        }
                        else {
                            Log.i("text",delete.getText().toString());
                        }
                    }
                });

        return builder.create();
    }
}
