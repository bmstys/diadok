package com.dinosoft.diadok;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class RiwayatDiagnosis extends AppCompatActivity
{
    private RecyclerView mDiagnosaList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseCurrentUser;
    private FirebaseAuth mAuth;
    private Query mQueryCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_diagnosis);

        mDiagnosaList = (RecyclerView)findViewById(R.id.diagnosis_list);
        mDiagnosaList.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Transaksi");

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Diagnosa");
        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("user_id").equalTo(current_user_id);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<DataDiagnosa,RiwayatDiagnosis.DiagnosisViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DataDiagnosa, RiwayatDiagnosis.DiagnosisViewHolder>
                (
                        DataDiagnosa.class,
                        R.layout.diagnosis_row,
                        RiwayatDiagnosis.DiagnosisViewHolder.class,
                        mQueryCurrentUser
                )
        {
            @Override
            protected void populateViewHolder(RiwayatDiagnosis.DiagnosisViewHolder viewHolder, DataDiagnosa model, int position)
            {
                viewHolder.setKategori_penyakit(model.getKategori_penyakit());
                viewHolder.setPenyakit(model.getPenyakit());
                viewHolder.setTanggal_diagnosa(model.getTanggal_diagnosa());
                viewHolder.setWaktu_diagnosa(model.getWaktu_diagnosa());
                viewHolder.setFoto_diagnosa(getApplicationContext(), model.getFoto_diagnosa());
            }
        };

        mDiagnosaList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class DiagnosisViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public DiagnosisViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;
        }

        public void setKategori_penyakit(String kategori_penyakit)
        {
            TextView distory_kategori = (TextView) mView.findViewById(R.id.dist_kategori_penyakit);
            distory_kategori.setText(": " +kategori_penyakit);
        }

        public void setPenyakit(String penyakit)
        {
            TextView distory_penyakit = (TextView) mView.findViewById(R.id.dist_penyakit);
            distory_penyakit.setText(": " +penyakit);
        }

        public void setWaktu_diagnosa(String waktu_diagnosa)
        {
            TextView distory_waktu = (TextView) mView.findViewById(R.id.dist_waktu_diagnosa);
            distory_waktu.setText(": " + waktu_diagnosa);
        }

        public void setTanggal_diagnosa(String tanggal_diagnosa)
        {
            TextView distory_tanggal = (TextView) mView.findViewById(R.id.dist_tanggal_diagnosa);
            distory_tanggal.setText(": " + tanggal_diagnosa);
        }

        public void setFoto_diagnosa(Context ctx, String foto_diagnosa)
        {
            ImageView distory_foto = (ImageView) mView.findViewById(R.id.dist_foto);
            Glide.with(ctx).load(foto_diagnosa).into(distory_foto);
        }
    }
}
