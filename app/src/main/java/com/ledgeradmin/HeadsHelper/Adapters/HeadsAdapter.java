package com.ledgeradmin.HeadsHelper.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ledgeradmin.CompaniesHelper.ConfirmationDialog;
import com.ledgeradmin.HeadsHelper.AddHeadFragment;
import com.ledgeradmin.HeadsHelper.HeadsFragment;
import com.ledgeradmin.HeadsHelper.Models.CompaniesModel;
import com.ledgeradmin.HeadsHelper.Models.HeadsModel;
import com.ledgeradmin.HeadsHelper.ViewHolder.HeadsViewHolder;
import com.ledgeradmin.ImageUploadActivity;
import com.ledgeradmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HeadsAdapter extends RecyclerView.Adapter<HeadsViewHolder> {

    Context context;
    ArrayList<HeadsModel> headsModelArrayList;
    ArrayList<CompaniesModel> companiesModelArrayList = new ArrayList<>();

    public HeadsAdapter(Context context, ArrayList<HeadsModel> headsModelArrayList) {
        this.context = context;
        this.headsModelArrayList = headsModelArrayList;
    }

    @NonNull
    @Override
    public HeadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_head,parent,false);
        return new HeadsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadsViewHolder holder, final int position) {

        holder.head.setText(headsModelArrayList.get(position).getName());


        if(headsModelArrayList.get(position).getImage()!=null){
            Picasso.get().load(headsModelArrayList.get(position).getImage()).into(holder.icon);
        }
        else {
            holder.icon.setImageResource(R.drawable.ic_sales);
        }

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ImageUploadActivity.class);
                intent.putExtra("type","head");
                intent.putExtra("id",headsModelArrayList.get(position).getId());
                v.getContext().startActivity(intent);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                FirebaseFirestore db;
                db = FirebaseFirestore.getInstance();

                db.collection("Heads").document(headsModelArrayList.get(position).getId())
                        .collection("companies").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        companiesModelArrayList.clear();
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            CompaniesModel companiesModel = new CompaniesModel();
                            companiesModel.setId(documentSnapshot.getString("id"));
                            companiesModel.setName(documentSnapshot.getString("name"));

                            companiesModelArrayList.add(companiesModel);
                        }

                        AddHeadFragment addHeadFragment = new AddHeadFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("name", headsModelArrayList.get(position).getName());
                        bundle.putString("email", headsModelArrayList.get(position).getEmail());
                        bundle.putString("password", headsModelArrayList.get(position).getPassword());
                        bundle.putString("phone", headsModelArrayList.get(position).getPhone());
                        bundle.putString("address", headsModelArrayList.get(position).getAddress());
                        bundle.putSerializable("companies", companiesModelArrayList);
                        bundle.putString("id",headsModelArrayList.get(position).getId());

                        addHeadFragment.setArguments(bundle);

                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_frame, addHeadFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }
                });

            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete head")
                        .setMessage("Are you sure you want to delete the head ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(v.getContext())
                                        .setTitle("Delete head Confirmation")
                                        .setMessage("Confirm deletion?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                AppCompatActivity activity = (AppCompatActivity) v.getContext();

                                                ConfirmationDialog confirmationDialog = new ConfirmationDialog("head",null,null,null,null,headsModelArrayList.get(position).getId());
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
        return headsModelArrayList.size();
    }
}
