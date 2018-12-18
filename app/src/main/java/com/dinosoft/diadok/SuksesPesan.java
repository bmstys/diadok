package com.dinosoft.diadok;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SuksesPesan extends AppCompatActivity
{
    private TextView info_noUrut,info_lokasi,info_dokter,info_waktu,info_tambahan,info_tanggal,info_hari;

    private String booking_id;

    private DatabaseReference mDatabaseBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sukses_pesan);

        Intent ambil_intent = getIntent();
        Bundle yang_dikirim = ambil_intent.getExtras();
        booking_id = yang_dikirim.getString("id_booking");

        info_noUrut = (TextView)findViewById(R.id.info_no_urut);
        info_lokasi = (TextView)findViewById(R.id.info_lokasi);
        info_dokter = (TextView)findViewById(R.id.info_dokter);
        info_waktu = (TextView)findViewById(R.id.info_waktu);
        info_tambahan = (TextView)findViewById(R.id.info_tambahan);
        info_tanggal = (TextView)findViewById(R.id.info_tanggal);
        info_hari = (TextView)findViewById(R.id.info_hari);

        mDatabaseBooking = FirebaseDatabase.getInstance().getReference().child("Booking").child(booking_id);

        show_booking();
    }

    private void show_booking()
    {
        mDatabaseBooking.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    DataBooking dataBooking = dataSnapshot.getValue(DataBooking.class);

                    info_noUrut.setText(": "+dataBooking.getNo_urut());
                    info_lokasi.setText(": "+dataBooking.getLokasi());
                    info_dokter.setText(": "+dataBooking.getNama_dokter());
                    info_hari.setText(": "+dataBooking.getHari());
                    info_tanggal.setText(": "+dataBooking.getTanggal());
                    info_waktu.setText(": "+dataBooking.getWaktu());
                    info_tambahan.setText("Mohon ingat informasi tersebut dengan cermat. Anda berobat dengan dokter "+dataBooking.getNama_dokter()+" pada pukul "+dataBooking.getWaktu()
                    +". Anda harus sudah berada di lokasi selambat lambatnya 15 menit sebelum waktu berobat. Apabila anda belum hadir di lokasi pada waktu yang sudah ditentukan, maka anda harus menunggu antrian yang sudah datang terlebih dahulu.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    public void mengerti_dokter(View view)
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}
