package com.dinosoft.diadok;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class HasilRingan extends AppCompatActivity
{
    private String sakit;
    private String catatan;
    private String penyakit_id;

    private TextView hasilDiagnosa,pengertianDiagnosa,catatanDiagnosa;

    private Query dataDiagnosis;
    private Query dataObat;

    private RecyclerView mRekObatList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_ringan);

        Bundle extras = getIntent().getExtras();
        sakit = extras.getString("sakitMirip");
        catatan = extras.getString("catatan");

        hasilDiagnosa = (TextView)findViewById(R.id.hasil_diagnosa_ringan);
        pengertianDiagnosa = (TextView)findViewById(R.id.penjelasan_diagnosa_ringan);
        catatanDiagnosa = (TextView)findViewById(R.id.catatan_daignosa_ringan);

        dataDiagnosis = FirebaseDatabase.getInstance().getReference().child("Penyakit").orderByChild("nama_penyakit").equalTo(sakit);

        mRekObatList = (RecyclerView)findViewById(R.id.rekomendasi_obat_lis);
        mRekObatList.setLayoutManager(new LinearLayoutManager(this));

        showDataSakit();
    }

    private void showDataSakit()
    {
        dataDiagnosis.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dataRingan : dataSnapshot.getChildren())
                    {
                        penyakit_id = dataRingan.getKey();

                        DataPenyakit sakitRingan = dataRingan.getValue(DataPenyakit.class);

                        String nama_sakit = sakitRingan.getNama_penyakit();
                        String deskripsi_sakit = sakitRingan.getDeskripsi();

                        hasilDiagnosa.setText(": "+nama_sakit);
                        catatanDiagnosa.setText(catatan);
                        pengertianDiagnosa.setText(deskripsi_sakit);

                        showDataObat(penyakit_id);
                    }
                }
                else
                {
                    Toast.makeText(HasilRingan.this, "Data null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(HasilRingan.this, "Upsss... Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDataObat(String penyakit_id)
    {
        dataObat = FirebaseDatabase.getInstance().getReference().child("Obat").orderByChild("penyakit_id").equalTo(penyakit_id);

        FirebaseRecyclerAdapter<DataObat,HasilRingan.RekObatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DataObat, HasilRingan.RekObatViewHolder>
                (
                        DataObat.class,
                        R.layout.obat_rekomendasi_row,
                        HasilRingan.RekObatViewHolder.class,
                        dataObat
                )
        {
            @Override
            protected void populateViewHolder(HasilRingan.RekObatViewHolder viewHolder, DataObat model, int position)
            {
                viewHolder.setNama_obat(model.getNama_obat());
            }
        };

        mRekObatList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RekObatViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public RekObatViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;
        }

        public void setNama_obat(String nama_obat)
        {
            TextView rek_obat = (TextView)mView.findViewById(R.id.rekomndasi_obatTv);
            rek_obat.setText(nama_obat);
        }
    }

    public void beli_obat(View view)
    {
        Intent intent = new Intent(HasilRingan.this, KonfirmasiObat.class);
        Bundle extras = new Bundle();
        extras.putString("penyakit_id", penyakit_id);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void beranda(View view)
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
