package com.adminsyndicate.DealersHelper;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adminsyndicate.R;

public class DealersViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    ImageView image;
    CardView dealer;
    ImageButton edit;
    ImageButton delete;

    public DealersViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.sales);
        image = itemView.findViewById(R.id.imageView2);
        dealer = itemView.findViewById(R.id.dealer);

        edit = itemView.findViewById(R.id.edit);
        delete = itemView.findViewById(R.id.delete);

    }
}
