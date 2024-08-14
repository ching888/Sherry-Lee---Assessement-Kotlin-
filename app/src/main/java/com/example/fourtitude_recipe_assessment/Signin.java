package com.example.fourtitude_recipe_assessment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.fourtitude_recipe_assessment.Recipe.RecipeList;

public class Signin extends AppCompatActivity {

    EditText username, password;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        btnLogin = findViewById(R.id.login);

        Intent intent = new Intent(this, RecipeList.class);
        startActivity(intent);
    }
}
