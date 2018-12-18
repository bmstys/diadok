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

public class RiwayatTransaksi extends AppCompatActivity
{
    private RecyclerView mTransaksiList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseCurrentUser;
    private FirebaseAuth mAuth;
    private Query mQueryCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_transaksi);

        mTransaksiList = (RecyclerView)findViewById(R.id.transaksi_list);
        mTransaksiList.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Transaksi");

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Transaksi");
        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("user_id").equalTo(current_user_id);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<DataTransaksi,TransaksiViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<DataTransaksi, TransaksiViewHolder>
        (
                DataTransaksi.class,
                R.layout.transaksi_row,
                TransaksiViewHolder.class,
                mQueryCurrentUser
        )
        {
            @Override
            protected void populateViewHolder(TransaksiViewHolder viewHolder, DataTransaksi model, int position)
            {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNominal(model.getNominal());
                viewHolder.setTanggal(model.getTanggal());
                viewHolder.setWaktu(model.getWaktu());
                viewHolder.setFoto(getApplicationContext(), model.getFoto());
            }
        };

        mTransaksiList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class TransaksiViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public TransaksiViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title)
        {
            TextView history_title = (TextView)mView.findViewById(R.id.hist_title);
            history_title.setText(title);
        }

        public void setNominal(String nominal)
        {
            TextView history_nominal = (TextView)mView.findViewById(R.id.hist_nominal);

            int nominalnya = Integer.parseInt(nominal);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            history_nominal.setText(": "+kursIndonesia.format(nominalnya));
        }

        public void setWaktu(String waktu)
        {
            TextView history_waktu = (TextView)mView.findViewById(R.id.hist_waktu);
            history_waktu.setText(": "+waktu);
        }

        public void setTanggal(String tanggal)
        {
            TextView history_tanggal = (TextView)mView.findViewById(R.id.hist_tanggal);
            history_tanggal.setText(": "+tanggal);
        }

        public void setFoto(Context ctx, String foto)
        {
            ImageView history_foto = (ImageView)mView.findViewById(R.id.hist_foto);
            Glide.with(ctx).load(foto).into(history_foto);
        }
    }
}
