package com.luong.mainctxhactivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class AddFragment extends Fragment {

    final static int REQUEST_CODE_CHOOSE_IMAGE = 1;

    View view;
    EditText inputName;
    EditText inputDescription;
    EditText inputLocation;
    EditText inputStartTime;
    EditText inputDue;
    EditText inputDeadline;
    EditText inputDays;
    EditText inputMaxReg;
    Button buttonPost;
    Button buttonChoose;
    TextView imgName;
    Spinner spinnerFaculty;

    Timestamp start;
    Timestamp due;
    Timestamp deadline;
    String name;
    String description;
    String location;
    String faculty;
    String imgFireURL;
    Uri imgLocalURI;
    HashMap<String, String> mapFaculty;
    double days;
    int maxReg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_ctxh, container, false);

        inputName = view.findViewById(R.id.editTextName);
        inputDescription = view.findViewById(R.id.editTextDescription);
        inputLocation = view.findViewById(R.id.editTextLocation);
        inputStartTime = view.findViewById(R.id.startTime);
        inputDue = view.findViewById(R.id.due);
        inputDeadline = view.findViewById(R.id.deadline);
        inputDays = view.findViewById(R.id.days);
        inputMaxReg = view.findViewById(R.id.maxReg);
        imgName = view.findViewById(R.id.imgName);
        buttonChoose = view.findViewById(R.id.buttonChoose);
        buttonPost = view.findViewById(R.id.buttonPost);
        spinnerFaculty = view.findViewById(R.id.spinnerFaculty);

        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateName();
                }
            }
        });

        inputDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateDescription();
                }
            }
        });

        inputLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateLocation();
                }
            }
        });

        inputStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateStart();
                }
            }
        });

        inputDue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateDue();
                }
            }
        });

        inputDeadline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateDeadline();
                }
            }
        });

        inputDays.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateDays();
                }
            }
        });

        inputMaxReg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateMaxReg();
                }
            }
        });

        String defaultImageName = "No image selected";
        imgName.setText(defaultImageName);
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, AddFragment.REQUEST_CODE_CHOOSE_IMAGE);
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
                updateDescription();
                updateLocation();
                updateStart();
                updateDue();
                updateDeadline();
                updateDays();
                updateMaxReg();
                if (name != null && location != null && faculty != null
                        && start != null && due != null && deadline != null
                        && days * maxReg > 0) {
                    uploadImage();
                } else {
                    Toast.makeText(view.getContext(), "Please correct all fields", Toast.LENGTH_LONG).show();
                    inputMaxReg.requestFocus();
                }
            }
        });

        FirebaseFirestore.getInstance()
                .collection("faculty")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mapFaculty = new HashMap<>();
                            for (QueryDocumentSnapshot item : Objects.requireNonNull(task.getResult())) {
                                mapFaculty.put(item.getString("name"), item.getId());
                            }
                            setUpSpinnerFaculty();
                        }
                    }
                });

        FirebaseStorage.getInstance().getReference()
                .child("default.jpg")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgFireURL = uri.toString();
                    }
                });

        return view;
    }


    private void updateName() {
        if (inputName.length() == 0) {
            inputName.setError("Empty title");
            name = null;
        } else {
            name = inputName.getText().toString();
        }
    }

    private void updateDescription() {
        description = inputDescription.getText().toString();
    }

    private void updateLocation() {
        if (inputLocation.length() == 0) {
            inputLocation.setError("Empty location");
            location = null;
        } else {
            location = inputLocation.getText().toString();
        }
    }

    private void updateStart() {
        start = convertTime(inputStartTime.getText().toString());
        if (start == null) {
            inputStartTime.setError("Time input format must be \"DD/MM/YYYY HH:MM\"");
        }
    }

    private void updateDue() {
        due = convertTime(inputDue.getText().toString());
        if (due == null) {
            inputDue.setError("Time input format must be \"DD/MM/YYYY HH:MM\"");
        } else if (due.getSeconds() < start.getSeconds()) {
            inputDue.setError("Wrong time: End before start!");
        }
    }

    private void updateDeadline() {
        deadline = convertTime(inputDeadline.getText().toString());
        if (deadline == null) {
            inputDeadline.setError("Time input format must be \"DD/MM/YYYY HH:MM\"");
        } else if (deadline.getSeconds() > due.getSeconds()) {
            inputDeadline.setError("Wrong time: Register after end!");
        }
    }

    private void updateDays() {
        if (inputDays.length() == 0) {
            inputDays.setError("Empty");
            days = 0;
        } else {
            days = Double.parseDouble(inputDays.getText().toString());
            if (days <= 0) {
                inputDays.setError("Must be positive");
            }
        }
    }

    private void updateMaxReg() {
        if (inputMaxReg.length() == 0) {
            inputMaxReg.setError("Empty");
            maxReg = 0;
        } else {
            maxReg = Integer.parseInt(inputMaxReg.getText().toString());
            if (maxReg <= 0) {
                inputMaxReg.setError("Must be positive");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == AddFragment.REQUEST_CODE_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            imgLocalURI = data.getData();

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            String fileName = (new File(picturePath)).getName();
            imgName.setText(fileName);
        }
    }

    private void setUpSpinnerFaculty() {
        ArrayList<String> listFaculty = new ArrayList<>(mapFaculty.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                view.getContext(), android.R.layout.simple_spinner_item, listFaculty);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFaculty.setAdapter(adapter);
        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                faculty = mapFaculty.get(spinnerFaculty.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                faculty = mapFaculty.get(spinnerFaculty.getItemAtPosition(0).toString());
            }
        });
    }

    private void uploadImage() {
        if (imgLocalURI != null) {

            SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault());
            String imgTag = formatter.format(new Date(System.currentTimeMillis()));

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imgTag);
            storageReference.putFile(imgLocalURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imgFireURL = uri.toString();
                                    parse();
                                }
                            });
                        }
                    });

        }
    }

    private void parse() {

        HashMap<String, Object> item = new HashMap<>();
        item.put("deadline_register", deadline);
        item.put("description", description);
        item.put("id_faculty", faculty);
        item.put("image", imgFireURL);
        item.put("location", location);
        item.put("maximum_ctxh_day", days);
        item.put("maximum_register", maxReg);
        item.put("time_end", due);
        item.put("time_start", start);
        item.put("title", name);

        FirebaseFirestore.getInstance()
                .collection("ctxh")
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Timestamp convertTime(String in) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return new Timestamp(format.parse(in + ":00"));
        } catch (ParseException e) {
            return null;
        }
    }

}
