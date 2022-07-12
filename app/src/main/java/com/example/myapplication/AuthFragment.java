package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AuthFragment extends Fragment {
    AppCompatButton appCompatBtnAuth;
    EditText phoneEditText;
    EditText passEditText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View v = inflater.inflate(R.layout.fragment_auth, container, false);
        appCompatBtnAuth = v.findViewById(R.id.appCompatBtnAuth);
        phoneEditText = v.findViewById(R.id.phoneEditText);
        passEditText = v.findViewById(R.id.passEditText);
        appCompatBtnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneEditText.getText().toString();
                String pass = passEditText.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket("192.168.1.6", 9178);
                            DataInputStream is = new DataInputStream(socket.getInputStream());
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            out.writeUTF("auth//"+phone+"//"+pass);
                            String response = is.readUTF();
                            System.out.println(response);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (response.equals("success")) {
                                        Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
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
        return v;
    }
}
