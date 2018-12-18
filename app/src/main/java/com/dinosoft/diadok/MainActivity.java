package com.dinosoft.diadok;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{
    // Inisiasi untuk login
    private EditText e_email,e_password;

    // Untuk registrasi
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Untuk Database
    private DatabaseReference mDatabase;

//    private static boolean setPersistence = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Untuk cek ada login atau tidak
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Kalo ga login maka
                if(firebaseAuth.getCurrentUser() != null)
                {
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    startActivity(intent);
                }
            }
        };

        // Untuk biar ketika offline data tetep bisa diakses
//        if (! setPersistence)
//        {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//            setPersistence = true;
//        }

        // Untuk database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        e_email = (EditText)findViewById(R.id.l_email);
        e_password = (EditText)findViewById(R.id.l_password);
    }

    public void daftar(View view)
    {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void masuk(View view)
    {
        check_akun();
    }

    private void check_akun()
    {
        String email = e_email.getText().toString().trim();
        String password = e_password.getText().toString().trim();

        if(email.isEmpty())
        {
            e_email.setError("Anda belum mengisi email anda");
            e_email.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            e_password.setError("Anda belum mengisi password anda");
            e_password.requestFocus();
            return;
        }

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        checkUserExist();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Gagal login, mohon pastikan kombinasi email dan password benar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkUserExist()
    {
        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(user_id))
                {
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    startActivity(intent);

                    finish();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Akun tidak terdaftar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Untuk listen user apakah authentication atau ga
        mAuth.addAuthStateListener(mAuthListener);
    }
}
