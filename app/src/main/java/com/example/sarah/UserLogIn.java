package com.example.sarah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class UserLogIn extends AppCompatActivity {
    Button userSignUp;
    Button login1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        userSignUp = findViewById(R.id.signup);
        login1 = findViewById(R.id.login);

        LinearLayout linearLayout = findViewById(R.id.Llayout);
        AnimationDrawable animationDrawable = (AnimationDrawable)linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();



        userSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserLogIn.this,UserSignUp.class);
                startActivity(intent);

            }
        });

        login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserLogIn.this,Dashboard.class);
                startActivity(intent);

            }
        });




    }
}