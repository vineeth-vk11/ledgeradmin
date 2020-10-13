package com.adminsyndicate.CompaniesHelper;

import android.app.AlertDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.adminsyndicate.R;

import java.util.HashMap;

public class AddCompanyDialog extends AppCompatDialogFragment {
    private EditText companyName;
    String existingName;
    String existingId;

    String positive;

    public AddCompanyDialog(String existingName, String existingId) {
        this.existingName = existingName;
        this.existingId = existingId;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_add_company, null);

        companyName = view.findViewById(R.id.company_name_edit);

        if(existingName!=null){
            companyName.setText(existingName);
            positive = "Save";
        }
        else {
            positive = "Add";
        }

        builder.setView(view)
                .setTitle("Add Company")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String company = companyName.getText().toString();
                        final HashMap<String, Object> companyMap = new HashMap<>();
                        companyMap.put("name",company);

                        if(TextUtils.isEmpty(company)){
                            Toast.makeText(getActivity(), "Enter the company name", Toast.LENGTH_SHORT).show();
                        }
                        else if(existingName==null) {
                            final FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Companies").add(companyMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                }
                            });
                        }
                        else{
                            final FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();

                            db.collection("Companies").document(existingId).update(companyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }

                    }
                });

        return builder.create();
    }

}
