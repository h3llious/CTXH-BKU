package com.luong.mainctxhactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class DetailFragment extends Fragment {
    CtxhItem item;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    View view;

    ToggleButton toggle;

    ImageView img;
    TextView deadline;
    TextView title;
    TextView start;
    TextView end;
    TextView social_day;
    TextView faculty;
    TextView location;
    TextView maxReg;
    TextView desc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        final String docID = getArguments().getString("docId");
        final String userID = user.getUid();

        toggle = view.findViewById(R.id.registerButton);

        img = view.findViewById(R.id.imageView);
        deadline = view.findViewById(R.id.deadline_detail);
        title = view.findViewById(R.id.title_detail);
        start = view.findViewById(R.id.time_start_detail);
        end = view.findViewById(R.id.time_end_detail);
        social_day = view.findViewById(R.id.social_day);
        faculty = view.findViewById(R.id.faculty);
        location = view.findViewById(R.id.location2);
        maxReg = view.findViewById(R.id.maxNum);
        desc = view.findViewById(R.id.description2);

        db = FirebaseFirestore.getInstance();



        CollectionReference cref=db.collection("registration");
        Query q1=cref.whereEqualTo("id_user",userID).whereEqualTo("id_ctxh",docID);
        q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String uidCheck = document.getString("id_user");
                    String didCheck = document.getString("id_ctxh");
                    if (uidCheck.equals(userID) && didCheck.equals(docID)) {
                        toggle.setChecked(true);
                        return;
                    }
                }
                toggle.setChecked(false);
            }
        });



//        db.collection("registration").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("Update_data_fall", "Listen failed.", e);
//                    return;
//                }
//
//                for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
//                    String uidCheck = document.getDocument().getString("id_user");
//                    String didCheck = document.getDocument().getString("id_ctxh");
//                    if (uidCheck.equals(userID) && didCheck.equals(docID)) {
//                        toggle.setChecked(true);
//                        return;
//                    }
//                }
//                toggle.setChecked(false);
//            }
//        });

        DocumentReference docRef = db.collection("ctxh").document(docID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String facultyNum = document.get("id_faculty").toString();

                    DocumentReference docFacultyName = db.collection("faculty").document(facultyNum);
                    docFacultyName.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot docFaculty = task.getResult();
                                if (docFaculty.exists()) {
                                    faculty.setText(docFaculty.get("name").toString());
                                    ;
                                }
                            }
                        }
                    });


                    if (document.exists()) {
                        Log.d("SocialWork", "DocumentSnapshot data: " + document.getData());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, dd-MM-yyyy");

                        title.setText(document.get("title").toString());
                        Picasso.get().load(document.get("image").toString()).into(img);
                        deadline.setText(dateFormat.format(document.getTimestamp("deadline_register").toDate()));
                        start.setText(dateFormat.format(document.getTimestamp("time_start").toDate()));
                        end.setText(dateFormat.format(document.getTimestamp("time_end").toDate()));
                        social_day.setText(document.get("maximum_ctxh_day").toString());
                        location.setText(document.get("location").toString());
                        maxReg.setText(document.get("maximum_register").toString());
                        desc.setText(document.get("description").toString());
                    } else {
                        Log.d("SocialWork", "No such document");
                    }
                } else {
                    Log.d("SocialWork", "get failed with ", task.getException());
                }
            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle.isChecked()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("ctxh_day_addto_user", social_day.getText());
                    data.put("id_ctxh", docID);
                    data.put("id_user", userID);

                    db.collection("registration")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("addData", "DocumentSnapshot written with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("addData", "Error adding document", e);
                                }
                            });
                }
                else {
                    CollectionReference cref=db.collection("registration");
                    Query q1=cref.whereEqualTo("id_user",userID).whereEqualTo("id_ctxh",docID);
                    q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                String uidCheck = document.getString("id_user");
                                String didCheck = document.getString("id_ctxh");
                                if (uidCheck.equals(userID) && didCheck.equals(docID)) {
                                    db.collection("registration").document(document.getId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("deleteDoc", "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("deleteDoc", "Error deleting document", e);
                                                }
                                            });
                                }
                                return;
                            }

                        }
                    });




//                    db.collection("registration").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                            if (e != null) {
//                                Log.w("Update_data_fall", "Listen failed.", e);
//                                return;
//                            }
//
//                            for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
//                                String uidCheck = document.getDocument().getString("id_user");
//                                String didCheck = document.getDocument().getString("id_ctxh");
//                                if (uidCheck.equals(userID) && didCheck.equals(docID)) {
//                                    db.collection("registration").document(document.getDocument().getId())
//                                            .delete()
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Log.d("deleteDoc", "DocumentSnapshot successfully deleted!");
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Log.w("deleteDoc", "Error deleting document", e);
//                                                }
//                                            });
//                                }
//                                return;
//                            }
//                        }
//                    });
                }
            }
        });

//        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("ctxh_day_addto_user", social_day.getText());
//                    data.put("id_ctxh", docID);
//                    data.put("id_user", userID);
//
//                    db.collection("registration")
//                            .add(data)
//                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                @Override
//                                public void onSuccess(DocumentReference documentReference) {
//                                    Log.d("addData", "DocumentSnapshot written with ID: " + documentReference.getId());
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w("addData", "Error adding document", e);
//                                }
//                            });
//                }
//                else {
//                    db.collection("registration").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                            if (e != null) {
//                                Log.w("Update_data_fall", "Listen failed.", e);
//                                return;
//                            }
//
//                            for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
//                                String uidCheck = document.getDocument().getString("id_user");
//                                String didCheck = document.getDocument().getString("id_ctxh");
//                                if (uidCheck.equals(userID) && didCheck.equals(docID)) {
//                                    db.collection("registration").document(document.getDocument().getId())
//                                            .delete()
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Log.d("deleteDoc", "DocumentSnapshot successfully deleted!");
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Log.w("deleteDoc", "Error deleting document", e);
//                                                }
//                                            });
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//        });


        return view;
    }

//    public void checkRegistered(FirebaseFirestore db, final String docID, final String userID, ToggleButton toggle){
//        CollectionReference cref=db.collection("registration");
//        Query q1=cref.whereEqualTo("id_user",userID).whereEqualTo("id_ctxh",docID);
//        q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//            }
//        })
//
//        db.collection("registration").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("Update_data_fall", "Listen failed.", e);
//                    return;
//                }
//
//                for (DocumentChange document: queryDocumentSnapshots.getDocumentChanges()){
//                    String uidCheck = document.getDocument().getString("id_user");
//                    String didCheck = document.getDocument().getString("id_ctxh");
//                    if (uidCheck.equals(userID) && didCheck.equals(docID)){
//                        updateToggle(toggle, false);
//                    }
//                }
//                toggle.setChecked(false);
//            }
//        });
//
//    }
//
//    public void updateToggle(ToggleButton toggle, boolean isChecked){
//
//    }


}
