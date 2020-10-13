package com.adminsyndicate.HeadsHelper.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adminsyndicate.R;

public class SelectCompaniesViewHolder extends RecyclerView.ViewHolder {

    public TextView companyName;
    public CheckBox isAccessesible;

    public SelectCompaniesViewHolder(@NonNull View itemView) {
        super(itemView);

        companyName = itemView.findViewById(R.id.companyName);
        isAccessesible = itemView.findViewById(R.id.checkbox);

    }
}
