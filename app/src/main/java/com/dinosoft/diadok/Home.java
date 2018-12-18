package com.dinosoft.diadok;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Deklarasi untuk display text
    private TextView nama_user,email_user;

    // Deklarasi Image
    private CircleImageView foto_user;

    // Deklarasi Untuk Drawable
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;

    // Untuk registrasi
    private FirebaseAuth mAuth;
    private FirebaseUser usernya;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Untuk database
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private String URL;

    private String userIdnya;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Untuk cek ada login atau tidak
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                // User Current Auth
                usernya = mAuth.getCurrentUser();

                // Kalo ga login maka
                if(usernya == null)
                {
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    startActivity(intent);
                }
                else if(usernya != null)
                {
                    userIdnya = usernya.getUid();

                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            show_data(userIdnya);
                        }
                    }, 2000);

                }
            }
        };

        // Untuk Drawable
        mDrawerLayout =(DrawerLayout) findViewById(R.id.drawer);
        mToogle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Untuk NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Untuk display user
        nama_user = (TextView)navigationView.getHeaderView(0).findViewById(R.id.d_nama_user);
        email_user = (TextView)navigationView.getHeaderView(0).findViewById(R.id.d_email_user);
        foto_user = (CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.d_foto_profil);
    }

    private void show_data(String UID)
    {
        // Cari database nya user yang authentication
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

        mDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                // Ambil usernamenya
                String usernamenya = dataSnapshot.child("username").getValue().toString();

                // Ambil emailnya
                String emailnya = dataSnapshot.child("email").getValue().toString();

                // Set Value untuk display
                email_user.setText(emailnya);
                nama_user.setText(usernamenya);

                // Ambil foto_profilnya
                final String foto_profilnya = dataSnapshot.child("foto_profil").getValue().toString();

                // Cari fotonya
                mStorage = FirebaseStorage.getInstance().getReference().child("Foto Profil").child(foto_profilnya);

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
        Glide.with(getApplicationContext()).load(alamat_foto).into(foto_user);
    }

    // Untuk item navigasi drawable
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(mToogle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Untuk NavigationView
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if( id == R.id.edit_profil )
        {
            Intent intent = new Intent(this, EditProfil.class);
            startActivity(intent);
        }
        if( id == R.id.logout )
        {
            Toast.makeText(this, "Anda telah logout dari akun anda", Toast.LENGTH_SHORT).show();

            finish();

            mAuth.signOut();
        }
        return false;
    }

    public void diagnosis(View view)
    {
        Intent intent = new Intent(this, Diagnosis.class);
        startActivity(intent);
    }

    public void tentang(View view)
    {
        Intent intent = new Intent(this, Tentang.class);
        startActivity(intent);
    }

    public void riwayat(View view)
    {
        Intent intent = new Intent(this, Riwayat.class);
        startActivity(intent);
    }

    public void virtual_wallet(View view)
    {
        Intent intent = new Intent(this, VirtualWallet.class);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Untuk cek listen authentication
        mAuth.addAuthStateListener(mAuthListener);
    }
}
