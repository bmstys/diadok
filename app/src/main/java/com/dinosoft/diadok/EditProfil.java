package com.dinosoft.diadok;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfil extends AppCompatActivity
{
    private EditText e_username,e_nama_lengkap,e_no_telfon,e_alamat;

    private ImageButton e_foto_profil;
    public static final int KITKAT_VALUE = 1002;
    private Uri alamatGambar = null;

    private FirebaseAuth mAuth;
    private FirebaseUser usernya;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private String URL;

    private boolean no_error = true;

    private ProgressDialog infoProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference().child("Foto Profil");

        infoProgress = new ProgressDialog(this);

        e_username = (EditText)findViewById(R.id.edit_username);
        e_nama_lengkap = (EditText)findViewById(R.id.edit_nama_lengkap);
        e_no_telfon = (EditText)findViewById(R.id.edit_no_telfon);
        e_alamat = (EditText)findViewById(R.id.edit_alamat);
        e_foto_profil = (ImageButton) findViewById(R.id.edit_foto_profil);

        show_data();
    }

    public void ubah_profil_pengguna(View view)
    {
        // Data inputan divalidasi dulu
        validasi_edit_profil();

        // Cek kondisi hasil validasinya, ada error atau engga, kalo engga yaa lanjut
        if(no_error)
        {
            start_edit_profil();

            Intent intent = new Intent(this, Home.class);
            startActivity(intent);

            finish();
        }
    }

    // Untuk proses ubah foto profil
    public void tombol_ubah_foto(View view)
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
            e_foto_profil.setImageURI(alamatGambar);
        }
    }

    private void show_data()
    {
        usernya = mAuth.getCurrentUser();
        String user_id = usernya.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                DataUser dataUser = dataSnapshot.getValue(DataUser.class);

                e_username.setText(dataUser.getUsername());
                e_nama_lengkap.setText(dataUser.getNama_lengkap());
                e_no_telfon.setText(dataUser.getNo_telfon());
                e_alamat.setText(dataUser.getAlamat());

                final String foto_profilnya = dataUser.getFoto_profil();

                mStorage = mStorage.child(foto_profilnya);

                mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        URL = uri.toString();

                        show_foto(URL);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    // Fungsi untuk menampilkan gambar foto profil
    private void show_foto(String alamatUrl)
    {
        String alamat_foto = alamatUrl;

        // Showing foto
        Glide.with(getApplicationContext()).load(alamat_foto).into(e_foto_profil);
    }

    private void validasi_edit_profil()
    {
        String username = e_username.getText().toString().trim();
        String nama_lengkap = e_nama_lengkap.getText().toString().trim();
        String no_telfon = e_no_telfon.getText().toString().trim();
        String alamat = e_alamat.getText().toString().trim();

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

        if(!alamat.isEmpty() && !username.isEmpty() && !no_telfon.isEmpty() && !nama_lengkap.isEmpty())
        {
            no_error = true;
            return;
        }
    }

    // Untuk start registrasi atau bisa dibilang save database
    private void start_edit_profil()
    {
        // Untuk nampilin progress bar
        infoProgress.setMessage("Loading ...");
        infoProgress.show();

        final String username = e_username.getText().toString().trim();
        final String nama_lengkap = e_nama_lengkap.getText().toString().trim();
        final String no_telfon = e_no_telfon.getText().toString().trim();
        final String alamat = e_alamat.getText().toString().trim();

        // Cek biar ga ada yang kosong semua fieldnya (harus diisi semua field nya)
        if(!TextUtils.isEmpty(alamat) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(nama_lengkap) && !TextUtils.isEmpty(no_telfon))
        {
            // Untuk nampilin progress bar
            infoProgress.setMessage("Loading ...");
            infoProgress.show();

            // Untuk get user id
            String user_id = mAuth.getCurrentUser().getUid();

            // Untuk nyimpen user_id sebagai judul tabel untuk tiap user
            DatabaseReference usernya = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            if(alamatGambar == null)
            {
                // Nyimpen ke database
                usernya.child("username").setValue(username);
                usernya.child("nama_lengkap").setValue(nama_lengkap);
                usernya.child("no_telfon").setValue(no_telfon);
                usernya.child("alamat").setValue(alamat);

                Toast.makeText(this, "Sukses update profil", Toast.LENGTH_SHORT).show();
            }
            else
            {
                usernya.child("foto_profil").setValue(username+'_'+alamatGambar.getLastPathSegment());

                if(alamatGambar != null)
                {
                    // Nyimpen ke database
                    usernya.child("username").setValue(username);
                    usernya.child("nama_lengkap").setValue(nama_lengkap);
                    usernya.child("no_telfon").setValue(no_telfon);
                    usernya.child("alamat").setValue(alamat);

                    StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Foto Profil").child(username+'_'+alamatGambar.getLastPathSegment());

                    filepath.putFile(alamatGambar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Toast.makeText(EditProfil.this, "Sukses update profil", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(this, "Gagal Upload Foto", Toast.LENGTH_SHORT).show();
                }
            }
        }
        infoProgress.dismiss();
    }
}
