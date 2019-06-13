package com.luong.mainctxhactivity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentsRegisteredActivity extends AppCompatActivity {
    RecyclerView listStudentView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_registered);

        final String docID = getIntent().getStringExtra("docId");

        db = FirebaseFirestore.getInstance();

        listStudentView = findViewById(R.id.listStudent);

        final ArrayList<DetailItem> listItem = new ArrayList<>();

//        CollectionReference cref1 = db.collection("registration");
//        Query q2 = cref1.whereEqualTo("id_ctxh", docID);
//        q2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                for (DocumentSnapshot document : queryDocumentSnapshots) {
//                    String uidCheck = document.getString("id_user");
//                    String didCheck = document.getString("id_ctxh");
//                    Toast.makeText(getBaseContext(), uidCheck + " : "+didCheck, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        CollectionReference cref = db.collection("registration");
        final Query q1 = cref.whereEqualTo("id_ctxh", docID);
        q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listItem.clear();
                //if (task.isSuccessful())
                //listItem.add(new DetailItem("16ff11237", "Hello"));
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String uid = document.getString("id_user");

                        //listItem.add(new DetailItem("16ff11237", "Hello"));
                        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    String StudentId = document.get("mssv").toString();
                                    String StudentName = document.get("lastname").toString() +" "+ document.get("firstname").toString();
                                    //Toast.makeText(getBaseContext(), StudentId + " : "+StudentName, Toast.LENGTH_SHORT).show();
                                    listItem.add(new DetailItem(StudentId, StudentName));


                                    DetailAdapter adapter = new DetailAdapter(listItem);
                                    listStudentView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                                    listStudentView.setAdapter(adapter);
                                }

                            }
                        });

                }

            }
        });




    }
}
