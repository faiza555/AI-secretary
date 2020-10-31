package com.example.sarah;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class speech_to_text extends Fragment implements View.OnClickListener {

    private Chronometer timer;
    private MediaRecorder mediaRecorder;
    private ImageButton stopRecordBtn;
    private Button storeText;
    private EditText transcript;
    private SpeechRecognizer sr;
    private static final String TAG = "MyStt3Activity";
    private static final int  WRITE_EXTERNAL_STORAGE_CODE = 1;

    String mtext;
    File tFile;

    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private static String mFileName = null;
    private static String tFileName = null;
    File file, fileEvents;

    static final int REQUEST_CODE=1;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    public speech_to_text() {
        // Required empty public constructor
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);

      //  startActivityForResult(intent, "<some code you choose>");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speech_to_text, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transcript = view.findViewById(R.id.text_view_id);
        storeText = view.findViewById(R.id.save_text);
        stopRecordBtn = view.findViewById(R.id.stop_rec);

        storeText.setOnClickListener(this);
        stopRecordBtn.setOnClickListener(this);

        promptSpeechInput();


        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(getActivity());

        timer = view.findViewById(R.id.record_timer);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();


        mFileName = getActivity().getExternalFilesDir("/").getAbsolutePath();
        int entryNumber = 1;

        File mFile = new File(mFileName + "/AudioRecording_" + entryNumber + ".3gp");
        while(mFile.exists()) {
            entryNumber++;
            mFile = new File(mFileName + "/AudioRecording_" + entryNumber + ".3gp");
        }
        mFileName = mFile.getAbsolutePath();


        tFileName =  Environment.getExternalStorageDirectory().getAbsolutePath();
//        int entryNumber1 = 1;
//
//        File tFile = new File(tFileName + "/SpeechToText" + entryNumber1 + ".txt");
//        while(tFile.exists()) {
//            entryNumber1++;
//            tFile = new File(tFileName + "/SpeechToText" + entryNumber1 + ".txt");
//        }
//        tFileName = tFile.getAbsolutePath();

         fileEvents = new File(getActivity().getFilesDir()+"/text/sample");
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setOutputFile(mFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

       // mediaRecorder.start();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_rec:

                stopRecording();
                uploadAudio();
                break;

            case R.id.save_text:
                saveText();
                uploadText();

        }

    }

    private void saveText(){

        file = new File(getActivity().getFilesDir(), "text");
            try {
                File gpxfile = new File(file, "sample");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(transcript.getText().toString());
                writer.flush();
                writer.close();
                Toast.makeText(this.getActivity(), "Saved your text", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }


        }



    private void stopRecording (){
        timer.stop();
        //mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void uploadAudio() {
        mProgress.setMessage("Uploading Audio");
        mProgress.show();


        Uri FileUri = Uri.fromFile(new File(mFileName));
        String date =  DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
// Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/AMR")
                .build();

        StorageReference Folder = mStorage.child("Recording "+ date );

        UploadTask  up = Folder.putFile(FileUri, metadata);

        up.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
                Folder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("audio").push();
                        Map<String, Object> map = new HashMap<>();
                        //map.put("title", editText.getText().toString());
                        map.put("id", databaseReference.getKey());
                        map.put("audiolink", String.valueOf(uri));

                        databaseReference.setValue(map);

                        Toast.makeText(getActivity(), "File Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void uploadText() {
        mProgress.setMessage("Uploading Text");
        mProgress.show();


        Uri FileUri = Uri.fromFile(new File(String.valueOf(fileEvents)));
        String date =  DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("text/plain")
                .build();

        StorageReference Folder = mStorage.child("Text "+ date );



        // Upload the file and metadata
        UploadTask  up = Folder.putFile(FileUri, metadata);

        up.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
                Folder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("text").push();
                        Map<String, Object> map = new HashMap<>();
                        //map.put("title", editText.getText().toString());
                        map.put("id", databaseReference.getKey());
                        map.put("textlink", String.valueOf(uri));

                        databaseReference.setValue(map);

                        Toast.makeText(getActivity(), "Text File Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
            stopRecording();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {


                    Bundle bundle = data.getExtras();
                    ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
                    transcript.setText(matches.get(0));
                    // the recording url is in getData:
                    Uri audioUri = data.getData();
                    ContentResolver contentResolver = getContext().getContentResolver();

                    InputStream filestream = null;
                    try {
                        filestream = contentResolver.openInputStream(audioUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    assert filestream != null;
                    byte[] buffer = new byte[0];
                    try {
                        buffer = new byte[filestream.available()];
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        filestream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    OutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(mFileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        outStream.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            }

        }
    }
}
