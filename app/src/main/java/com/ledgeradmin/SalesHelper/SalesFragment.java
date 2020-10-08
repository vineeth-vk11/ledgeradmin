package com.ledgeradmin.SalesHelper;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledgeradmin.CompaniesHelper.AddCompanyDialog;
import com.ledgeradmin.R;
import com.ledgeradmin.TransactionsHelper.TransactionsFragment;

import java.util.ArrayList;

public class SalesFragment extends Fragment implements AddSalesDialog.OnSales {

    RecyclerView sales;
    FirebaseFirestore db;
    ArrayList<SalesModel> salesModelArrayList;
    String company;

    FloatingActionButton floatingActionButton;

    SearchView searchView;
    SalesAdapter salesAdapter;

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
        View view = inflater.inflate(R.layout.fragment_sales, container, false);

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
        company = bundle.getString("company");

        floatingActionButton = view.findViewById(R.id.add_sales);
        progressBar = view.findViewById(R.id.progressBar3);
        imageView = view.findViewById(R.id.empty);

        sales = view.findViewById(R.id.salesRecycler);
        db = FirebaseFirestore.getInstance();
        salesModelArrayList = new ArrayList<>();

        sales.setLayoutManager(new LinearLayoutManager(getContext()));
        sales.setHasFixedSize(true);

        db.collection("Companies").document(company).collection("sales").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getSales();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSalesDialog addSalesDialog = new AddSalesDialog(null,null, null, null, null,null,company);
                addSalesDialog.setTargetFragment(SalesFragment.this, 1);
                addSalesDialog.show(getActivity().getSupportFragmentManager(), "Add Sales Dialog");
            }
        });

        searchView = view.findViewById(R.id.salesSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                salesAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private void getSales(){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Companies").document(company).collection("sales").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                salesModelArrayList.clear();
                for(DocumentSnapshot documentSnapshot: task.getResult()){

                    SalesModel salesModel = new SalesModel();
                    salesModel.setName(documentSnapshot.getString("name"));
                    salesModel.setId(documentSnapshot.getId());
                    salesModel.setAddress(documentSnapshot.getString("address"));
                    salesModel.setCompany(documentSnapshot.getString("company"));
                    salesModel.setEmail(documentSnapshot.getString("email"));
                    salesModel.setPassword(documentSnapshot.getString("password"));
                    salesModel.setPhoneNumber(documentSnapshot.getString("phoneNumber"));
                    salesModel.setRole(documentSnapshot.getString("role"));

                    if(documentSnapshot.getString("pic") != null){
                        salesModel.setImage(documentSnapshot.getString("pic"));
                    }

                    Log.i("name",documentSnapshot.getString("name"));

                    salesModelArrayList.add(salesModel);
                }

                salesAdapter = new SalesAdapter(getContext(), salesModelArrayList, company);
                sales.setAdapter(salesAdapter);
                progressBar.setVisibility(View.INVISIBLE);

                if(salesModelArrayList.size()==0){
                    imageView.setVisibility(View.VISIBLE);
                }
                else {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void sendInput(String isAdded, String isEdited) {
        Log.i("added",isAdded);
        Log.i("edited",isEdited);
    }
}