package com.luong.mainctxhactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DetailFragment extends Fragment {
    CtxhItem item;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    View view;

    ToggleButton toggle;
    FloatingActionButton listRegistered;

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
    TextView curReg;

    RecyclerView commentListView;
    ArrayList<CmtItem> commentListItems;
    EditText editComment;
    Button buttonComment;

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
        curReg = view.findViewById(R.id.num2);
        listRegistered = view.findViewById(R.id.listRegistered);

        listRegistered.hide();

        commentListItems = new ArrayList<>();
        commentListView = view.findViewById(R.id.commentListRecyclerView);
        commentListView.setLayoutManager(new LinearLayoutManager(getContext()));

        editComment = view.findViewById(R.id.edit_comment);
        buttonComment = view.findViewById(R.id.button_comment);


        db = FirebaseFirestore.getInstance();


        CollectionReference cref = db.collection("registration");
        Query q1 = cref.whereEqualTo("id_user", userID).whereEqualTo("id_ctxh", docID);
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

        DocumentReference userRef = db.collection("users").document(userID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Boolean admin = (Boolean) document.get("admin");
                    if (admin) {
                        toggle.setVisibility(View.GONE);
                        listRegistered.show();
                    }
                }
            }
        });

        //if (listRegistered.isShown()){
            listRegistered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StudentsRegisteredActivity.class);
                    intent.putExtra("docId", docID);
                    startActivity(intent);
                }
            });
        //}


        updateCurrentReg(docID);

        final DocumentReference docRef = db.collection("ctxh").document(docID);
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

        updateCmtList(docRef);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curRegNum = Integer.parseInt(curReg.getText().toString());
                int maxRegNum = Integer.parseInt(maxReg.getText().toString());

                if (toggle.isChecked()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("ctxh_day_addto_user", Double.parseDouble(social_day.getText().toString()));
                    data.put("id_ctxh", docID);
                    data.put("id_user", userID);



                    if (curRegNum == maxRegNum) {
                        Toast.makeText(getContext(), "Hoạt động đã đủ người", Toast.LENGTH_SHORT).show();
                        toggle.setChecked(false);
                        return;
                    }

                    curReg.setText(String.valueOf(curRegNum+1));

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
                    updateCurrentReg(docID);
                } else {

                    //just a dummy number
                    curReg.setText(String.valueOf(curRegNum-1));

                    CollectionReference cref = db.collection("registration");
                    final Query q1 = cref.whereEqualTo("id_user", userID).whereEqualTo("id_ctxh", docID);
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

                                    //change curReg
                                    updateCurrentReg(docID);
                                }

                                return;
                            }

                        }
                    });


                }
            }
        });

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    return;
                }
                String id_user = currentUser.getUid();
                String comment = editComment.getText().toString();
                Timestamp time = getCurrentTimestamp();
                HashMap<String, Object> item = new HashMap<>();
                item.put("id_user", id_user);
                item.put("comment", comment);
                item.put("time", time);
                docRef.collection("comments").add(item)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                editComment.setText("");
                                updateCmtList(docRef);
                            }
                        });
            }
        });

        return view;
    }

    public void updateCurrentReg(final String docID) {
        CollectionReference cref = db.collection("registration");
        Query q1 = cref.whereEqualTo("id_ctxh", docID);
        q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int num = 0;
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String didCheck = document.getString("id_ctxh");
                    if (didCheck.equals(docID)) {
                        num++;
                    }
                }
                curReg.setText(String.valueOf(num));
                Log.d("why not show", "curReg" + curReg.getText());
            }
        });
    }

    // Get comment from comments collection and full name from users collection
    private void updateCmtList(DocumentReference documentReference) {
        commentListItems.clear();
        documentReference.collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot cmt : task.getResult()) {
                                String idUser = (String) cmt.get("id_user");

                                final String[] commentName = new String[1];
                                final Timestamp commentTime = (Timestamp) cmt.get("time");
                                final String commentContent = (String) cmt.get("comment");

                                if (idUser != null) {
                                    db.collection("users")
                                            .document(idUser)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        commentName[0] = task.getResult().get("firstname")
                                                                + " "
                                                                + task.getResult().get("lastname");
                                                        commentListItems.add(new CmtItem(commentName[0], commentTime, commentContent));
                                                        updateCmtAdapter();
                                                    } else {
                                                        Log.d("Comment", "failed getting full user name ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d("Comment", "failed getting comment reference ", task.getException());
                        }
                    }

                });
    }


    // private class SocialWorkInfoTask extends AsyncTask<String, Void, DocumentSnapshot>

    private void updateCmtAdapter() {
        commentListItems.sort(new Comparator<CmtItem>() {
            @Override
            public int compare(CmtItem o1, CmtItem o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        CmtAdapter cmtAdapter = new CmtAdapter(commentListItems);
        commentListView.setAdapter(cmtAdapter);
    }

    private Timestamp getCurrentTimestamp() {
        return new Timestamp(Calendar.getInstance(TimeZone.getDefault()).getTime());
    }

}
