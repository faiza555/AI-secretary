package com.example.sarah;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;

    private ImageButton listBtn;
    private ImageButton recordBtn;
    private TextView filenameText;

    private boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private MediaRecorder mediaRecorder;
    private String recordFile;



    private static final int PICK_FILE = 1 ;
    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn = view.findViewById(R.id.record_btn);
        //timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);


        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_list_btn:
                if (isRecording) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", null);
                    alertDialog.setTitle("Audio still recording");
                    alertDialog.setMessage("Are you sure you want to stop the recording");
                    alertDialog.create().show();
                } else {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);

                }

                break;

            case R.id.record_btn:
                if (isRecording) {
                    //stopRecording();
                   // uploadAudio();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.stoprec, null));
//                    }
//                    isRecording = false;
                } else {
                    //Start Recording
                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        if (CheckPermissions()) {
                            startRecording();
                            navController.navigate(R.id.action_recordFragment_to_speech_to_text);

                            //recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.startrec, null));

                            //isRecording = true;
                        } else {
                            RequestPermissions();
                        }
                   // }
                }
                break;


        }
    }


//    private void stopRecording() {
//        navController.navigate(R.id.action_recordFragment_to_speech_to_text);
//        timer.stop();
//
//        filenameText.setText("Recording Stopped, File Saved : " + recordFile);
//
//        mediaRecorder.stop();
//        mediaRecorder.release();
//        mediaRecorder = null;
//    }

    private void startRecording() {
        //timer.setBase(SystemClock.elapsedRealtime());
       // timer.start();

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        String date =  DateFormat.getDateTimeInstance().format(System.currentTimeMillis());

        recordFile = "Recording " + date ;

        filenameText.setText("Recording, File Name : " + recordFile);

//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        mediaRecorder.setOutputFile(mFileName);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mediaRecorder.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        mediaRecorder.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


//    @Override
//    public void onStop() {
//        super.onStop();
//        if (isRecording) {
//            stopRecording();
//        }
//    }



//    private void uploadAudio() {
//        mProgress.setMessage("Uploading Audio");
//        mProgress.show();
//
//
//        Uri FileUri = Uri.fromFile(new File(mFileName));
//        String date =  DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
//
//        StorageReference Folder = mStorage.child("Recording "+ date );
//
//
//        Folder.putFile(FileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                mProgress.dismiss();
//                Folder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("posts").push();
//                        Map<String, Object> map = new HashMap<>();
//                        //map.put("title", editText.getText().toString());
//                        map.put("id", databaseReference.getKey());
//                        map.put("audiolink", String.valueOf(uri));
//
//                        databaseReference.setValue(map);
//
//                        Toast.makeText(getActivity(), "File Uploaded", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
}

