package com.luong.mainctxhactivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CloundFireStoreHelper {

    ArrayList<CtxhItem> ctxhItems;

    Context context;

// ...
// Initialize Firebase Auth

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;

    public CloundFireStoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void getListCtxh(final CtxhAdapter ctxhAdapter, final ArrayList<CtxhItem> ctxhItems) {
        db.collection("ctxh")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            Timestamp deadline = document.getTimestamp("deadline_register");
                            String img = document.getString("image");
                            Double ctxh_day = document.getDouble("maximum_ctxh_day");
                            Timestamp time_start = document.getTimestamp("time_start");
                            Timestamp time_end = document.getTimestamp("time_end");
                            updateUI(ctxhAdapter, ctxhItems, new CtxhItem(document.getId(), img, title, deadline, time_start, time_end, ctxh_day));

                            Log.d("TAG", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Toast.makeText(context, "Cant get data", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    private void updateUI(CtxhAdapter ctxhAdapter, ArrayList<CtxhItem> ctxhItems, CtxhItem item) {

    }
}
