package com.luong.mainctxhactivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.constraint.Constraints.TAG;

public class AddFragment extends Fragment {

    final static int REQUEST_CODE_CHOOSE_IMAGE = 1;

    View view;
    EditText inputName;
    EditText inputDescription;
    EditText inputLocation;
    EditText inputDays;
    EditText inputMaxReg;

    Calendar current;
    Button buttonStartTime;
    Button buttonEndTime;
    Button buttonDeadlineTime;
    Button buttonStartDate;
    Button buttonEndDate;
    Button buttonDeadlineDate;

    Button buttonChoose;
    Button buttonPost;
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

    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_ctxh, container, false);

        inputName = view.findViewById(R.id.editTextName);
        inputDescription = view.findViewById(R.id.editTextDescription);
        inputLocation = view.findViewById(R.id.editTextLocation);
        inputDays = view.findViewById(R.id.days);
        inputMaxReg = view.findViewById(R.id.maxReg);

        buttonStartDate = view.findViewById(R.id.btnStartDate);
        buttonStartTime = view.findViewById(R.id.btnStartTime);
        buttonEndDate = view.findViewById(R.id.btnEndDate);
        buttonEndTime = view.findViewById(R.id.btnEndTime);
        buttonDeadlineDate = view.findViewById(R.id.btnDeadlineDate);
        buttonDeadlineTime = view.findViewById(R.id.btnDeadlineTime);

        current = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        buttonChoose = view.findViewById(R.id.buttonChoose);
        buttonPost = view.findViewById(R.id.buttonPost);
        imgName = view.findViewById(R.id.imgName);
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


        buttonStartDate.setText(dateFormat.format(current.getTimeInMillis()));
        buttonStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(buttonStartDate);
            }
        });
        buttonStartTime.setText(timeFormat.format(current.getTimeInMillis()));
        buttonStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(buttonStartTime);
            }
        });

        buttonEndDate.setText(dateFormat.format(current.getTimeInMillis()));
        buttonEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(buttonEndDate);
            }
        });
        buttonEndTime.setText(timeFormat.format(current.getTimeInMillis()));
        buttonEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(buttonEndTime);
            }
        });

        buttonDeadlineDate.setText(dateFormat.format(current.getTimeInMillis()));
        buttonDeadlineDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(buttonDeadlineDate);
            }
        });
        buttonDeadlineTime.setText(timeFormat.format(current.getTimeInMillis()));
        buttonDeadlineTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(buttonDeadlineTime);
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
                if (updateName() && updateDescription() && updateLocation() &&
                        updateStart() && updateEnd() && updateDeadline() &&
                        updateDays() && updateMaxReg()) {
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

    private void datePicker(final Button button) {
        new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                button.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year));
            }
        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void timePicker(final Button button) {
        new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                button.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }
        }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), true).show();
    }


    private boolean updateName() {
        if (inputName.length() == 0) {
            inputName.setError("Empty title");
            name = null;
        } else {
            name = inputName.getText().toString();
        }
        return name != null;
    }

    private boolean updateDescription() {
        description = inputDescription.getText().toString();
        return true;
    }

    private boolean updateLocation() {
        if (inputLocation.length() == 0) {
            inputLocation.setError("Empty location");
            location = null;
        } else {
            location = inputLocation.getText().toString();
        }
        return location != null;
    }

    private boolean updateStart() {
        start = convertTime(buttonStartDate.getText().toString() + " " + buttonStartTime.getText().toString());
        if (start == null) {
            Toast.makeText(view.getContext(), "Error while parse the selected time", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean updateEnd() {
        due = convertTime(buttonEndDate.getText().toString() + " " + buttonEndTime.getText().toString());
        if (due == null) {
            Toast.makeText(view.getContext(), "Error while parse the selected time", Toast.LENGTH_SHORT).show();
        } else if (due.getSeconds() < start.getSeconds()) {
            Toast.makeText(view.getContext(), "Wrong time: End before start!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean updateDeadline() {
        deadline = convertTime(buttonDeadlineDate.getText().toString() + " " + buttonDeadlineTime.getText().toString());
        if (deadline == null) {
            Toast.makeText(view.getContext(), "Error while parse the selected time", Toast.LENGTH_SHORT).show();
        } else if (deadline.getSeconds() > due.getSeconds()) {
            Toast.makeText(view.getContext(), "Wrong time: Register after end!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean updateDays() {
        if (inputDays.length() == 0) {
            inputDays.setError("Empty");
            days = 0;
            return false;
        } else {
            days = Double.parseDouble(inputDays.getText().toString());
            if (days <= 0) {
                inputDays.setError("Must be positive");
                return false;
            }
        }
        return true;
    }

    private boolean updateMaxReg() {
        if (inputMaxReg.length() == 0) {
            inputMaxReg.setError("Empty");
            maxReg = 0;
            return false;
        } else {
            maxReg = Integer.parseInt(inputMaxReg.getText().toString());
            if (maxReg <= 0) {
                inputMaxReg.setError("Must be positive");
                return false;
            }
            return true;
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

                        sendingNotificationToAllUser();
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

    private void sendingNotificationToAllUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                //.whereEqualTo("admin", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sendToToken(document.get("token").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void sendToToken(String token) {
        if(token != "") {
            Retrofit retrofit =  new Retrofit.Builder()
                    .baseUrl("https://cthx-manager.firebaseio.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Api api = retrofit.create(Api.class);

            Call<ResponseBody> call = api.sendNotification(token, "CTXH BKU", "Hoạt động mới: " + inputName.getText().toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Toast.makeText(context, response.body().toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Fail to send", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

}
