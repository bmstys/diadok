package com.dinosoft.diadok;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HasilBerat extends AppCompatActivity
{
    private String sakit;
    private String catatan;

    private TextView hasilDiagnosa,pengertianDiagnosa,catatanDiagnosa;

    private Query dataDiagnosis;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_berat);

        Bundle extras = getIntent().getExtras();
        sakit = extras.getString("sakitMirip");
        catatan = extras.getString("catatan");

        hasilDiagnosa = (TextView)findViewById(R.id.hasil_diagnosa);
        pengertianDiagnosa = (TextView)findViewById(R.id.penjelasan_diagnosa);
        catatanDiagnosa = (TextView)findViewById(R.id.catatan_daignosa);

        dataDiagnosis = FirebaseDatabase.getInstance().getReference().child("Penyakit").orderByChild("nama_penyakit").equalTo(sakit);

        showData();
    }

    private void showData()
    {
        dataDiagnosis.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dataPenyakit : dataSnapshot.getChildren())
                    {
                        DataPenyakit penyakit = dataPenyakit.getValue(DataPenyakit.class);

                        String nama_penyakit = penyakit.getNama_penyakit();
                        String deskripsi_penyakit = penyakit.getDeskripsi();

                        hasilDiagnosa.setText(": "+nama_penyakit);
                        catatanDiagnosa.setText(catatan);
                        pengertianDiagnosa.setText(deskripsi_penyakit);
                    }
                }
                else
                {
                    Toast.makeText(HasilBerat.this, "Data null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(HasilBerat.this, "Upsss... Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pesan_dokter(View view)
    {
        Intent intent = new Intent(HasilBerat.this, PesanDokter.class);
        Bundle extras = new Bundle();
        extras.putString("diagnosaSakit", sakit);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void beranda(View view)
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
