package com.dinosoft.diadok;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Diagnosis extends AppCompatActivity
{
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private ArrayList<DataGejala> dataGejalas;
    private MyAdapter adapter;

    private float treshold = (float) 0.75;
    private float min = 0;
    private String kode;
    private List<String> kabehKode = new ArrayList<>();
    private int nilai_kemiripan;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);

        recyclerView = (RecyclerView)findViewById(R.id.gejala_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataGejalas = new ArrayList<DataGejala>();

        reference = FirebaseDatabase.getInstance().getReference().child("Gejala");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    DataGejala d = dataSnapshot1.getValue(DataGejala.class);
                    dataGejalas.add(d);
                }
                adapter = new MyAdapter(Diagnosis.this,dataGejalas);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(Diagnosis.this, "Upss... Gagal mencari data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cari_tahu(View view)
    {
        if(adapter.gejalanyas.size()>0)
        {
            final Map<String, Float> hasilKemiripan = new HashMap<>();

            for(DataGejala d : adapter.gejalanyas)
            {
                kabehKode.add(d.getKode());
            }

            final DatabaseReference kasus = FirebaseDatabase.getInstance().getReference().child("Kasus");
            kasus.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot dataKasus : dataSnapshot.getChildren())
                    {
                        float kemiripan = 0;
                        for (DataSnapshot gejalaKasus : dataKasus.child("Gejala").getChildren())
                        {
                            if(kabehKode.contains(gejalaKasus.getValue().toString()))
                            {
                                kemiripan++;
                            }
                        }

                        float pembagi = Math.max((float) (dataKasus.child("Gejala").getChildrenCount()), kabehKode.size());

                        float hasil_bagi = kemiripan/pembagi;

                        DecimalFormat df = new DecimalFormat("#.##");
                        hasil_bagi = Float.parseFloat(df.format(hasil_bagi));

                        hasilKemiripan.put(dataKasus.getKey(), hasil_bagi);
                    }

                    for (Map.Entry<String, Float> entry : hasilKemiripan.entrySet())
                    {
                        if (entry.getValue() > min)
                        {
                            min = entry.getValue();
                            kode = entry.getKey();
                        }
                    }

                    if(kode != null)
                    {
                        System.out.println(kode+" - "+min);
                        diagnosa(kode,min,kabehKode);
                        kasus.removeEventListener(this);
                    }
                    else
                    {
                        Toast.makeText(Diagnosis.this, "Kasus yang mirip tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Toast.makeText(Diagnosis.this, "Upss... Gagal mencari data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(Diagnosis.this, "Anda belum mengisi gejala yang anda rasakan", Toast.LENGTH_SHORT).show();
        }
    }

    private void diagnosa(String kode, final float min, final List<String> kabehKode)
    {
        final String kode_kasus = kode;
        nilai_kemiripan = (int) (min*100);

        System.out.println(kode_kasus+" mirip "+nilai_kemiripan);

        DatabaseReference refSakit = FirebaseDatabase.getInstance().getReference().child("Kasus").child(kode_kasus);
        refSakit.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final String diagnosaSakit = dataSnapshot.child("sakit").getValue().toString();

                if(diagnosaSakit.equals("Hepatitis A") || diagnosaSakit.equals("Hepatitis B") || diagnosaSakit.equals("Hepatitis C") )
                {
                    if(nilai_kemiripan > treshold*100)
                    {
                        DatabaseReference save_diagnosis = FirebaseDatabase.getInstance().getReference().child("Diagnosa").push();

                        save_diagnosis.child("foto_diagnosa").setValue("https://firebasestorage.googleapis.com/v0/b/diagnosisdokter.appspot.com/o/Foto%20Profil%2Fhist_diagnosis.png?alt=media&token=edefaf31-b988-46f2-93b4-3a08ad79de53");
                        save_diagnosis.child("kategori_penyakit").setValue("Berat");
                        save_diagnosis.child("penyakit").setValue(diagnosaSakit);

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        save_diagnosis.child("user_id").setValue(userId);

                        SimpleDateFormat format_tanggal = new SimpleDateFormat("dd MMMM yyyy");
                        SimpleDateFormat format_waktu = new SimpleDateFormat("HH:MM:SS");
                        Date date = new Date();

                        String tanggal = format_tanggal.format(date);
                        String waktu = format_waktu.format(date);

                        save_diagnosis.child("tanggal_diagnosa").setValue(tanggal);
                        save_diagnosis.child("waktu_diagnosa").setValue(waktu);

                        Intent intent = new Intent(Diagnosis.this, HasilBerat.class);
                        Bundle extras = new Bundle();
                        extras.putString("sakitMirip", diagnosaSakit);
                        extras.putString("catatan", "Gejala yang anda alami mengarah pada diagnosis untuk penyakit "+diagnosaSakit+" dengan nilai diagnosis kemiripan sebesar "+nilai_kemiripan+"%");
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                    else
                    {
                        final DatabaseReference dataKasus = FirebaseDatabase.getInstance().getReference().child("Kasus");
                        dataKasus.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                save_kasus_baru_berat(dataSnapshot.getChildrenCount(),diagnosaSakit);
                                dataKasus.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });
                    }
                }
                else if(diagnosaSakit.equals("Flu"))
                {
                    if(nilai_kemiripan > treshold*100)
                    {
                        DatabaseReference save_diagnosis = FirebaseDatabase.getInstance().getReference().child("Diagnosa").push();

                        save_diagnosis.child("foto_diagnosa").setValue("https://firebasestorage.googleapis.com/v0/b/diagnosisdokter.appspot.com/o/Foto%20Profil%2Fhist_diagnosis.png?alt=media&token=edefaf31-b988-46f2-93b4-3a08ad79de53");
                        save_diagnosis.child("kategori_penyakit").setValue("Ringan");
                        save_diagnosis.child("penyakit").setValue(diagnosaSakit);

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        save_diagnosis.child("user_id").setValue(userId);

                        SimpleDateFormat format_tanggal = new SimpleDateFormat("dd MMMM yyyy");
                        SimpleDateFormat format_waktu = new SimpleDateFormat("HH:MM:SS");
                        Date date = new Date();

                        String tanggal = format_tanggal.format(date);
                        String waktu = format_waktu.format(date);

                        save_diagnosis.child("tanggal_diagnosa").setValue(tanggal);
                        save_diagnosis.child("waktu_diagnosa").setValue(waktu);

                        Intent intent = new Intent(Diagnosis.this, HasilRingan.class);
                        Bundle extras = new Bundle();
                        extras.putString("sakitMirip", diagnosaSakit);
                        extras.putString("catatan", "Gejala yang anda alami mengarah pada diagnosis untuk penyakit "+diagnosaSakit+" dengan nilai diagnosis kemiripan sebesar "+nilai_kemiripan+"%");
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                    else
                    {
                        final DatabaseReference dataKasus = FirebaseDatabase.getInstance().getReference().child("Kasus");
                        dataKasus.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                save_kasus_baru_ringan(dataSnapshot.getChildrenCount(),diagnosaSakit);
                                dataKasus.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(Diagnosis.this, "Upss... data tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void save_kasus_baru_berat(long childrenCount, String diagnosaSakit)
    {
        int nomer_kasus_baru = (int) (childrenCount+1);
        String kode_kasus_baru = "K"+Integer.toString(nomer_kasus_baru);

        DatabaseReference save_kasus = FirebaseDatabase.getInstance().getReference().child("Kasus").child(kode_kasus_baru);

        int jumlah_gejala_user = kabehKode.size();
        for(int i=0;i<jumlah_gejala_user;i++)
        {
            save_kasus.child("Gejala").child(""+i).setValue(kabehKode.get(i));
            System.out.println(kabehKode.get(i));
        }
        save_kasus.child("sakit").setValue(diagnosaSakit);

        Intent intent = new Intent(Diagnosis.this, HasilBerat.class);
        Bundle extras = new Bundle();
        extras.putString("sakitMirip", diagnosaSakit);
        extras.putString("catatan", "Gejala yang anda alami merupakan kasus baru yang sistem kami temui, dengan nilai diagnosis terhadap penyakit "+diagnosaSakit+" sebesar "+nilai_kemiripan+"%");
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    private void save_kasus_baru_ringan(long childrenCount, String diagnosaSakit)
    {
        int nomer_kasus_baru = (int) (childrenCount+1);
        String kode_kasus_baru = "K"+Integer.toString(nomer_kasus_baru);

        DatabaseReference save_kasus = FirebaseDatabase.getInstance().getReference().child("Kasus").child(kode_kasus_baru);

        int jumlah_gejala_user = kabehKode.size();
        for(int i=0;i<jumlah_gejala_user;i++)
        {
            save_kasus.child("Gejala").child(""+i).setValue(kabehKode.get(i));
            System.out.println(kabehKode.get(i));
        }
        save_kasus.child("sakit").setValue(diagnosaSakit);

        Intent intent = new Intent(Diagnosis.this, HasilRingan.class);
        Bundle extras = new Bundle();
        extras.putString("sakitMirip", diagnosaSakit);
        extras.putString("catatan", "Gejala yang anda alami merupakan kasus baru yang sistem kami temui, dengan nilai diagnosis terhadap penyakit "+diagnosaSakit+" sebesar "+nilai_kemiripan+"%");
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }
}
