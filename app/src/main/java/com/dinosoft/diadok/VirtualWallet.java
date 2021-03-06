package com.dinosoft.diadok;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import de.hdodenhof.circleimageview.CircleImageView;

public class VirtualWallet extends AppCompatActivity
{
    private TextView username,saldo;
    private CircleImageView foto;

    private FirebaseAuth mAuth;
    private FirebaseUser usernya;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private String URL;
    private String userIdnya;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_wallet);

        mAuth = FirebaseAuth.getInstance();
        usernya = mAuth.getCurrentUser();
        userIdnya = usernya.getUid();

        username = (TextView)findViewById(R.id.w_username);
        saldo = (TextView)findViewById(R.id.w_saldo);
        foto = (CircleImageView)findViewById(R.id.w_foto_profil);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userIdnya);
        mDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String usernamenya = dataSnapshot.child("username").getValue().toString();
                String saldonya = dataSnapshot.child("saldo").getValue().toString();

                int saldo_fix = Integer.parseInt(saldonya);

                DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                formatRp.setCurrencySymbol("Rp ");
                formatRp.setMonetaryDecimalSeparator(',');
                formatRp.setGroupingSeparator('.');

                kursIndonesia.setDecimalFormatSymbols(formatRp);

                username.setText(usernamenya);
                saldo.setText(kursIndonesia.format(saldo_fix));

                final String foto_profilnya = dataSnapshot.child("foto_profil").getValue().toString();

                mStorage = FirebaseStorage.getInstance().getReference().child("Foto Profil").child(foto_profilnya);
                mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        URL = uri.toString();

                        show_foto(URL);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void show_foto(String alamatUrl)
    {
        String alamat_foto = alamatUrl;

        // Showing foto
        Glide.with(getApplicationContext()).load(alamat_foto).into(foto);
    }

    public void top_up_saldo(View view)
    {
        Intent intent = new Intent(this, TopUp.class);
        startActivity(intent);
        finish();
    }
}
