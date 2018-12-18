package com.dinosoft.diadok;

import android.app.ProgressDialog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TopUp extends AppCompatActivity
{
    private EditText voucher;

    private FirebaseAuth mAuth;
    private FirebaseUser usernya;
    private DatabaseReference mDatabaseWallet;
    private DatabaseReference mDatabaseUser;

    private String userIdnya;

    private ProgressDialog mProgress;

    private boolean no_error = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        usernya = mAuth.getCurrentUser();
        userIdnya = usernya.getUid();

        voucher = (EditText)findViewById(R.id.top_up_voucher);

        mDatabaseWallet = FirebaseDatabase.getInstance().getReference().child("Transaksi");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userIdnya);
    }

    public void top_up(View view)
    {
        validasi_voucher();

        if(no_error)
        {
            Toast.makeText(getApplicationContext(), "Top up sukses, saldo anda bertambah", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, VirtualWallet.class);
            startActivity(intent);
            finish();
        }
    }

    private void validasi_voucher()
    {
        String kode_voucher = voucher.getText().toString().trim();

        String kode1 = "2121212121";
        String kode2 = "1111111111";
        String kode3 = "2222222222";
        String kode4 = "3333333333";
        String kode5 = "4444444444";
        String kode6 = "5555555555";

        if(kode_voucher.isEmpty())
        {
            voucher.setError("Isikan kode voucher disini!");
            voucher.requestFocus();
            no_error = false;
            return;
        }

        if(kode_voucher.length() != 10)
        {
            voucher.setError("Kode voucher harus terdiri dari 10 digit!");
            voucher.requestFocus();
            no_error = false;
            return;
        }

        if(!kode_voucher.isEmpty())
        {
            if(kode_voucher.equals(kode1))
            {
                int tambah = 1000000;

                verifikasi_voucher(tambah);

                no_error = true;
                return;
            }
            else if(kode_voucher.equals(kode2))
            {
                int tambah = 100000;

                verifikasi_voucher(tambah);

                no_error = true;
                return;
            }
            else if(kode_voucher.equals(kode3))
            {
                int tambah = 200000;

                verifikasi_voucher(tambah);

                no_error = true;
                return;
            }
            else if(kode_voucher.equals(kode4))
            {
                int tambah = 300000;

                verifikasi_voucher(tambah);

                no_error = true;
                return;
            }
            else if(kode_voucher.equals(kode5))
            {
                int tambah = 400000;

                verifikasi_voucher(tambah);

                no_error = true;
                return;
            }
            else if(kode_voucher.equals(kode6))
            {
                int tambah = 500000;

                verifikasi_voucher(tambah);

                no_error = true;
                return;
            }
            else
            {
                voucher.setError("Kode voucher yang anda masukkan salah");
                voucher.requestFocus();
                no_error = false;
                return;
            }
        }
    }

    private void verifikasi_voucher(int tambah)
    {
        mProgress.setMessage("Loading ...");
        mProgress.show();

        final int jumlah = tambah;

        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String saldonya = dataSnapshot.child("saldo").getValue().toString();

                save_database(saldonya,jumlah);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void save_database(String saldo_a,int jumlah_tu)
    {
        int sal = Integer.parseInt(saldo_a);

        final int saldo_a_u = sal;
        final int jumlah_t_u = jumlah_tu;

        int saldo_w_u = saldo_a_u + jumlah_t_u;

        String jumlah_top_up_user = Integer.toString(jumlah_t_u);
        String saldo_wallet_user = Integer.toString(saldo_w_u);

        DatabaseReference save_top_up = mDatabaseWallet.push();
        DatabaseReference update_saldo = FirebaseDatabase.getInstance().getReference().child("Users").child(userIdnya);

        SimpleDateFormat format_tanggal = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat format_waktu = new SimpleDateFormat("HH:MM:SS");
        Date date = new Date();

        String tanggal = format_tanggal.format(date);
        String waktu = format_waktu.format(date);

        save_top_up.child("title").setValue("Top Up Virtual Wallet");
        save_top_up.child("user_id").setValue(userIdnya);
        save_top_up.child("nominal").setValue(jumlah_top_up_user);
        save_top_up.child("tanggal").setValue(tanggal);
        save_top_up.child("waktu").setValue(waktu);
        save_top_up.child("foto").setValue("https://firebasestorage.googleapis.com/v0/b/diagnosisdokter.appspot.com/o/Foto%20Profil%2Fhist_wallet.png?alt=media&token=e2c790ee-6529-4057-815c-166580cd090e");

        update_saldo.child("saldo").setValue(saldo_wallet_user);

        mProgress.dismiss();
    }
}
