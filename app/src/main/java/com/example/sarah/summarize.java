package com.example.sarah;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static android.provider.FontsContract.Columns.RESULT_CODE;


public class summarize extends Fragment {

    // private WebView webview;
    private EditText summary_text;
    private NavController navController;
    TextView textView;


    public summarize() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_summarize, container, false);
        textView = rootView.findViewById(R.id.text_view_id);



       // Intent returnIntent = new Intent();
       // setResult(AudioListAdapter.RESULT_CODE, returnIntent);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        Intent intent = getActivity().getIntent();
        String txt_put =intent.getStringExtra("KEY");
        Toast.makeText(getContext(), "Intent recieved!!!" + txt_put , Toast.LENGTH_SHORT).show();
        textView.setText(txt_put);

        Intent i= getActivity().getIntent();
        i.getExtras();


        showSummary();
    }


    private void showSummary() {
        StorageReference sr = FirebaseStorage.getInstance().getReference();
        File outputfile = new File(Environment.getExternalStorageDirectory(), "summary.txt");
        sr.child("Summarized Text").getFile(outputfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "File Downloaded: " + outputfile);
            }
        });


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
        textView.setText(str);


    }

}