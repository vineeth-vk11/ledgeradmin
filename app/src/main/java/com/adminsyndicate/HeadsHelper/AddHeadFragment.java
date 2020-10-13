package com.adminsyndicate.HeadsHelper;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.adminsyndicate.HeadsHelper.Adapters.CompaniesAdapter;
import com.adminsyndicate.HeadsHelper.Models.CompaniesModel;
import com.adminsyndicate.MainActivity;
import com.adminsyndicate.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AddHeadFragment extends Fragment {

    RecyclerView companies;
    ArrayList<CompaniesModel> companiesModelArrayList;
    FirebaseFirestore db;

    ArrayList<CompaniesModel> selectedCompanies;

    Button save;

    EditText txtName;
    EditText txtEmail;
    EditText txtPassword;
    EditText txtPhoneNumber;
    EditText txtAddress;

    String name, email, password, phone, address, id;
    ArrayList<CompaniesModel> existingCompanies;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_head, container, false);

        Bundle bundle = getArguments();

        existingCompanies = new ArrayList<>();

        save = view.findViewById(R.id.saveHead);

        txtName = view.findViewById(R.id.name_edit);
        txtEmail = view.findViewById(R.id.email_edit);
        txtPassword = view.findViewById(R.id.password_edit);
        txtPhoneNumber = view.findViewById(R.id.phone_number_edit);
        txtAddress = view.findViewById(R.id.address_edit);

        progressBar = view.findViewById(R.id.progressBar7);

        if(getArguments() != null){
            name = bundle.getString("name");
            email = bundle.getString("email");
            password = bundle.getString("password");
            phone = bundle.getString("phone");
            address = bundle.getString("address");
            id = bundle.getString("id");
            existingCompanies = (ArrayList<CompaniesModel>) bundle.get("companies");

            txtName.setText(name);
            txtEmail.setText(email);
            txtPassword.setText(password);
            txtAddress.setText(address);
            txtPhoneNumber.setText(phone);

        }

        db = FirebaseFirestore.getInstance();

        companies = view.findViewById(R.id.selectCompaniesRecycler);
        companiesModelArrayList = new ArrayList<>();
        selectedCompanies = new ArrayList<>();

        companies.setLayoutManager(new LinearLayoutManager(getContext()));
        companies.setHasFixedSize(true);

        db.collection("Companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    CompaniesModel companiesModel = new CompaniesModel();

                    companiesModel.setId(documentSnapshot.getId());
                    companiesModel.setName(documentSnapshot.getString("name"));

                    Log.i("id",documentSnapshot.getId());

                    companiesModelArrayList.add(companiesModel);
                }

                CompaniesAdapter companiesAdapter = new CompaniesAdapter(getContext(), companiesModelArrayList, selectedCompanies, existingCompanies);
                companies.setAdapter(companiesAdapter);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                final String name = txtName.getText().toString();
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();
                final String phone = txtPhoneNumber.getText().toString();
                final String address = txtAddress.getText().toString();

                final HashMap<String, Object> head = new HashMap<>();
                head.put("name",name);
                head.put("email",email);
                head.put("password",password);
                head.put("phoneNumber",phone);
                head.put("address",address);
                head.put("role","head");

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getActivity(), "Enter name", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if(TextUtils.isEmpty(email)){
                    Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getActivity(), "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if(TextUtils.isEmpty(phone)){
                    Toast.makeText(getActivity(), "Enter phone number", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if(TextUtils.isEmpty(address)){
                    Toast.makeText(getActivity(), "Enter address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if(!email.contains("@")){
                    Toast.makeText(getActivity(), "Enter a correct email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else if(phone.length()!=10){
                    Toast.makeText(getActivity(), "Enter a correct number", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    if(getArguments() != null){

                        db.collection("Heads").document(id).collection("companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(DocumentSnapshot documentSnapshot1: task.getResult()){
                                    String companyId = documentSnapshot1.getId();

                                    db.collection("Heads").document(id).collection("companies").document(companyId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            db.collection("Users").document(id).update(head).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    db.collection("Heads").document(id).update(head).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            for(int i= 0; i<selectedCompanies.size();i++){

                                                                db.collection("Heads").document(id).collection("companies").document(selectedCompanies.get(i).getId())
                                                                        .set(selectedCompanies.get(i));

                                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                                startActivity(intent);
                                                                getActivity().finish();

                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });

                    }
                    else{

                        db.collection("Users").add(head).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                db.collection("Users").whereEqualTo("name",name).whereEqualTo("email",email).whereEqualTo("password",password)
                                        .whereEqualTo("phoneNumber",phone).whereEqualTo("address",address).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        DocumentSnapshot documentSnapshot1 = task.getResult().getDocuments().get(0);
                                        final String id = documentSnapshot1.getId();

                                        db.collection("Heads").document(id).set(head).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                for(int i= 0; i<selectedCompanies.size();i++){

                                                    db.collection("Heads").document(id).collection("companies").document(selectedCompanies.get(i).getId())
                                                            .set(selectedCompanies.get(i));

                                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                                    startActivity(intent);
                                                    getActivity().finish();

                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                }

            }
        });
        return view;
    }
}