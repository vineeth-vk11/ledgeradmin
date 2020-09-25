package com.ledgeradmin.HeadsHelper.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ledgeradmin.HeadsHelper.Models.CompaniesModel;
import com.ledgeradmin.HeadsHelper.ViewHolder.SelectCompaniesViewHolder;
import com.ledgeradmin.R;

import java.util.ArrayList;

public class CompaniesAdapter extends RecyclerView.Adapter<SelectCompaniesViewHolder> {

    Context context;
    ArrayList<CompaniesModel> companiesModelArrayList;
    ArrayList<CompaniesModel> selectedCompanies;
    ArrayList<CompaniesModel> existingCompanies;

    public CompaniesAdapter(Context context, ArrayList<CompaniesModel> companiesModelArrayList, ArrayList<CompaniesModel> selectedCompanies, ArrayList<CompaniesModel> existingCompanies) {
        this.context = context;
        this.companiesModelArrayList = companiesModelArrayList;
        this.selectedCompanies = selectedCompanies;
        this.existingCompanies = existingCompanies;
    }

    @NonNull
    @Override
    public SelectCompaniesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_select_company,parent,false);
        return new SelectCompaniesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectCompaniesViewHolder holder, final int position) {
        holder.companyName.setText(companiesModelArrayList.get(position).getName());

        for(int i =0; i<existingCompanies.size();i++){
            if(existingCompanies.get(i).getId().equals(companiesModelArrayList.get(position).getId())){
                holder.isAccessesible.setChecked(true);
                selectedCompanies.add(companiesModelArrayList.get(position));
            }
        }
        holder.isAccessesible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectedCompanies.add(companiesModelArrayList.get(position));
                }
                else {
                    selectedCompanies.remove(companiesModelArrayList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return companiesModelArrayList.size();
    }
}
