package com.adminsyndicate.HeadsHelper.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adminsyndicate.R;

public class HeadsViewHolder extends RecyclerView.ViewHolder {

    public ImageView icon;
    public TextView head;
    public ImageButton edit;
    public ImageButton delete;

    public HeadsViewHolder(@NonNull View itemView) {
        super(itemView);

        icon = itemView.findViewById(R.id.imageView2);
        head = itemView.findViewById(R.id.headName);
        edit = itemView.findViewById(R.id.edit);
        delete = itemView.findViewById(R.id.delete);

    }
}
