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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PostedFragment extends Fragment {
    View view;
    Context context;
    ArrayList<CtxhItem> ctxhList;

    CtxhAdapter ctxhAdapter;

    FirebaseFirestore db;

    private String id_faculty;

    public PostedFragment() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        ctxhList = new ArrayList<>();

        initDatabase();

        initView();

        return view;
    }

    public void initView(){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ctxhAdapter = new CtxhAdapter(ctxhList, context);
        recyclerView.setAdapter(ctxhAdapter);
    }

    public void updateUI(CtxhItem item, String type) {
        int idx = -1;
        if (type != "add") {
            for (int i = 0; i < ctxhList.size(); i++) {
                if (item.getId().equals(ctxhList.get(i).getId())) {
                    idx = i;
                    break;
                }
            }
        }
        switch (type) {
            case "delete":
                if (idx >= 0) {
                    ctxhList.remove(idx);
                    ctxhAdapter.notifyItemRemoved(idx);
                }
                break;
            case "update":
                if (idx >= 0) {
                    ctxhList.get(idx).update(item);
                    ctxhAdapter.notifyItemChanged(idx);
                } else {
                    ctxhList.add(item);
                    ctxhAdapter.notifyItemInserted(ctxhList.size() - 1);
                }
                break;
            case "add":
                ctxhList.add(item);
                ctxhAdapter.notifyItemInserted(ctxhList.size() - 1);
                break;

        }

    }

    public void initDatabase() {
        db = FirebaseFirestore.getInstance();

        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            id_faculty = task.getResult().get("id_faculty").toString();
                            getCtxhPosted();
                        }
                    }
                });
    }

    private void getCtxhPosted() {
        db.collection("ctxh")
                .whereEqualTo("id_faculty", id_faculty)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Update_data_fall", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                            String title = document.getDocument().getString("title");
                            Timestamp deadline = document.getDocument().getTimestamp("deadline_register");
                            Log.i("deadline", deadline.toString());
                            String img = document.getDocument().getString("image");
                            Double ctxh_day = document.getDocument().getDouble("maximum_ctxh_day");
                            Timestamp time_start = document.getDocument().getTimestamp("time_start");
                            Timestamp time_end = document.getDocument().getTimestamp("time_end");

                            CtxhItem item = new CtxhItem(document.getDocument().getId(), img, title, deadline, time_start, time_end, ctxh_day);

                            switch (document.getType()) {
                                case ADDED:
                                    updateUI(item, "add");
                                    break;
                                case MODIFIED:
                                    updateUI(item, "update");
                                    break;
                                case REMOVED:
                                    updateUI(item, "delete");
                                    break;
                            }

                        }
                    }
                });
    }

}
