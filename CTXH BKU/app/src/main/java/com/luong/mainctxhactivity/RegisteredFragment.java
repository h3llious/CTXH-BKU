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
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;

public class RegisteredFragment extends Fragment {
    final String TAG = "RegisteredFragment";

    View view;
    Context context;
    ArrayList<CtxhItem> ctxhReList;

    CtxhAdapter ctxhAdapter;

    FirebaseFirestore db;
    private FirebaseUser user;

    public RegisteredFragment() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registered, container, false);

        ctxhReList = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        //getDatabase();
        initDatabase();

        initView();

        return view;
    }

    public void initView(){
        RecyclerView recyclerView = view.findViewById(R.id.registered_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ctxhAdapter = new CtxhAdapter(ctxhReList, context);
        recyclerView.setAdapter(ctxhAdapter);
    }

    public void updateReList(String idctxh, String type) {
        int idx = -1;
        if (type != "add") {
            for (int i = 0; i < ctxhReList.size(); i++) {
                if (idctxh.equals(ctxhReList.get(i).getId())) {
                    idx = i;
                    break;
                }
            }
        }
        switch (type) {
            case "delete":
                if (idx >= 0) {
                    ctxhReList.remove(idx);
                    ctxhAdapter.notifyItemRemoved(idx);
                }
                break;
            case "update":
                if (idx >= 0) {
                    getCtxh(idx, idctxh, type);
                } else {
                    getCtxh(idx, idctxh, "add");
                }
                break;
            case "add":
                getCtxh(idx, idctxh, type);
                break;

        }
    }

    public void initDatabase() {
        db = FirebaseFirestore.getInstance();
        db.collection("registration")
                .whereEqualTo("id_user", user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Update_data_fall", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            String idctxh = dc.getDocument().getString("id_ctxh");
                            switch (dc.getType()) {
                                case ADDED:
                                    updateReList(idctxh, "add");
                                    break;
                                case MODIFIED:
                                    updateReList(idctxh, "update");
                                    break;
                                case REMOVED:
                                    updateReList(idctxh, "delete");
                                    break;
                            }
                        }
                    }
                });
    }

    private void getCtxh(final int idx, final String id, final String typeReTable) {
        db.collection("ctxh").document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, " data: " + snapshot.getData());

                            String title = snapshot.getString("title");
                            Timestamp deadline = snapshot.getTimestamp("deadline_register");
                            String img = snapshot.getString("image");
                            Double ctxh_day = snapshot.getDouble("maximum_ctxh_day");
                            Timestamp time_start = snapshot.getTimestamp("time_start");
                            Timestamp time_end = snapshot.getTimestamp("time_end");

                            CtxhItem item = new CtxhItem(snapshot.getId(), img, title, deadline, time_start, time_end, ctxh_day);

                            updateUI(idx, item, typeReTable);

                        } else {
                            Log.d(TAG, " data: null");
                        }

                    }
                });
    }

    public void updateUI(int idx, CtxhItem item, String typeReTablee) {
        // DO something stupid
        switch (typeReTablee){
            case "add":
                ctxhReList.add(item);
                ctxhAdapter.notifyItemInserted(ctxhReList.size() - 1);
                break;
            case "update":
                ctxhReList.get(idx).update(item);
                ctxhAdapter.notifyItemChanged(idx);
        }
    }
}
