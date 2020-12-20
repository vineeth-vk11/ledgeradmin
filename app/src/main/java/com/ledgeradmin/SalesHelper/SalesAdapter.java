package com.ledgeradmin.SalesHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.ledgeradmin.CompaniesHelper.AddCompanyDialog;
import com.ledgeradmin.CompaniesHelper.ConfirmationDialog;
import com.ledgeradmin.DealersHelper.DealersFragment;
import com.ledgeradmin.ImageUploadActivity;
import com.ledgeradmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesViewHolder> implements Filterable {

    Context context;
    ArrayList<SalesModel> salesModelArrayList;
    ArrayList<SalesModel> salesModelArrayListAll;
    String company;

    public SalesAdapter(Context context, ArrayList<SalesModel> salesModelArrayList, String company) {
        this.context = context;
        this.salesModelArrayList = salesModelArrayList;
        this.salesModelArrayListAll = new ArrayList<>(salesModelArrayList);
        this.company = company;
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_sales, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, final int position) {

        final String name = salesModelArrayList.get(position).getName();
        final String email = salesModelArrayList.get(position).getEmail();
        final String password = salesModelArrayList.get(position).getPassword();
        final String phoneNumber = salesModelArrayList.get(position).getPhoneNumber();
        final String address = salesModelArrayList.get(position).getAddress();
        final String company = salesModelArrayList.get(position).getCompany();
        final String id = salesModelArrayList.get(position).getId();
        final String salesTarget = salesModelArrayList.get(position).getSalesTarget();

        holder.name.setText(salesModelArrayList.get(position).getName());

        if(salesModelArrayList.get(position).getImage() != null){
            Picasso.get().load(salesModelArrayList.get(position).getImage()).into(holder.image);
        }
        else {
            holder.image.setImageResource(R.drawable.ic_sales);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ImageUploadActivity.class);
                intent.putExtra("type","sales");
                intent.putExtra("company",company);
                intent.putExtra("id",id);
                v.getContext().startActivity(intent);
            }
        });
        holder.sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DealersFragment dealersFragment = new DealersFragment();

                Bundle bundle = new Bundle();
                bundle.putString("userId", salesModelArrayList.get(position).getId());
                bundle.putString("company",company);
                bundle.putString("name",salesModelArrayList.get(position).getName());
                dealersFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,dealersFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                AddSalesDialog addSalesDialog = new AddSalesDialog(name,email,password,phoneNumber,address,id,company,salesTarget);
                addSalesDialog.show(activity.getSupportFragmentManager(), "Add Sales Dialog");
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete sales person")
                        .setMessage("Are you sure you want to delete the sales person?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(v.getContext())
                                        .setTitle("Delete sales person Confirmation")
                                        .setMessage("Confirm deletion?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                                                ConfirmationDialog confirmationDialog = new ConfirmationDialog("sales",company,id,null,null,null);
                                                confirmationDialog.show(activity.getSupportFragmentManager(), "Confirmation Dialog");

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
        return salesModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<SalesModel> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(salesModelArrayListAll);
            }
            else {
                for(int i = 0; i<salesModelArrayListAll.size();i++){
                    if(salesModelArrayListAll.get(i).getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(salesModelArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            salesModelArrayList.clear();
            salesModelArrayList.addAll((Collection<? extends SalesModel>) results.values);
            notifyDataSetChanged();
        }
    };
}
