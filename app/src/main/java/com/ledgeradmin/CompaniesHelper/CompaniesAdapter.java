package com.ledgeradmin.CompaniesHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

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
import com.ledgeradmin.SalesHelper.SalesFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesViewHolder> implements Filterable {

    Context context;
    ArrayList<CompaniesModels>companiesModelsArrayList;
    ArrayList<CompaniesModels> companiesModelsArrayListAll;


    public CompaniesAdapter(Context context, ArrayList<CompaniesModels> companiesModelsArrayList) {
        this.context = context;
        this.companiesModelsArrayList = companiesModelsArrayList;
        this.companiesModelsArrayListAll = new ArrayList<>(companiesModelsArrayList);
    }

    @NonNull
    @Override
    public CompaniesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_company, parent, false);
        return new CompaniesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompaniesViewHolder holder, final int position) {

        final String companyName = companiesModelsArrayList.get(position).getCompanyName();
        final String companyId = companiesModelsArrayList.get(position).getCompanyId();

        holder.companyName.setText(companyName);
        holder.image.setImageResource(R.drawable.ic_building_office);

        holder.company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SalesFragment salesFragment = new SalesFragment();

                Bundle bundle = new Bundle();
                bundle.putString("company",companyId);
                salesFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,salesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                AddCompanyDialog addCompanyDialog = new AddCompanyDialog(companyName, companyId);
                addCompanyDialog.show(activity.getSupportFragmentManager(), "Add Company Dialog");
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Company")
                        .setMessage("Are you sure you want to delete the company?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(v.getContext())
                                        .setTitle("Delete Company Confirmation")
                                        .setMessage("Confirm deletion?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseFirestore db;
                                                db = FirebaseFirestore.getInstance();

                                                db.collection("Companies").document(companyId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(v.getContext(), "Company deleted successfully", Toast.LENGTH_SHORT).show();
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
        return companiesModelsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<CompaniesModels> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(companiesModelsArrayListAll);
            }
            else {
                for(int i = 0; i<companiesModelsArrayListAll.size();i++){
                    if(companiesModelsArrayListAll.get(i).getCompanyName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(companiesModelsArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            companiesModelsArrayList.clear();
            companiesModelsArrayList.addAll((Collection<? extends CompaniesModels>) results.values);
            notifyDataSetChanged();
        }
    };

}
