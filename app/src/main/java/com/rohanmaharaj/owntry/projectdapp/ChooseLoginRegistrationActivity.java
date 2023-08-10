package com.rohanmaharaj.owntry.projectdapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {

    private Button loginbtn, registerbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);
        loginbtn = findViewById(R.id.loginBtn);
        registerbtn = findViewById(R.id.registerBtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseLoginRegistrationActivity.this, LoginActivity.class));
                finish();
                return;
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseLoginRegistrationActivity.this, RegistrationActivity.class));
                finish();
                return;

            }
        });
    }
}