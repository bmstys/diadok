package com.dinosoft.diadok;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PilihJadwal extends AppCompatActivity
{
    private String user_id;
    private String pasien_id;
    private String dokter_id;
    private String spesialis_tujuan =  "Spesialis Penyakit Dalam";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabaseDokter;
    private DatabaseReference mDatabaseJadwalDokter;
    private DatabaseReference mDatabaseSavePesanan;
    private StorageReference mStorageDokter;
    private Query mQueryDokter;
    private Query mQueryJadwalDokter;
    private String URL;

    private TextView nama,spesialis,no_telfon,alamat,status;
    private CircleImageView foto;

    private RadioGroup jadwal_group;
    private RadioButton jadwal_1;
    private RadioButton jadwal_2;
    private RadioButton jadwal_3;

    private String idJadwal1;
    private String idJadwal2;
    private String idJadwal3;

    private String jadwal_id_dipilih;

    private ProgressDialog mProgress;

    private TextView mDisplayErrorRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_jadwal);

        Intent ambil_intent = getIntent();
        Bundle yang_dikirim = ambil_intent.getExtras();
        pasien_id = yang_dikirim.getString("pasienId");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        user_id = user.getUid();

        mDatabaseDokter = FirebaseDatabase.getInstance().getReference().child("Dokter");
        mQueryDokter = mDatabaseDokter.orderByChild("spesialis_dokter").equalTo(spesialis_tujuan);
        mStorageDokter = FirebaseStorage.getInstance().getReference().child("Foto Profil");

        mDatabaseSavePesanan = FirebaseDatabase.getInstance().getReference().child("Booking");

        mProgress = new ProgressDialog(this);

        nama = (TextView)findViewById(R.id.dokter_nama);
        spesialis = (TextView)findViewById(R.id.dokter_spesialis);
        no_telfon = (TextView)findViewById(R.id.dokter_no_telfon);
        status = (TextView)findViewById(R.id.dokter_status);
        alamat = (TextView)findViewById(R.id.dokter_alamat);
        foto = (CircleImageView)findViewById(R.id.dokter_foto);

        jadwal_group = (RadioGroup)findViewById(R.id.radio_grup_jadwal);
        jadwal_1 = (RadioButton)findViewById(R.id.jadwal1);
        jadwal_2 = (RadioButton)findViewById(R.id.jadwal2);
        jadwal_3 = (RadioButton)findViewById(R.id.jadwal3);

        mDisplayErrorRadio = (TextView)findViewById(R.id.alertViewJadwal);

        show_data();
    }

    private void show_data()
    {
        mQueryDokter.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    for(DataSnapshot dataDokter : dataSnapshot.getChildren())
                    {
                        DataDokter dokter = dataDokter.getValue(DataDokter.class);

                        String dok_nama = dokter.getNama_dokter();
                        String dok_alamat = dokter.getAlamat_dokter();
                        String dok_spesialis = dokter.getSpesialis_dokter();
                        String dok_status = dokter.getStatus_dokter();
                        String dok_no_telfon = dokter.getNomor_dokter();

                        nama.setText(dok_nama);
                        spesialis.setText(": "+dok_spesialis);
                        no_telfon.setText(": "+dok_no_telfon);
                        status.setText(": "+dok_status);
                        alamat.setText(": "+dok_alamat);

                        final String foto_dokter = dokter.getFoto_dokter();

                        mStorageDokter = mStorageDokter.child(foto_dokter);
                        mStorageDokter.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                        {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                URL = uri.toString();

                                show_foto(URL);
                            }
                        });

                        dokter_id = dataDokter.getKey();

                        cari_jadwal(dokter_id);
                    }
                }
                else
                {
                    Toast.makeText(PilihJadwal.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
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

    private void cari_jadwal(String id)
    {
        String idDokter = id;

        mDatabaseJadwalDokter = FirebaseDatabase.getInstance().getReference().child("Jadwal");
        mQueryJadwalDokter = mDatabaseJadwalDokter.orderByChild("dokter_id").equalTo(idDokter);

        mQueryJadwalDokter.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ArrayList<String> arr_jadwalId = new ArrayList<>();
                ArrayList<String> arr_hari = new ArrayList<>();
                ArrayList<String> arr_lokasi = new ArrayList<>();
                ArrayList<String> arr_mulai = new ArrayList<>();
                ArrayList<String> arr_akhir = new ArrayList<>();

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dataJadwal : dataSnapshot.getChildren())
                    {
                        DataJadwal data = dataJadwal.getValue(DataJadwal.class);

                        arr_jadwalId.add(dataJadwal.getKey());
                        arr_hari.add(data.getHari());
                        arr_lokasi.add(data.getLokasi());
                        arr_mulai.add(data.getWaktu_mulai());
                        arr_akhir.add(data.getWaktu_akhir());
                    }

                    show_jadwal(arr_jadwalId,arr_hari,arr_lokasi,arr_mulai,arr_akhir);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void show_jadwal(ArrayList<String> arr_jadwalId,ArrayList<String> arr_hari,ArrayList<String> arr_lokasi,ArrayList<String> arr_mulai,ArrayList<String> arr_akhir)
    {
        idJadwal1 = arr_jadwalId.get(0);
        idJadwal2 = arr_jadwalId.get(1);
        idJadwal3 = arr_jadwalId.get(2);

        jadwal_1.setText(arr_hari.get(0)+" : "+arr_mulai.get(0)+"-"+arr_akhir.get(0)+" ("+arr_lokasi.get(0)+")");
        jadwal_2.setText(arr_hari.get(1)+" : "+arr_mulai.get(1)+"-"+arr_akhir.get(1)+" ("+arr_lokasi.get(1)+")");
        jadwal_3.setText(arr_hari.get(2)+" : "+arr_mulai.get(2)+"-"+arr_akhir.get(2)+" ("+arr_lokasi.get(2)+")");

        jadwal_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID)
            {
                switch(radioButtonID)
                {
                    case R.id.jadwal1:
                        jadwal_id_dipilih = idJadwal1;
                        break;
                    case R.id.jadwal2:
                        jadwal_id_dipilih = idJadwal2;
                        break;
                    case R.id.jadwal3:
                        jadwal_id_dipilih = idJadwal3;
                        break;
                }
            }
        });
    }

    public void pesan(View view)
    {
        if (jadwal_group.getCheckedRadioButtonId() == -1)
        {
            mDisplayErrorRadio.setError("Anda Belum Memilih Jadwal!");
            mDisplayErrorRadio.requestFocus();
            return;
        }
        else
        {
            mProgress.setMessage("Loading ...");
            mProgress.show();

            cariDatanyaJadwal();
        }
    }

    private void cariDatanyaJadwal()
    {
        DatabaseReference Data_Jadwal = FirebaseDatabase.getInstance().getReference().child("Jadwal").child(jadwal_id_dipilih);
        Data_Jadwal.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                DataJadwal data_Jadwal = dataSnapshot.getValue(DataJadwal.class);

                String harii = data_Jadwal.getHari();
                String lokasii = data_Jadwal.getLokasi();
                String waktuMull = data_Jadwal.getWaktu_mulai();

                hitung_no_urut(harii,lokasii,waktuMull);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void hitung_no_urut(String harii, String lokasii, String waktuMull)
    {
        final String hari = harii;
        final String lokasi = lokasii;
        final String waktuMul = waktuMull;

        Query ada_pesanan = FirebaseDatabase.getInstance().getReference().child("Booking").orderByChild("jadwal_id").equalTo(jadwal_id_dipilih);
        ada_pesanan.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int jumlah_data_found;
                int no_urutt;

                if(dataSnapshot.exists())
                {
                    jumlah_data_found = (int) dataSnapshot.getChildrenCount();

                    no_urutt = jumlah_data_found + 1;

                    save_data(hari,lokasi,waktuMul,no_urutt);
                }
                else
                {
                    no_urutt = 1;
                    save_data(hari,lokasi,waktuMul,no_urutt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void save_data(String hari, String lokasi, String waktuMul, int no_urutt)
    {
        DatabaseReference save_pesanan = mDatabaseSavePesanan.push();

        String namaDokter = nama.getText().toString().trim();
        String pasienId = pasien_id;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String jadwalId = jadwal_id_dipilih;
        String noUrut = Integer.toString(no_urutt);

        save_pesanan.child("nama_dokter").setValue(namaDokter);
        save_pesanan.child("pasien_id").setValue(pasienId);
        save_pesanan.child("user_id").setValue(userId);
        save_pesanan.child("jadwal_id").setValue(jadwalId);

        // Untuk tanggal
        Calendar now = Calendar.getInstance();
        String tanggal = "";

        DateFormat format_tanggal = new SimpleDateFormat("dd MMMM yyyy");

        if(hari.equals("Minggu"))
        {
            if(now.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            {
                while(now.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                {
                    now.add(Calendar.DATE, 1);
                }
                tanggal = format_tanggal.format(now.getTime());
            }
            else
            {
                now.add(Calendar.DATE, 7);
                tanggal = format_tanggal.format(now.getTime());
            }
        }
        else if(hari.equals("Senin"))
        {
            if(now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
            {
                while(now.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                {
                    now.add(Calendar.DATE, 1);
                }
                tanggal = format_tanggal.format(now.getTime());
            }
            else
            {
                now.add(Calendar.DATE, 7);
                tanggal = format_tanggal.format(now.getTime());
            }
        }
        else if(hari.equals("Selasa"))
        {
            if(now.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY)
            {
                while(now.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY)
                {
                    now.add(Calendar.DATE, 1);
                }
                tanggal = format_tanggal.format(now.getTime());
            }
            else
            {
                now.add(Calendar.DATE, 7);
                tanggal = format_tanggal.format(now.getTime());
            }
        }
        else if(hari.equals("Rabu"))
        {
            if(now.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY)
            {
                while(now.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY)
                {
                    now.add(Calendar.DATE, 1);
                }
                tanggal = format_tanggal.format(now.getTime());
            }
            else
            {
                now.add(Calendar.DATE, 7);
                tanggal = format_tanggal.format(now.getTime());
            }
        }
        else if(hari.equals("Kamis"))
        {
            if(now.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
            {
                while(now.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
                {
                    now.add(Calendar.DATE, 1);
                }
                tanggal = format_tanggal.format(now.getTime());
            }
            else
            {
                now.add(Calendar.DATE, 7);
                tanggal = format_tanggal.format(now.getTime());
            }
        }
        else if(hari.equals("Jumat"))
        {
            if(now.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
            {
                while(now.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
                {
                    now.add(Calendar.DATE, 1);
                }
                tanggal = format_tanggal.format(now.getTime());
            }
            else
            {
                now.add(Calendar.DATE, 7);
                tanggal = format_tanggal.format(now.getTime());
            }
        }
        else if(hari.equals("Sabtu"))
        {
            if(now.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
            {
                while(now.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                {
                    now.add(Calendar.DATE, 1);
                }
                tanggal = format_tanggal.format(now.getTime());
            }
            else
            {
                now.add(Calendar.DATE, 7);
                tanggal = format_tanggal.format(now.getTime());
            }
        }

        save_pesanan.child("tanggal").setValue(tanggal);
        save_pesanan.child("hari").setValue(hari);
        save_pesanan.child("lokasi").setValue(lokasi);
        save_pesanan.child("waktu").setValue(waktuMul);
        save_pesanan.child("no_urut").setValue(noUrut);

        String id_booking = save_pesanan.getKey();

        mProgress.dismiss();

        Intent intent = new Intent(this, SuksesPesan.class);
        intent.putExtra("id_booking",id_booking);
        startActivity(intent);
        finish();
    }
}
