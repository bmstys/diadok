package com.dinosoft.diadok;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class BeliObat extends AppCompatActivity
{
    private RecyclerView mObatList;
    private DatabaseReference mDatabaseObat;
    private FirebaseUser usernya;
    private String userIdnya;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Query mQueryObat;

    private String penyakit_id ;

    private int total_harga = 0;
    private int saldoUser;
    private String saldonya;

    private TextView totalHarga;

    private FusedLocationProviderClient client;

    private double LatUser;
    private double LongUser;

    private double LatApotekUII = -7.6870825;
    private double LongApotekUII = 110.4182501;

    private double LatApotekKimiaFarmaPalagan = -7.7436455;
    private double LongApotekKimiaFarmaPalagan = 110.3756178;

    private double LatApotekPharm24 = -7.7014167;
    private double LongApotekPharm24 = 110.4147454;

    private int JarakKeApotekUII;
    private int JarakKeApotekKimiaFarmaPalagan;
    private int JarakKeApotekPharm24;
    private int JarakTerdekat;

    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beli_obat);

        mAuth = FirebaseAuth.getInstance();
        usernya = mAuth.getCurrentUser();
        userIdnya = usernya.getUid();

        mObatList = (RecyclerView)findViewById(R.id.obat_list);
        mObatList.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        penyakit_id = extras.getString("penyakit_id");

        mDatabaseObat = FirebaseDatabase.getInstance().getReference().child("Obat");
        mQueryObat = mDatabaseObat.orderByChild("penyakit_id").equalTo(penyakit_id);

        totalHarga = (TextView)findViewById(R.id.obat_total);

        get_data_total();

        requestPermission();

        client = LocationServices.getFusedLocationProviderClient(this);

        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(BeliObat.this,new String[]{ACCESS_FINE_LOCATION},1);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<DataObat,BeliObat.ObatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DataObat, BeliObat.ObatViewHolder>
                (
                        DataObat.class,
                        R.layout.obat_row,
                        BeliObat.ObatViewHolder.class,
                        mQueryObat
                )
        {
            @Override
            protected void populateViewHolder(BeliObat.ObatViewHolder viewHolder, DataObat model, int position)
            {
                viewHolder.setNama_obat(model.getNama_obat());
                viewHolder.setHarga(model.getHarga());
            }
        };

        mObatList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ObatViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public ObatViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;
        }

        public void setNama_obat(String nama_obat)
        {
            TextView obat_nama = (TextView)mView.findViewById(R.id.obat_nama);
            obat_nama.setText(nama_obat);
        }

        public void setHarga(String harga)
        {
            TextView obat_harga = (TextView)mView.findViewById(R.id.obat_harga);

            int harga_obat = Integer.parseInt(harga);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            obat_harga.setText(kursIndonesia.format(harga_obat));
        }
    }

    private void get_data_total()
    {
        mQueryObat.addValueEventListener(new ValueEventListener()
        {
            ArrayList<String> arr_harga = new ArrayList<>();
            int count_isi_array;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dataObat : dataSnapshot.getChildren())
                    {
                        DataObat data = dataObat.getValue(DataObat.class);

                        arr_harga.add(data.getHarga());
                    }

                    count_isi_array = arr_harga.size();

                    hitung_total(arr_harga,count_isi_array);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void hitung_total(ArrayList<String> arr_harga, int count_isi_array)
    {
        int jumlah = count_isi_array;
        int[] harganya = new int[jumlah];

        for(int i=0;i<jumlah;i++)
        {
            harganya[i] = Integer.parseInt(arr_harga.get(i));

            total_harga += harganya[i];
        }

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);

        totalHarga.setText(kursIndonesia.format(total_harga));
    }

    public void bayar(View view)
    {
        cek_saldo();
    }

    private void cek_saldo()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userIdnya);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                saldonya = dataSnapshot.child("saldo").getValue().toString();

                saldoUser = Integer.parseInt(saldonya);

                if(saldoUser < total_harga)
                {
                    Toast.makeText(BeliObat.this, "Maaf saldo anda tidak mencukupi, mohon lakukan pengisian saldo terlebih dahulu untuk melakukan transaksi", Toast.LENGTH_SHORT).show();
                }
                else if(saldoUser > total_harga)
                {
                    int pengurangan_saldo = saldoUser-total_harga;
                    String saldo_akhir = Integer.toString(pengurangan_saldo);

                    DatabaseReference usernya = FirebaseDatabase.getInstance().getReference().child("Users").child(userIdnya);
                    usernya.child("saldo").setValue(saldo_akhir);

                    DatabaseReference riwayatTransaksi = FirebaseDatabase.getInstance().getReference().child("Transaksi");
                    DatabaseReference saveRiwayat = riwayatTransaksi.push();

                    saveRiwayat.child("foto").setValue("https://firebasestorage.googleapis.com/v0/b/diagnosisdokter.appspot.com/o/Foto%20Profil%2Fhist_obat.png?alt=media&token=b8fc06e6-8a57-43b1-9a31-f1e23c1640b7");
                    saveRiwayat.child("nominal").setValue(Integer.toString(total_harga));
                    saveRiwayat.child("user_id").setValue(userIdnya);
                    saveRiwayat.child("title").setValue("Pembelian Obat");

                    SimpleDateFormat format_tanggal = new SimpleDateFormat("dd MMMM yyyy");
                    SimpleDateFormat format_waktu = new SimpleDateFormat("HH:MM:SS");
                    Date date = new Date();

                    String tanggal = format_tanggal.format(date);
                    String waktu = format_waktu.format(date);

                    saveRiwayat.child("waktu").setValue(waktu);
                    saveRiwayat.child("tanggal").setValue(tanggal);

                    get_location();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void get_location()
    {
        if (ActivityCompat.checkSelfPermission(BeliObat.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        client.getLastLocation().addOnSuccessListener(BeliObat.this, new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                if (location != null)
                {
                    LatUser = location.getLatitude();
                    LongUser = location.getLongitude();

                    distanceApotekUII(LatUser,LongUser,LatApotekUII,LongApotekUII);
                    distanceKimiaFarmaPalagan(LatUser,LongUser,LatApotekKimiaFarmaPalagan,LongApotekKimiaFarmaPalagan);
                    distancePharm24Besi(LatUser,LongUser,LatApotekPharm24,LongApotekPharm24);

                    getJarakTerdekat();
                }
            }
        });
    }

    private void distanceApotekUII(double LatU, double LongU, double LatAUII, double LongAUII)
    {
        double LatitudeUser = LatU;
        double LongitudeUser = LongU;
        double LatitudeApotekUII = LatAUII;
        double LongitudeApotekUII = LongAUII;

        if ((LatitudeUser == LatitudeApotekUII) && (LongitudeUser == LongitudeApotekUII))
        {
            JarakKeApotekUII = 0;
        }
        else
        {
            double theta = LongitudeUser - LongitudeApotekUII;
            double dist = Math.sin(Math.toRadians(LatitudeUser)) * Math.sin(Math.toRadians(LatitudeApotekUII)) + Math.cos(Math.toRadians(LatitudeUser)) * Math.cos(Math.toRadians(LatitudeApotekUII)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            dist = dist * 1000;
            JarakKeApotekUII = (int) dist;
        }
    }

    private void distanceKimiaFarmaPalagan(double LatU, double LongU, double Lat2, double Long2)
    {
        double LatitudeUser = LatU;
        double LongitudeUser = LongU;
        double LatitudeKimiaFarma = Lat2;
        double LongitudeKimiaFarma = Long2;

        if ((LatitudeUser == LatitudeKimiaFarma) && (LongitudeUser == LongitudeKimiaFarma))
        {
            JarakKeApotekKimiaFarmaPalagan = 0;
        }
        else
        {
            double theta = LongitudeUser - LongitudeKimiaFarma;
            double dist = Math.sin(Math.toRadians(LatitudeUser)) * Math.sin(Math.toRadians(LatitudeKimiaFarma)) + Math.cos(Math.toRadians(LatitudeUser)) * Math.cos(Math.toRadians(LatitudeKimiaFarma)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            dist = dist * 1000;
            JarakKeApotekKimiaFarmaPalagan = (int) dist;
        }
    }

    private void distancePharm24Besi(double LatU, double LongU, double LatPharm, double LongPharm)
    {
        double LatitudeUser = LatU;
        double LongitudeUser = LongU;
        double LatitudePharm = LatPharm;
        double LongitudePharm = LongPharm;

        if ((LatitudeUser == LatitudePharm) && (LongitudeUser == LongitudePharm))
        {
            JarakKeApotekPharm24 = 0;
        }
        else
        {
            double theta = LongitudeUser - LongitudePharm;
            double dist = Math.sin(Math.toRadians(LatitudeUser)) * Math.sin(Math.toRadians(LatitudePharm)) + Math.cos(Math.toRadians(LatitudeUser)) * Math.cos(Math.toRadians(LatitudePharm)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            dist = dist * 1000;
            JarakKeApotekPharm24 = (int) dist;
        }
    }

    private void getJarakTerdekat()
    {
        JarakTerdekat = Math.min(JarakKeApotekUII, Math.min(JarakKeApotekKimiaFarmaPalagan, JarakKeApotekPharm24));

        String apotekTerdekat = "";

        if(JarakTerdekat == JarakKeApotekUII)
        {
            apotekTerdekat = "Apotek Farma UII dengan jarak "+JarakTerdekat+" meter dari lokasi anda.";
        }
        else if(JarakTerdekat == JarakKeApotekKimiaFarmaPalagan)
        {
            apotekTerdekat = "Apotek Kimia Farma Palagan dengan jarak "+JarakTerdekat+" meter dari lokasi anda.";
        }
        else if(JarakTerdekat == JarakKeApotekPharm24)
        {
            apotekTerdekat = "Apotek Pharma Jakal Besi dengan jarak "+JarakTerdekat+" meter dari lokasi anda.";
        }

        Intent intent = new Intent(BeliObat.this, SuksesObat.class);
        Bundle extras = new Bundle();
        extras.putString("latUser", String.valueOf(LatUser));
        extras.putString("longUser", String.valueOf(LongUser));
        extras.putString("apotekTerdekat", apotekTerdekat);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
