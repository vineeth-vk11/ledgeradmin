package com.ledgeradmin.HeadsHelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledgeradmin.HeadsHelper.Adapters.CompaniesAdapter;
import com.ledgeradmin.HeadsHelper.Models.CompaniesModel;
import com.ledgeradmin.R;

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

    String name, email, password, phone, address;
    ArrayList<CompaniesModel> existingCompanies;

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

        if(getArguments() != null){
            name = bundle.getString("name");
            email = bundle.getString("email");
            password = bundle.getString("password");
            phone = bundle.getString("password");
            address = bundle.getString("address");
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

                final String name = txtName.getText().toString();
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();
                final String phone = txtPhoneNumber.getText().toString();
                final String address = txtAddress.getText().toString();

                final HashMap<String, String> head = new HashMap<>();
                head.put("name",name);
                head.put("email",email);
                head.put("password",password);
                head.put("phone",phone);
                head.put("address",address);
                head.put("role","head");

                db.collection("Users").add(head).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        db.collection("Users").whereEqualTo("name",name).whereEqualTo("email",email).whereEqualTo("password",password)
                                .whereEqualTo("phone",phone).whereEqualTo("address",address).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                                            HeadsFragment headsFragment = new HeadsFragment();
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.main_frame, headsFragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        return view;
    }
}