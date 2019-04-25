package com.ace.xiatom.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText n = findViewById(R.id.name);
                EditText p = findViewById(R.id.password);
                EditText s = findViewById(R.id.sendTo);
                String name = n.getText().toString();
                String pass = p.getText().toString();
                String sendto = s.getText().toString();
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("password",pass);
                intent.putExtra("sendto",sendto);
                startActivity(intent);
            }
        });

    }
}
