package com.dinosoft.diadok;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class KonfirmasiObat extends AppCompatActivity
{
    private RecyclerView mObatList;
    private DatabaseReference mDatabaseObat;
    private Query mQueryObat;

    private String penyakit_id;

    private Button tombolKonfirmasi;

    private CheckBox aman_beliObat;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_obat);

        Bundle extras = getIntent().getExtras();
        penyakit_id = extras.getString("penyakit_id");

        mObatList = (RecyclerView)findViewById(R.id.kandungan_list);
        mObatList.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseObat = FirebaseDatabase.getInstance().getReference().child("Obat");
        mQueryObat = mDatabaseObat.orderByChild("penyakit_id").equalTo(penyakit_id);

        tombolKonfirmasi = (Button)findViewById(R.id.tombol_konfirmasi_obat);
        tombolKonfirmasi.setEnabled(false);

        final Drawable buttonBackground = tombolKonfirmasi.getBackground();

        tombolKonfirmasi.setAlpha(0);

        aman_beliObat = (CheckBox) findViewById( R.id.amanBeliObat );
        aman_beliObat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if ( ((CheckBox)v).isChecked() )
                {
                    tombolKonfirmasi.setAlpha(1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        tombolKonfirmasi.setBackground(buttonBackground);
                    }
                    tombolKonfirmasi.setEnabled(true);
                }
                else
                {
                    tombolKonfirmasi.setEnabled(false);
                    tombolKonfirmasi.setAlpha(0);
                }
            }
        });
    }

    public void konfirmasiObat(View view)
    {
        Intent intent = new Intent(KonfirmasiObat.this, BeliObat.class);
        Bundle extras = new Bundle();
        extras.putString("penyakit_id", penyakit_id);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<DataObat,KonfirmasiObat.KandunganViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DataObat, KonfirmasiObat.KandunganViewHolder>
                (
                        DataObat.class,
                        R.layout.kandungan_row,
                        KonfirmasiObat.KandunganViewHolder.class,
                        mQueryObat
                )
        {
            @Override
            protected void populateViewHolder(KonfirmasiObat.KandunganViewHolder viewHolder, DataObat model, int position)
            {
                viewHolder.setKandungan(model.getKandungan());
                viewHolder.setNama_obat(model.getNama_obat());
            }
        };

        mObatList.setAdapter(firebaseRecyclerAdapter);
    }

    public void homes(View view)
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }


    public static class KandunganViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public KandunganViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;
        }

        public void setKandungan(String kandungan)
        {
            TextView obat_kandungan = (TextView)mView.findViewById(R.id.kandungan_obatTv);
            obat_kandungan.setText(kandungan);
        }

        public void setNama_obat(String nama_obat)
        {
            TextView obat_nama = (TextView)mView.findViewById(R.id.nama_obatTv);
            obat_nama.setText(nama_obat);
        }
    }

}
