package com.luong.mainctxhactivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    View view;
    Context context;
    ArrayList<CtxhItem> ctxhList;

    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CloundFireStoreHelper cldb;

    CtxhAdapter ctxhAdapter;

    public NotificationFragment() {
        cldb = new CloundFireStoreHelper();
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        ctxhList = new ArrayList<>();

        initView();
        return view;
    }

    public void initView(){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_home);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // getFirebase();
        //ctxhList = cldb.getListCtxh();
        cldb.setContext(context);

        Log.i("sizectxh", Integer.toString(ctxhList.size()));

        ctxhAdapter = new CtxhAdapter(ctxhList, context);
        recyclerView.setAdapter(ctxhAdapter);
    }

    public void showId() {
        if (user != null) {
            //Toast.makeText(context, "User Id: " + user.getUid(), Toast.LENGTH_LONG).show();
        }
        else  {
            //Toast.makeText(context, "User Id: " + "None", Toast.LENGTH_LONG).show();
        }

        db.collection("registration")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Toast.makeText(context, "document ID: "  + document.getData(), Toast.LENGTH_SHORT).show();
                                Log.i("data registration", document.getData().toString());
                            }
                        } else {
                            Toast.makeText(context, "Error getting documents.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
