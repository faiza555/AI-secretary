package com.example.sarah;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class summarize extends Fragment {
    

    //private TextView summary_text;
    private EditText summary_text;
    private NavController navController;


    public summarize() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_summarize, container, false);
        summary_text = rootView.findViewById(R.id.text_view_id);
        return rootView;

        // return inflater.inflate(R.layout.fragment_summarize, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        summary_text = view.findViewById(R.id.text_view_id);
        navController = Navigation.findNavController(view);

        LinearLayout.LayoutParams newLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        summary_text.setLayoutParams(newLp);

        //navController = Navigation.findNavController(view);
        //summaryBtn.setOnClickListener((View.OnClickListener) this);


    }

    public void sendMessage(View view) {
        UserMessage userMessage = null;
        MessageSender messageSender;
        String msg = "summarize";
        OkHttpClient okHttpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://45f5d6874e11.ngrok.io/webhooks/rest/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userMessage = new UserMessage("User",msg);

//        Toast.makeText(Lexi_chatbot.this, ""+userMessage.getMessage(), Toast.LENGTH_LONG).show();
//        messageSender = retrofit.create(MessageSender.class);
//        Call<List<BotResponse>> response = messageSender.sendMessage(userMessage);
//        response.enqueue(new Callback<List<BotResponse>>() {
//            @Override
//            public void onResponse(Call<List<BotResponse>> call, Response<List<BotResponse>> response) {
//                if(response.body() == null || response.body().size() == 0){
//                    showTextView("Sorry didn't understand",BOT);
//                }
//                else{
//                    BotResponse botResponse = response.body().get(0);
//                    showTextView(botResponse.getText(),BOT);
//                }
//            }
//            @Override
//            public void onFailure(Call<List<BotResponse>> call, Throwable t) {
//                showTextView("Waiting for message",BOT);
//                Toast.makeText(Lexi_chatbot.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        });


    }
}