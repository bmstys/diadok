package com.dinosoft.diadok;

public class DataTransaksi
{
    private String title,user_id,nominal,tanggal,foto,waktu;

    public DataTransaksi()
    {

    }

    public DataTransaksi(String title, String user_id, String nominal, String tanggal, String foto, String waktu)
    {
        this.title = title;
        this.user_id = user_id;
        this.nominal = nominal;
        this.tanggal = tanggal;
        this.foto = foto;
        this.waktu = waktu;
    }

    public String getWaktu()
    {
        return waktu;
    }

    public void setWaktu(String waktu)
    {
        this.waktu = waktu;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getNominal()
    {
        return nominal;
    }

    public void setNominal(String top_up)
    {
        this.nominal = top_up;
    }

    public String getTanggal()
    {
        return tanggal;
    }

    public void setTanggal(String tanggal)
    {
        this.tanggal = tanggal;
    }

    public String getFoto()
    {
        return foto;
    }

    public void setFoto(String foto)
    {
        this.foto = foto;
    }
}
