package com.dinosoft.diadok;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class PesanDokter extends AppCompatActivity
{
    private EditText p_nama_lengkap,p_tempat_lahir,p_umur,p_alamat,p_riwayat_sakit;
    private RadioGroup jenis_kelamin_group;
    private RadioButton jenis_kelamin_button;

    private RadioGroup p_gol_group;
    private RadioButton p_goldarah;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    private String pasienId;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private String tanggal_lahir;

    private String diagnosa_sakit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan_dokter);

        Bundle extras = getIntent().getExtras();
        diagnosa_sakit = extras.getString("diagnosaSakit");

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pasien");

        p_nama_lengkap = (EditText)findViewById(R.id.ps_nama_lengkap);
        p_tempat_lahir = (EditText)findViewById(R.id.ps_tempat_lahir);
        p_umur = (EditText)findViewById(R.id.ps_umur);
        p_alamat = (EditText)findViewById(R.id.ps_alamat);
        p_riwayat_sakit = (EditText)findViewById(R.id.ps_riwayat_sakit);
        jenis_kelamin_group = (RadioGroup)findViewById(R.id.ps_jenis_kelamin);
        p_gol_group = (RadioGroup)findViewById(R.id.ps_goldar);

        // Untuk tanggal, ketika TV tanggal di pencet nanti muncul tanggal
        mDisplayDate = (TextView)findViewById(R.id.ps_tanggal_lahir);
        mDisplayDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar tanggalan = Calendar.getInstance();

                int tahun = tanggalan.get(Calendar.YEAR);
                int bulan = tanggalan.get(Calendar.MONTH);
                int tanggal = tanggalan.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        PesanDokter.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        tahun,bulan,tanggal
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int tahun, int bulan, int tanggal)
            {
                String thn = Integer.toString(tahun);
                String bln = Integer.toString(bulan+1);
                String tgl = Integer.toString(tanggal);

                if(bln.equals("1"))
                {
                    bln = "Januari";
                }
                else if(bln.equals("2"))
                {
                    bln = "Februari";
                }
                else if(bln.equals("3"))
                {
                    bln = "Maret";
                }
                else if(bln.equals("4"))
                {
                    bln = "April";
                }
                else if(bln.equals("5"))
                {
                    bln = "Mei";
                }
                else if(bln.equals("6"))
                {
                    bln = "Juni";
                }
                else if(bln.equals("7"))
                {
                    bln = "Juli";
                }
                else if(bln.equals("8"))
                {
                    bln = "Agustus";
                }
                else if(bln.equals("9"))
                {
                    bln = "September";
                }
                else if(bln.equals("10"))
                {
                    bln = "Oktober";
                }
                else if(bln.equals("11"))
                {
                    bln = "November";
                }
                else if(bln.equals("12"))
                {
                    bln = "Desember";
                }

                tanggal_lahir = tgl+" "+bln+" "+thn;

                mDisplayDate.setText(tanggal_lahir);
            }
        };
    }

    public void daftar_pasien(View view)
    {
        validasi_form_pendaftaran();
    }

    private void validasi_form_pendaftaran()
    {
        String nama_lengkap = p_nama_lengkap.getText().toString().trim();
        String tempat_lahir = p_tempat_lahir.getText().toString().trim();
        String umur = p_umur.getText().toString().trim();
        String alamat = p_alamat.getText().toString().trim();
        String riwayat_sakit = p_riwayat_sakit.getText().toString().trim();
        int jk_id = jenis_kelamin_group.getCheckedRadioButtonId();
        jenis_kelamin_button = (RadioButton)findViewById(jk_id);
        String jenis_kelamin = jenis_kelamin_button.getText().toString().trim();
        int goldar_id = p_gol_group.getCheckedRadioButtonId();
        p_goldarah = (RadioButton)findViewById(goldar_id);
        String golongan_darah = p_goldarah.getText().toString().trim();


        if(nama_lengkap.isEmpty())
        {
            p_nama_lengkap.setError("Mohon isikan nama lengkap pasien!");
            p_nama_lengkap.requestFocus();
            return;
        }

        if(tempat_lahir.isEmpty())
        {
            p_tempat_lahir.setError("Mohon isikan tempat lahir pasien!");
            p_tempat_lahir.requestFocus();
            return;
        }

        if (tanggal_lahir == null)
        {
            mDisplayDate.setError("Anda belum mengisi tanggal lahir!");
            mDisplayDate.requestFocus();
            return;
        }

        if(umur.isEmpty())
        {
            p_umur.setError("Mohon isikan umur pasien!");
            p_umur.requestFocus();
            return;
        }

        if(umur.length() > 2)
        {
            p_umur.setError("Umur anda terlalu tua untuk membuat akun ini!");
            p_umur.requestFocus();
            return;
        }

        if(alamat.isEmpty())
        {
            p_alamat.setError("Mohon isikan alamat pasien!");
            p_alamat.requestFocus();
            return;
        }

        if(riwayat_sakit.isEmpty())
        {
            p_riwayat_sakit.setError("Mohon isikan riwayat penyakit pasien!");
            p_riwayat_sakit.requestFocus();
            return;
        }

        if(!nama_lengkap.isEmpty() && !tempat_lahir.isEmpty() && tanggal_lahir != null && !umur.isEmpty() && !golongan_darah.isEmpty() && !alamat.isEmpty() && !riwayat_sakit.isEmpty())
        {
            mProgress.setMessage("Loading ...");
            mProgress.show();

            save_pendaftaran(nama_lengkap,tempat_lahir,tanggal_lahir,umur,golongan_darah,alamat,riwayat_sakit,jenis_kelamin);
            return;
        }
    }

    private void save_pendaftaran(String nama_lengkap, String tempat_lahir, String tanggal_lahir, String umur, String golongan_darah, String alamat, String riwayat_sakit, String jenis_kelamin)
    {
        String nama_lengkap_pasien = nama_lengkap;
        String tempat_lahir_pasien = tempat_lahir;
        String tanggal_lahir_pasien = tanggal_lahir;
        String umur_pasien = umur;
        String golongan_darah_pasien = golongan_darah;
        String alamat_pasien = alamat;
        String riwayat_sakit_pasien = riwayat_sakit;
        String jenis_kelamin_pasien = jenis_kelamin;

        String user_id = mAuth.getCurrentUser().getUid();

        DatabaseReference save_pendaftaran = mDatabase.push();

        save_pendaftaran.child("nama_lengkap").setValue(nama_lengkap_pasien);
        save_pendaftaran.child("diagnosa_sakit").setValue(diagnosa_sakit);
        save_pendaftaran.child("tempat_lahir").setValue(tempat_lahir_pasien);
        save_pendaftaran.child("tanggal_lahir").setValue(tanggal_lahir_pasien);
        save_pendaftaran.child("umur").setValue(umur_pasien);
        save_pendaftaran.child("golongan_darah").setValue(golongan_darah_pasien);
        save_pendaftaran.child("alamat").setValue(alamat_pasien);
        save_pendaftaran.child("riwayat_sakit").setValue(riwayat_sakit_pasien);
        save_pendaftaran.child("jenis_kelamin").setValue(jenis_kelamin_pasien);
        save_pendaftaran.child("user_id").setValue(user_id);

        pasienId = save_pendaftaran.getKey();

        mProgress.dismiss();

        Intent intent = new Intent(this, PilihJadwal.class);
        intent.putExtra("pasienId",pasienId);
        startActivity(intent);
    }
}
