package com.ledgeradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    EditText txtUserId;
    EditText txtPassword;
    Button login;
    FirebaseFirestore db;
    ProgressBar progressBar;

    public static final String SHARED_PREFS = "sharedPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUserId = findViewById(R.id.login_phone_edit);
        txtPassword = findViewById(R.id.password_edit);
        login = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNow();
            }
        });
    }

    private void loginNow(){

        progressBar.setVisibility(View.VISIBLE);

        final String user = txtUserId.getText().toString();
        final String password = txtPassword.getText().toString();

        if(TextUtils.isEmpty(user)){
            Toast.makeText(this, "Please enter user id", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
        else {

            db.collection("Admin").whereEqualTo("user",user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(!task.getResult().isEmpty()) {

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        String pass = documentSnapshot.getString("password");

                        if (password.equals(pass)) {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        else{
                            Toast.makeText(LoginActivity.this, "Please enter a correct password", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }

                    }
                    else if (task.getResult().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Please enter a valid user Id", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    } else {
                        Toast.makeText(LoginActivity.this, "Error Occurred, Please contact an administrator", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

}