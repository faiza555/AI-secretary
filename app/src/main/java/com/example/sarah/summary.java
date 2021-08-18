package com.example.sarah;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import static android.content.ContentValues.TAG;

public class summary extends AppCompatActivity {

    TextView textView,textView1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_summarize);

        textView1 = findViewById(R.id.text_view_id1);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSummary();
            }
        }, 5 * 1000);
//        showSummary();


        //textView = findViewById(R.id.text_view_id);
        Bundle bundle = getIntent().getExtras();
        String venName = bundle.getString("hi");
        textView1.setTextSize(15);
        textView1.setText(venName);
        Log.d("WHAT I GOT: ", String.valueOf(venName));



        //String titleresult = getIntent().getStringExtra("WHAT I SENT:");
        //Log.d("WHAT I GOT: ", String.valueOf(titleresult));

       // textView.setText(titleresult);
    }

    //get file from FirebaseStorage then store in device then read to view it
    private void showSummary()  {
//        StorageReference sr = FirebaseStorage.getInstance().getReference();
        File outputfile = new File(Environment.getExternalStorageDirectory(), "summary.txt");
//        sr.child("Summarized Text").getFile(outputfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                Log.d(TAG, "File Downloaded: " + outputfile);
//            }
//        });


        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(outputfile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        String str = text.toString();
        str.replaceAll("\\.\\u2022", "\\.\n");
        textView1.setTextSize(15);
        textView1.setText(str);
    }


}


