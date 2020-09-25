package com.ledgeradmin.HeadsHelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.ledgeradmin.HeadsHelper.Adapters.HeadsAdapter;
import com.ledgeradmin.HeadsHelper.Models.HeadsModel;
import com.ledgeradmin.R;

import java.util.ArrayList;

public class HeadsFragment extends Fragment {

    FloatingActionButton addHead;
    RecyclerView heads;
    FirebaseFirestore db;
    ArrayList<HeadsModel> headsModelArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_heads, container, false);

        heads = view.findViewById(R.id.headRecycler);
        heads.setLayoutManager(new LinearLayoutManager(getContext()));
        heads.setHasFixedSize(true);

        db = FirebaseFirestore.getInstance();

        headsModelArrayList = new ArrayList<>();

        addHead = view.findViewById(R.id.add_head);
        addHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddHeadFragment addHeadFragment = new AddHeadFragment();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, addHeadFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        db.collection("Heads").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getHeads();
            }
        });

        return view;
    }

    private void getHeads(){
        db.collection("Heads").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot:task.getResult()){
                    HeadsModel headsModel = new HeadsModel();
                    headsModel.setName(documentSnapshot.getString("name"));
                    headsModel.setAddress(documentSnapshot.getString("address"));
                    headsModel.setEmail(documentSnapshot.getString("email"));
                    headsModel.setPassword(documentSnapshot.getString("password"));
                    headsModel.setId(documentSnapshot.getId());
                    headsModel.setPhone(documentSnapshot.getString("phone"));

                    headsModelArrayList.add(headsModel);
                }

                HeadsAdapter headsAdapter = new HeadsAdapter(getContext(), headsModelArrayList);
                heads.setAdapter(headsAdapter);


            }
        });
    }
}