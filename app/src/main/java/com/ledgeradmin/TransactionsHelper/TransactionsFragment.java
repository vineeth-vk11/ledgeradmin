package com.ledgeradmin.TransactionsHelper;

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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledgeradmin.R;
import com.ledgeradmin.SalesHelper.AddSalesDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;


public class TransactionsFragment extends Fragment implements SortTransactionsDialog.OnDatesSelected {

    RecyclerView transactions;
    FirebaseFirestore db;
    ArrayList<TransactionsModel> transactionsModelArrayList;

    String company;
    String sales;
    String id;

    ImageButton imageButton;
    ImageButton sort;
    ImageButton download;
    ImageButton share;

    SearchView searchView;
    TransactionsAdapter transactionsAdapter;

    String startDateExisting;
    String endDateExisting;

    Date startDateD;
    Date endDateD;
    Date dateD;

    String creditAmount;
    String debitAmount;

    int currentDebit;
    int currentCredit;
    int openingCreditAmount;
    int openingDebitAmount;
    int openingBalance;
    int outStandingAmount;

    TextView currentC;
    TextView currentD;
    TextView outstanding;
    TextView outStandingCredit;
    TextView openingCredit;
    TextView openingDebit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        Bundle bundle = getArguments();
        company = bundle.getString("company");
        sales = bundle.getString("sales");
        id = bundle.getString("userId");

        imageButton = getActivity().findViewById(R.id.add_transaction);
        imageButton.setVisibility(View.VISIBLE);

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);

        currentC = view.findViewById(R.id.currentCredit);
        currentD = view.findViewById(R.id.currentDebit);
        outstanding = view.findViewById(R.id.outStandingTotal);
        openingDebit = view.findViewById(R.id.openingBalanceDebit);
        openingCredit = view.findViewById(R.id.openingBalanceCredit);
        outStandingCredit = view.findViewById(R.id.outStandingCredit);

        sort.setVisibility(View.VISIBLE);
        download.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);

        Log.i("company",company);
        Log.i("sales",sales);
        Log.i("userId",id);

        transactions = view.findViewById(R.id.transactions_recycler);
        db = FirebaseFirestore.getInstance();
        transactionsModelArrayList = new ArrayList<>();

        transactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactions.setHasFixedSize(true);

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortTransactionsDialog sortTransactionsDialog = new SortTransactionsDialog(startDateExisting,endDateExisting);
                sortTransactionsDialog.setTargetFragment(TransactionsFragment.this, 1);
                sortTransactionsDialog.show(getActivity().getSupportFragmentManager(), "Sort transactions");
            }
        });

        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getTransactions();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTransactionDialog addTransactionDialog = new AddTransactionDialog(company,sales,id,null,null,null,null,null, null);
                addTransactionDialog.show(getActivity().getSupportFragmentManager(), "Add Transaction Dialog");
            }
        });

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionsAdapter.getFilter().filter(newText);
                return false;
            }
        });


        return view;
    }

    private void getTransactions(){

        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                transactionsModelArrayList.clear();
                currentDebit = 0;
                currentCredit = 0;
                openingCreditAmount = 0;
                openingDebitAmount = 0;
                openingBalance = 0;
                outStandingAmount = 0;

                currentC.setText("");
                currentD.setText("");
                outstanding.setText("");
                outStandingCredit.setText("");
                openingCredit.setText("");
                openingDebit.setText("");

                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    TransactionsModel transactionsModel = new TransactionsModel();

                    if(documentSnapshot.getString("type").equals("Credit")){
                        creditAmount = documentSnapshot.getString("amount");
                        debitAmount = "0";

                        transactionsModel.setCredit(documentSnapshot.getString("amount"));
                        transactionsModel.setDebit("");
                    }
                    else {
                        transactionsModel.setDebit(documentSnapshot.getString("amount"));
                        transactionsModel.setCredit("");

                        creditAmount = "0";
                        debitAmount = documentSnapshot.getString("amount");

                    }

                    DateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
                    Date date = new Date();
                    String finalDateS = "01/"+dateFormat.format(date);

                    Date finalDate = null;
                    Date currentDate = null;

                    try {
                        finalDate = new SimpleDateFormat("dd/MM/yyyy").parse(finalDateS);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        currentDate = new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(!currentDate.before(finalDate)){
                        currentDebit += Integer.parseInt(debitAmount);
                        currentCredit += Integer.parseInt(creditAmount);
                    }

                    if(!currentDate.after(finalDate)){

                        openingCreditAmount += Integer.parseInt(creditAmount);
                        openingDebitAmount += Integer.parseInt(debitAmount);

                    }

                    transactionsModel.setDate(documentSnapshot.getString("date"));
                    transactionsModel.setParticular(documentSnapshot.getString("particular"));
                    transactionsModel.setCompany(company);
                    transactionsModel.setSales(sales);
                    transactionsModel.setId(documentSnapshot.getId());
                    transactionsModel.setAmount(documentSnapshot.getString("amount"));
                    transactionsModel.setDealer(id);
                    transactionsModel.setType(documentSnapshot.getString("type"));
                    transactionsModel.setVoucher(documentSnapshot.getString("voucher"));

                    try {
                        transactionsModel.setDateD(new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    transactionsModelArrayList.add(transactionsModel);
                }

                openingBalance = openingCreditAmount - openingDebitAmount;
                outStandingAmount = currentCredit - currentDebit+openingBalance;

                if(openingBalance<0){
                    openingDebit.setText(String.valueOf(Math.abs(openingBalance)));
                }
                else {
                    openingCredit.setText(String.valueOf(Math.abs(openingBalance)));
                }

                if(outStandingAmount>0){
                    outStandingCredit.setText(String.valueOf(Math.abs(outStandingAmount)));
                }
                else {
                    outstanding.setText(String.valueOf(Math.abs(outStandingAmount)));
                }

                currentC.setText(String.valueOf(Math.abs(currentCredit)));
                currentD.setText(String.valueOf(Math.abs(currentDebit)));

                Comparator c = Collections.reverseOrder();
                Collections.sort(transactionsModelArrayList,c);

                transactionsAdapter = new TransactionsAdapter(getContext(), transactionsModelArrayList);
                transactions.setAdapter(transactionsAdapter);


            }
        });
    }

    private void getSortedTransactions(final String start, final String end){

        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                transactionsModelArrayList.clear();
                currentDebit = 0;
                currentCredit = 0;
                openingCreditAmount = 0;
                openingDebitAmount = 0;
                openingBalance = 0;
                outStandingAmount = 0;

                currentC.setText("");
                currentD.setText("");
                outstanding.setText("");
                outStandingCredit.setText("");
                openingCredit.setText("");
                openingDebit.setText("");

                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    TransactionsModel transactionsModel = new TransactionsModel();

                    try {
                        startDateD = new SimpleDateFormat("dd/MM/yyyy").parse(start);
                        startDateD.getMonth();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        endDateD = new SimpleDateFormat("dd/MM/yyyy").parse(end);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        dateD = new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(documentSnapshot.getString("type").equals("Credit")){
                        transactionsModel.setCredit(documentSnapshot.getString("amount"));
                        transactionsModel.setDebit("");

                        creditAmount = documentSnapshot.getString("amount");
                        debitAmount = "0";

                    }
                    else {
                        transactionsModel.setDebit(documentSnapshot.getString("amount"));
                        transactionsModel.setCredit("");

                        creditAmount = "0";
                        debitAmount = documentSnapshot.getString("amount");
                    }

                    try {
                        transactionsModel.setDateD(new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    transactionsModel.setDate(documentSnapshot.getString("date"));
                    transactionsModel.setParticular(documentSnapshot.getString("particular"));
                    transactionsModel.setCompany(company);
                    transactionsModel.setSales(sales);
                    transactionsModel.setId(documentSnapshot.getId());
                    transactionsModel.setAmount(documentSnapshot.getString("amount"));
                    transactionsModel.setDealer(id);
                    transactionsModel.setType(documentSnapshot.getString("type"));
                    transactionsModel.setVoucher(documentSnapshot.getString("voucher"));

                    if(!dateD.before(startDateD)){
                        currentDebit += Integer.parseInt(debitAmount);
                        currentCredit += Integer.parseInt(creditAmount);
                    }

                    if(dateD.before(startDateD)){
                        openingCreditAmount += Integer.parseInt(creditAmount);
                        openingDebitAmount += Integer.parseInt(debitAmount);
                    }

                    if(!dateD.before(startDateD)&& !dateD.after(endDateD)){
                        transactionsModelArrayList.add(transactionsModel);
                    }
                }

                openingBalance = openingCreditAmount - openingDebitAmount;
                outStandingAmount = currentCredit - currentDebit+openingBalance;

                if(openingBalance<0){
                    openingDebit.setText(String.valueOf(Math.abs(openingBalance)));
                }
                else {
                    openingCredit.setText(String.valueOf(Math.abs(openingBalance)));
                }

                if(outStandingAmount>0){
                    outStandingCredit.setText(String.valueOf(Math.abs(outStandingAmount)));
                }
                else {
                    outstanding.setText(String.valueOf(Math.abs(outStandingAmount)));
                }

                currentC.setText(String.valueOf(Math.abs(currentCredit)));
                currentD.setText(String.valueOf(Math.abs(currentDebit)));


                Comparator c = Collections.reverseOrder();
                Collections.sort(transactionsModelArrayList,c);

                transactionsAdapter = new TransactionsAdapter(getContext(), transactionsModelArrayList);
                transactions.setAdapter(transactionsAdapter);
            }
        });
    }

    @Override
    public void sendInput(String startDate, String endDate) {
        if(startDate!=null && endDate != null){
            startDateExisting = startDate;
            endDateExisting = endDate;
            getSortedTransactions(startDate,endDate);
        }
        else {
            getTransactions();
        }

    }
}