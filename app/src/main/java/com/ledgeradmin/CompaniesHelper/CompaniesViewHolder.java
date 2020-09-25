package com.ledgeradmin.CompaniesHelper;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ledgeradmin.R;

public class CompaniesViewHolder extends RecyclerView.ViewHolder {

    TextView companyName;
    CardView company;
    ImageView image;
    ImageButton edit;
    ImageButton delete;

    public CompaniesViewHolder(@NonNull View itemView) {
        super(itemView);

        companyName = itemView.findViewById(R.id.companyName);
        company = itemView.findViewById(R.id.company);
        image = itemView.findViewById(R.id.imageView2);

        edit = itemView.findViewById(R.id.edit);
        delete = itemView.findViewById(R.id.delete);
    }
}
