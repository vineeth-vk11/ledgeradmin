package com.ledgeradmin.DealersHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledgeradmin.R;
import com.ledgeradmin.SalesHelper.AddSalesDialog;
import com.ledgeradmin.TransactionsHelper.TransactionsFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DealersAdapter extends RecyclerView.Adapter<DealersViewHolder>  implements Filterable {

    Context context;
    ArrayList<DealersModel> dealersModelArrayList;
    ArrayList<DealersModel> dealersModelArrayListAll;

    public DealersAdapter(Context context, ArrayList<DealersModel> dealersModelArrayList) {
        this.context = context;
        this.dealersModelArrayList = dealersModelArrayList;
        this.dealersModelArrayListAll = new ArrayList<>(dealersModelArrayList);
    }

    @NonNull
    @Override
    public DealersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_dealer, parent, false);
        return new DealersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealersViewHolder holder, final int position) {

        final String name = dealersModelArrayList.get(position).getName();
        final String email = dealersModelArrayList.get(position).getEmail();
        final String phoneNumber = dealersModelArrayList.get(position).getPhoneNumber();
        final String password = dealersModelArrayList.get(position).getPassword();
        final String address = dealersModelArrayList.get(position).getAddress();
        final String id = dealersModelArrayList.get(position).getId();
        final String company = dealersModelArrayList.get(position).getCompany();
        final String sales = dealersModelArrayList.get(position).getSalesId();

        holder.name.setText(dealersModelArrayList.get(position).getName());
        holder.image.setImageResource(R.drawable.ic_dealer);

        holder.dealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransactionsFragment transactionsFragment = new TransactionsFragment();

                Bundle bundle = new Bundle();
                bundle.putString("userId", dealersModelArrayList.get(position).getId());
                bundle.putString("company",dealersModelArrayList.get(position).getCompany());
                bundle.putString("sales",dealersModelArrayList.get(position).getSalesId());
                transactionsFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,transactionsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                AddDealerDialog addDealerDialog = new AddDealerDialog(name,email,password,phoneNumber,address,id,company,sales);
                addDealerDialog.show(activity.getSupportFragmentManager(), "Add Dealer Dialog");
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete dealer")
                        .setMessage("Are you sure you want to delete the dealer ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(v.getContext())
                                        .setTitle("Delete dealer Confirmation")
                                        .setMessage("Confirm deletion?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseFirestore db;
                                                db = FirebaseFirestore.getInstance();

                                                db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton("No",null).show();
                            }
                        })
                        .setNegativeButton("No",null).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return dealersModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<DealersModel> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(dealersModelArrayListAll);
            }
            else {
                for(int i = 0; i<dealersModelArrayListAll.size();i++){
                    if(dealersModelArrayListAll.get(i).getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(dealersModelArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dealersModelArrayList.clear();
            dealersModelArrayList.addAll((Collection<? extends DealersModel>) results.values);
            notifyDataSetChanged();
        }
    };

}
