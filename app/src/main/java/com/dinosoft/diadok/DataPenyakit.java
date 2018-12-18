package com.dinosoft.diadok;

public class DataPenyakit
{
    private String nama_penyakit,deskripsi;

    public DataPenyakit()
    {

    }

    public DataPenyakit(String nama_penyakit, String deskripsi)
    {
        this.nama_penyakit = nama_penyakit;
        this.deskripsi = deskripsi;
    }

    public String getNama_penyakit()
    {
        return nama_penyakit;
    }

    public void setNama_penyakit(String nama_penyakit)
    {
        this.nama_penyakit = nama_penyakit;
    }

    public String getDeskripsi()
    {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi)
    {
        this.deskripsi = deskripsi;
    }
}
