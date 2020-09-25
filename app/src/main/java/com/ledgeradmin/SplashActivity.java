package com.ledgeradmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        if(sharedPreferences!=null){
//            Log.i("method","entered");
//            Boolean Authenticated = sharedPreferences.getBoolean("Authenticated",false);
//
//            Log.i("auth",String.valueOf(Authenticated));
//
//            if(Authenticated){
//                String type = sharedPreferences.getString("type","");
//                if(type.equals("head")){
//
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//
//                    intent.putExtra("userId", sharedPreferences.getString("userId",""));
//                    intent.putExtra("type","head");
//
//                    startActivity(intent);
//                    finish();
//
//                }
//                else if(type.equals("sales")){
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//
//                    intent.putExtra("userId", sharedPreferences.getString("userId",""));
//                    intent.putExtra("type","head");
//                    intent.putExtra("company", sharedPreferences.getString("company",""));
//
//                    startActivity(intent);
//                    finish();
//                }
//
//                else if(type.equals("dealer")){
//
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//
//                    intent.putExtra("userId", sharedPreferences.getString("userId",""));
//                    intent.putExtra("type","head");
//                    intent.putExtra("company", sharedPreferences.getString("company",""));
//                    intent.putExtra("sales",sharedPreferences.getString("sales",""));
//
//                    startActivity(intent);
//                    finish();
//                }
//            }
//            else {
//                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }
//        else {
//            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }
}