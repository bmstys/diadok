package com.dinosoft.diadok;

public class DataGejala
{
    private String nama,kode;

    public DataGejala()
    {

    }

    public DataGejala(String nama, String kode)
    {
        this.nama = nama;
        this.kode = kode;
    }

    public String getNama()
    {
        return nama;
    }

    public void setNama(String nama)
    {
        this.nama = nama;
    }

    public String getKode()
    {
        return kode;
    }

    public void setKode(String kode)
    {
        this.kode = kode;
    }
}
