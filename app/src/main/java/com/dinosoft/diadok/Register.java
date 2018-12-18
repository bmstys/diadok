package com.dinosoft.diadok;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class Register extends AppCompatActivity
{
    // Inisiasi field
    private EditText e_username,e_password,e_nama_lengkap,e_no_telfon,e_email,e_alamat;
    private RadioGroup jenis_kelamin_group;
    private RadioButton jenis_kelamin_button;

    // Inisiasi untuk foto profil
    private ImageButton foto_profil;
    public static final int KITKAT_VALUE = 1002;
    private StorageReference mStorage;
    private Uri alamatGambar = null;

    // Inisiasi untuk kondisi error dan tidak error
    private boolean no_error = true;

    // Inisiasi untuk database dan authentication
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Inisiasi untuk progress bar
    private ProgressDialog mProgress;

    // Untuk tanggal
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private String tanggal_lahir;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Mengambil instance authentication
        mAuth = FirebaseAuth.getInstance();

        // Mengambil alamat url database dan membuat child (tabel) baru dengan nama Users
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // Mengambil instance storage
        mStorage = FirebaseStorage.getInstance().getReference();

        // Declare untuk progress bar
        mProgress = new ProgressDialog(this);

        // Declare untuk variabel fieldnya
        e_username = (EditText)findViewById(R.id.r_username);
        e_password = (EditText)findViewById(R.id.r_password);
        e_nama_lengkap = (EditText)findViewById(R.id.r_nama_lengkap);
        e_no_telfon = (EditText)findViewById(R.id.r_no_telfon);
        e_email = (EditText)findViewById(R.id.r_email);
        e_alamat = (EditText)findViewById(R.id.r_alamat);
        jenis_kelamin_group = (RadioGroup)findViewById(R.id.r_jenis_kelamin);

        //Declare untuk foto profil
        foto_profil = (ImageButton)findViewById(R.id.r_foto_profil);

        // Untuk tanggal, ketika TV tanggal di pencet nanti muncul tanggal
        mDisplayDate = (TextView)findViewById(R.id.r_tanggal_lahir);
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
                        Register.this,
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

    // Tombol Registrasi ketika di klik akan masuk ke daftar_akun()
    public void daftar_akun(View view)
    {
        // Data inputan divalidasi dulu
        validasi_registrasi();

        // Cek kondisi hasil validasinya, ada error atau engga, kalo engga yaa lanjut
        if(no_error)
        {
            // Untuk nampilin progress bar
            mProgress.setMessage("Loading ...");
            mProgress.show();

            start_registrasi();

            mProgress.dismiss();

            Intent intent = new Intent(this, Home.class);
            startActivity(intent);

            finish();
        }
    }

    // Untuk proses upload foto profil
    public void tombol_upload_foto(View view)
    {
        Intent intent;

        if(Build.VERSION.SDK_INT < 19)
        {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, KITKAT_VALUE);
        }
        else
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, KITKAT_VALUE);
        }
    }

    // Untuk dapetin alamat uri gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == KITKAT_VALUE && resultCode == RESULT_OK)
        {
            alamatGambar = data.getData();
            foto_profil.setImageURI(alamatGambar);
        }
    }

    // Proses untuk validasi inputannya
    private void validasi_registrasi()
    {
        String username = e_username.getText().toString().trim();
        String password = e_password.getText().toString().trim();
        String nama_lengkap = e_nama_lengkap.getText().toString().trim();
        String no_telfon = e_no_telfon.getText().toString().trim();
        String email = e_email.getText().toString().trim();
        String alamat = e_alamat.getText().toString().trim();
        int jk_id = jenis_kelamin_group.getCheckedRadioButtonId();
        jenis_kelamin_button = (RadioButton)findViewById(jk_id);
        String jenis_kelamin = jenis_kelamin_button.getText().toString().trim();

        if(username.isEmpty())
        {
            e_username.setError("Username harus diisi!");
            e_username.requestFocus();
            no_error = false;
            return;
        }

        if(nama_lengkap.isEmpty())
        {
            e_nama_lengkap.setError("Nama lengkap harus diisi!");
            e_nama_lengkap.requestFocus();
            no_error = false;
            return;
        }

        if (tanggal_lahir == null)
        {
            mDisplayDate.setError("Anda belum mengisi tanggal lahir!");
            mDisplayDate.requestFocus();
            no_error = false;
            return;
        }

        if(no_telfon.isEmpty())
        {
            e_no_telfon.setError("Nomor Telfon harus diisi!");
            e_no_telfon.requestFocus();
            no_error = false;
            return;
        }

        if(no_telfon.length() < 10)
        {
            e_no_telfon.setError("Nomor Telfon terdiri dari 10 sampai 12 digit!");
            e_no_telfon.requestFocus();
            no_error = false;
            return;
        }

        if(no_telfon.length() > 12)
        {
            e_no_telfon.setError("Nomor Telfon terdiri dari 10 sampai 12 digit!");
            e_no_telfon.requestFocus();
            no_error = false;
            return;
        }

        if(alamat.isEmpty())
        {
            e_alamat.setError("Alamat harus diisi!");
            e_alamat.requestFocus();
            no_error = false;
            return;
        }

        if(email.isEmpty())
        {
            e_email.setError("Email harus diisi!");
            e_email.requestFocus();
            no_error = false;
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            e_email.setError("Email yang anda isikan salah!");
            e_email.requestFocus();
            no_error = false;
            return;
        }

        if(password.isEmpty())
        {
            e_password.setError("Password harus diisi!");
            e_password.requestFocus();
            no_error = false;
            return;
        }

        if(password.length() < 6)
        {
            e_password.setError("Password harus terdiri dari minimal 6 digit!");
            e_password.requestFocus();
            no_error = false;
            return;
        }

        if(!alamat.isEmpty() && !tanggal_lahir.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !no_telfon.isEmpty() && !nama_lengkap.isEmpty() && tanggal_lahir != null)
        {
            no_error = true;
            return;
        }
    }

    // Untuk start registrasi atau bisa dibilang save database
    private void start_registrasi()
    {
        final String username = e_username.getText().toString().trim();
        final String password = e_password.getText().toString().trim();
        final String nama_lengkap = e_nama_lengkap.getText().toString().trim();
        final String no_telfon = e_no_telfon.getText().toString().trim();
        final String email = e_email.getText().toString().trim();
        final String alamat = e_alamat.getText().toString().trim();
        int jk_id = jenis_kelamin_group.getCheckedRadioButtonId();
        jenis_kelamin_button = (RadioButton)findViewById(jk_id);
        final String jenis_kelamin = jenis_kelamin_button.getText().toString().trim();

        // Cek biar ga ada yang kosong semua fieldnya (harus diisi semua field nya)
        if(!TextUtils.isEmpty(alamat) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(nama_lengkap) && !TextUtils.isEmpty(no_telfon) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(jenis_kelamin))
        {
            // Untuk membuat user baru dengan email dan password
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        save_register();
                    }
                }

                private void save_register()
                {
                    // Untuk get user id
                    String user_id = mAuth.getCurrentUser().getUid();

                    // Untuk nyimpen user_id sebagai judul tabel untuk tiap user
                    DatabaseReference usernya = mDatabase.child(user_id);

                    if(alamatGambar != null)
                    {
                        usernya.child("username").setValue(username);
                        usernya.child("nama_lengkap").setValue(nama_lengkap);
                        usernya.child("jenis_kelamin").setValue(jenis_kelamin);
                        usernya.child("no_telfon").setValue(no_telfon);
                        usernya.child("tanggal_lahir").setValue(tanggal_lahir);
                        usernya.child("alamat").setValue(alamat);
                        usernya.child("email").setValue(email);
                        usernya.child("password").setValue(password);
                        usernya.child("saldo").setValue("0");
                        usernya.child("foto_profil").setValue(username+'_'+alamatGambar.getLastPathSegment());

                        StorageReference filepath = mStorage.child("Foto Profil").child(username+'_'+alamatGambar.getLastPathSegment());

                        filepath.putFile(alamatGambar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                Toast.makeText(Register.this, "Registrasi Sukses", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else if(alamatGambar == null)
                    {
                        usernya.child("username").setValue(username);
                        usernya.child("nama_lengkap").setValue(nama_lengkap);
                        usernya.child("jenis_kelamin").setValue(jenis_kelamin);
                        usernya.child("no_telfon").setValue(no_telfon);
                        usernya.child("tanggal_lahir").setValue(tanggal_lahir);
                        usernya.child("alamat").setValue(alamat);
                        usernya.child("email").setValue(email);
                        usernya.child("password").setValue(password);
                        usernya.child("saldo").setValue("0");
                        usernya.child("foto_profil").setValue("https://firebasestorage.googleapis.com/v0/b/diagnosisdokter.appspot.com/o/Foto%20Profil%2Fdefault_profile.jpg?alt=media&token=e19056dc-2435-42f3-a4f3-8b748e6d9112");

                        Toast.makeText(Register.this, "Registrasi Sukses", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Register.this, "Gagal dalam upload foto", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
