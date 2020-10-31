package com.example.sarah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Lexi_chatbot extends AppCompatActivity {

    private EditText editText;
    private final int USER = 0;
    private final int BOT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexi_chatbot);
        editText = findViewById(R.id.edittext_chatbox);


        ConstraintLayout constraintLayout = findViewById(R.id.chatbotsc);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav);

        bottomNavigationView.setSelectedItemId(R.id.chatbotsc);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext()
                                , Dashboard.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext()
                                , settings.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.lexi_chatbot:
                        return true;
                }
                return false;
            }
        });


    }

    public void sendMessage(View view) {
        UserMessage userMessage = null;
        MessageSender messageSender;
        String msg = editText.getText().toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://45f5d6874e11.ngrok.io/webhooks/rest/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        if (msg.trim().isEmpty()) {
            Toast.makeText(Lexi_chatbot.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            editText.setText("");

            userMessage = new UserMessage("User",msg);
        }
        Toast.makeText(Lexi_chatbot.this, ""+userMessage.getMessage(), Toast.LENGTH_LONG).show();
        messageSender = retrofit.create(MessageSender.class);
        Call<List<BotResponse>> response = messageSender.sendMessage(userMessage);
        response.enqueue(new Callback<List<BotResponse>>() {
            @Override
            public void onResponse(Call<List<BotResponse>> call, Response<List<BotResponse>> response) {
                if(response.body() == null || response.body().size() == 0){
                    showTextView("Sorry didn't understand",BOT);
                }
                else{
                    BotResponse botResponse = response.body().get(0);
                    showTextView(botResponse.getText(),BOT);
                }
            }
            @Override
            public void onFailure(Call<List<BotResponse>> call, Throwable t) {
                showTextView("Waiting for message",BOT);
                Toast.makeText(Lexi_chatbot.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void showTextView(String message, int type) {
        LinearLayout chatLayout = findViewById(R.id.chat_layout);
        LayoutInflater inflater = LayoutInflater.from(Lexi_chatbot.this);
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout);
        TextView tv = layout.findViewById(R.id.chat_msg);
        tv.setText(message);
        layout.requestFocus();
        editText.requestFocus();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa",
                Locale.ENGLISH);
        String time = dateFormat.format(date);
        TextView timeTextView = layout.findViewById(R.id.message_time);
        timeTextView.setText(time.toString());
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(Lexi_chatbot.this);
        return (FrameLayout) inflater.inflate(R.layout.user, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(Lexi_chatbot.this);
        return (FrameLayout) inflater.inflate(R.layout.bot, null);
    }
}

