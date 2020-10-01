package com.ledgeradmin.DealersHelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledgeradmin.R;
import com.ledgeradmin.SalesHelper.AddSalesDialog;

import java.util.ArrayList;


public class DealersFragment extends Fragment {

    RecyclerView dealers;
    FirebaseFirestore db;
    ArrayList<DealersModel> dealersModelArrayList;
    String company;
    String salesId;

    FloatingActionButton floatingActionButton;

    SearchView searchView;
    DealersAdapter dealersAdapter;

    ImageButton imageButton;

    ImageButton sort;
    ImageButton download;
    ImageButton share;
    ImageButton logout;

    ProgressBar progressBar;
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dealers, container, false);

        imageButton = getActivity().findViewById(R.id.add_transaction);
        imageButton.setVisibility(View.INVISIBLE);

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);
        logout = getActivity().findViewById(R.id.logout);

        sort.setVisibility(View.INVISIBLE);
        download.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        logout.setVisibility(View.INVISIBLE);

        Bundle bundle = getArguments();
        salesId = bundle.getString("userId");
        company = bundle.getString("company");

        floatingActionButton = view.findViewById(R.id.add_dealer);
        progressBar = view.findViewById(R.id.progressBar4);
        imageView = view.findViewById(R.id.empty);

        dealers = view.findViewById(R.id.dealersRecycler);
        db = FirebaseFirestore.getInstance();
        dealersModelArrayList = new ArrayList<>();

        dealers.setLayoutManager(new LinearLayoutManager(getContext()));
        dealers.setHasFixedSize(true);

        db.collection("Companies").document(company).collection("sales").document(salesId).collection("dealers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getDealers();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDealerDialog addDealerDialog = new AddDealerDialog(null,null,null,null,null,null,company,salesId);
                addDealerDialog.show(getActivity().getSupportFragmentManager(), "Add Dealer Dialog");
            }
        });

        searchView = view.findViewById(R.id.dealerSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dealersAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private void getDealers(){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Companies").document(company).collection("sales").document(salesId).collection("dealers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dealersModelArrayList.clear();
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    DealersModel dealersModel = new DealersModel();
                    dealersModel.setName(documentSnapshot.getString("name"));
                    dealersModel.setId(documentSnapshot.getId());
                    dealersModel.setCompany(company);
                    dealersModel.setSalesId(salesId);
                    dealersModel.setEmail(documentSnapshot.getString("email"));
                    dealersModel.setPhoneNumber(documentSnapshot.getString("phoneNumber"));
                    dealersModel.setAddress(documentSnapshot.getString("address"));
                    dealersModel.setPassword(documentSnapshot.getString("password"));

                    dealersModelArrayList.add(dealersModel);
                }

                dealersAdapter = new DealersAdapter(getContext(), dealersModelArrayList);
                dealers.setAdapter(dealersAdapter);
                progressBar.setVisibility(View.INVISIBLE);

                if(dealersModelArrayList.size()==0){
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}