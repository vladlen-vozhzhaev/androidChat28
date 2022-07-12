package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button authBtn;
    LinearLayout mainLinearLayout;
    static String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authBtn = findViewById(R.id.authBtn);
        mainLinearLayout = findViewById(R.id.mainLinearLayout);
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(10, 20, 10, 20);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(lp);
                TextView textView = new TextView(context);
                EditText loginEditText = new EditText(context);
                EditText passEditText = new EditText(context);
                Button loginBtn = new Button(context);
                textView.setText("Авторизация");
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                loginEditText.setHint("Телефон");
                loginEditText.setLayoutParams(lp);
                loginEditText.setGravity(Gravity.CENTER);
                passEditText.setHint("Пароль");
                passEditText.setLayoutParams(lp);
                passEditText.setGravity(Gravity.CENTER);
                passEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                loginBtn.setText("Войти");
                linearLayout.addView(textView);
                linearLayout.addView(loginEditText);
                linearLayout.addView(passEditText);
                linearLayout.addView(loginBtn);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                mainLinearLayout.removeAllViews();
                mainLinearLayout.addView(linearLayout);
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = loginEditText.getText().toString();
                        String pass = passEditText.getText().toString();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Socket socket = new Socket("192.168.1.8", 9178);
                                    DataInputStream is = new DataInputStream(socket.getInputStream());
                                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                    out.writeUTF("auth//"+phone+"//"+pass);
                                    String response = is.readUTF();
                                    System.out.println(response);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(response.equals("success")){
                                                TextView textView = new TextView(context);
                                                EditText chatEditText = new EditText(context);
                                                Button sendBtn = new Button(context);
                                                mainLinearLayout.removeAllViews();
                                                mainLinearLayout.addView(textView);
                                                mainLinearLayout.addView(chatEditText);
                                                mainLinearLayout.addView(sendBtn);

                                                sendBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Thread thread1 = new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        message = chatEditText.getText().toString();
                                                                        Thread thread2 = new Thread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    out.writeUTF(message);
                                                                                } catch (IOException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                        thread2.start();
                                                                    }
                                                                });

                                                            }
                                                        });
                                                        thread1.start();

                                                    }
                                                });
                                                Thread thread1 = new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            while (true){
                                                                String response = is.readUTF();
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        textView.append(response+"\n");
                                                                    }
                                                                });
                                                            }
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                                thread1.start();

                                            }else{
                                                Toast.makeText(context, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
                                            }



                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                });
            }
        });
    }
}