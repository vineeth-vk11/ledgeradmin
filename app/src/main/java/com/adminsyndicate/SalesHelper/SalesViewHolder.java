package com.adminsyndicate.SalesHelper;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adminsyndicate.R;

public class SalesViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    CardView sales;
    ImageView image;
    ImageButton edit;
    ImageButton delete;

    public SalesViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.salesName);
        sales = itemView.findViewById(R.id.sales);
        image = itemView.findViewById(R.id.imageView2);

        edit = itemView.findViewById(R.id.edit);
        delete = itemView.findViewById(R.id.delete);

    }
}
