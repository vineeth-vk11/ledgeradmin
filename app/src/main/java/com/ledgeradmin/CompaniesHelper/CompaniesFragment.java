package com.ledgeradmin.CompaniesHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledgeradmin.LoginActivity;
import com.ledgeradmin.MainHelper.MainFragment;
import com.ledgeradmin.R;
import com.ledgeradmin.TransactionsHelper.TransactionsFragment;

import java.util.ArrayList;


public class CompaniesFragment extends Fragment{

    RecyclerView companies;
    FirebaseFirestore db;
    ArrayList<CompaniesModels> companiesModelsArrayList;
    String userId;

    FloatingActionButton floatingActionButton;

    SearchView searchView;
    CompaniesAdapter companiesAdapter;

    ImageButton imageButton;

    ImageButton sort;
    ImageButton download;
    ImageButton share;
    ImageButton logout;

    ProgressBar progressBar;

    ImageView imageView;

    TextView toolbarText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_companies, container, false);

        Log.i("activity", String.valueOf(getActivity()));
        imageButton = getActivity().findViewById(R.id.add_transaction);
        imageButton.setVisibility(View.INVISIBLE);

        toolbarText = getActivity().findViewById(R.id.toolbar);
        toolbarText.setText("Admin");
        toolbarText.setTextSize(24);

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);
        logout = getActivity().findViewById(R.id.logout);

        sort.setVisibility(View.GONE);
        download.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        logout.setVisibility(View.VISIBLE);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        companies = view.findViewById(R.id.companiesRecycler);
        db = FirebaseFirestore.getInstance();
        companiesModelsArrayList = new ArrayList<>();
        floatingActionButton = view.findViewById(R.id.add_company);
        progressBar = view.findViewById(R.id.progressBar2);
        imageView = view.findViewById(R.id.empty);

        companies.setLayoutManager(new LinearLayoutManager(getContext()));
        companies.setHasFixedSize(true);

        db.collection("Companies").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getCompanies();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCompanyDialog addCompanyDialog = new AddCompanyDialog(null,null);
                addCompanyDialog.show(getActivity().getSupportFragmentManager(), "Add Company Dialog");
            }
        });

        searchView = view.findViewById(R.id.companiesSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                companiesAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private void getCompanies(){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Companies").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                companiesModelsArrayList.clear();
                for(DocumentSnapshot documentSnapshot: task.getResult()){

                    CompaniesModels companiesModels = new CompaniesModels();
                    companiesModels.setCompanyName(documentSnapshot.getString("name"));
                    companiesModels.setCompanyId(documentSnapshot.getId());
                    companiesModels.setImage(documentSnapshot.getString("pic"));
                    companiesModelsArrayList.add(companiesModels);
                }

                companiesAdapter = new CompaniesAdapter(getContext(), companiesModelsArrayList);
                companies.setAdapter(companiesAdapter);
                progressBar.setVisibility(View.INVISIBLE);

                if(companiesModelsArrayList.size()==0){
                    imageView.setVisibility(View.VISIBLE);
                }
                else {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}