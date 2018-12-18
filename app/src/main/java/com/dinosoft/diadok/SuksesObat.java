package com.dinosoft.diadok;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SuksesObat extends AppCompatActivity
{
    private String apotekTerdekat;
    private String latUser;
    private String longUser;

    Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sukses_obat);

        geocoder = new Geocoder(this, Locale.getDefault());

        Bundle extras = getIntent().getExtras();
        apotekTerdekat = extras.getString("apotekTerdekat");
        latUser = extras.getString("latUser");
        longUser = extras.getString("longUser");

        double latitude = Double.parseDouble(latUser);
        double longitude = Double.parseDouble(longUser);

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView apotekView = findViewById(R.id.apotekTerdekatTv);
        apotekView.setText(apotekTerdekat);

        String lokasi = addresses.get(0).getAddressLine(0);

        TextView lokasiUser = findViewById(R.id.lokasiUserTv);
        lokasiUser.setText(lokasi);

        Toast.makeText(this, "Obat sukses dibeli", Toast.LENGTH_SHORT).show();
    }

    public void mengerti_obat(View view)
    {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
