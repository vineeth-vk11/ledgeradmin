package com.ledgeradmin.TransactionsHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ledgeradmin.R;
import com.ledgeradmin.SalesHelper.AddSalesDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsViewHolder> implements Filterable {

    Context context;
    ArrayList<TransactionsModel> transactionsModelArrayList;
    ArrayList<TransactionsModel> transactionsModelArrayListAll;

    public TransactionsAdapter(Context context, ArrayList<TransactionsModel> transactionsModelArrayList) {
        this.context = context;
        this.transactionsModelArrayList = transactionsModelArrayList;
        this.transactionsModelArrayListAll = new ArrayList<>(transactionsModelArrayList);
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_transactions, parent, false);
        return new TransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {

        final String company = transactionsModelArrayList.get(position).getCompany();
        final String sales = transactionsModelArrayList.get(position).getSales();
        final String dealer = transactionsModelArrayList.get(position).getDealer();
        final String id = transactionsModelArrayList.get(position).getId();
        final String date = transactionsModelArrayList.get(position).getDate();
        final String particular = transactionsModelArrayList.get(position).getParticular();
        final String type = transactionsModelArrayList.get(position).getType();
        final String amount = transactionsModelArrayList.get(position).getAmount();
        final String voucher = transactionsModelArrayList.get(position).getVoucher();

        holder.date.setText(transactionsModelArrayList.get(position).getDate());
        holder.particular.setText(transactionsModelArrayList.get(position).getParticular());
        holder.debit.setText(transactionsModelArrayList.get(position).getDebit());
        holder.credit.setText(transactionsModelArrayList.get(position).getCredit());

        holder.transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Edit or Delete")
                        .setMessage("What do you wish to do?")
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                AddTransactionDialog addTransactionDialog = new AddTransactionDialog(company,sales,dealer,id,particular,amount,date,type,voucher);
                                addTransactionDialog.show(activity.getSupportFragmentManager(), "Add Sales Dialog");
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new AlertDialog.Builder(v.getContext())
                                        .setTitle("Delete transaction")
                                        .setMessage("Are you sure you want to delete the transaction?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new AlertDialog.Builder(v.getContext())
                                                        .setTitle("Delete transaction Confirmation")
                                                        .setMessage("Confirm deletion?")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                FirebaseFirestore db;
                                                                db = FirebaseFirestore.getInstance();

                                                                db.collection("Companies").document(company).collection("sales")
                                                                        .document(sales).collection("dealers").document(dealer)
                                                                        .collection("transactions").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionsModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<TransactionsModel> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(transactionsModelArrayListAll);
            }
            else {
                for(int i = 0; i<transactionsModelArrayListAll.size();i++){
                    if(transactionsModelArrayListAll.get(i).getDate().toLowerCase().contains(constraint.toString().toLowerCase())
                            || transactionsModelArrayListAll.get(i).getCredit().toLowerCase().contains(constraint.toString().toLowerCase())
                            || transactionsModelArrayListAll.get(i).getDebit().toLowerCase().contains(constraint.toString().toLowerCase())
                            || transactionsModelArrayListAll.get(i).getParticular().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(transactionsModelArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            transactionsModelArrayList.clear();
            transactionsModelArrayList.addAll((Collection<? extends TransactionsModel>) results.values);
            notifyDataSetChanged();
        }
    };

}
