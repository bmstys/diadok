package com.dinosoft.diadok;

public class DataObat
{
    private String harga,nama_obat,penyakit_id,kandungan;

    public DataObat()
    {

    }

    public DataObat(String harga, String nama_obat, String penyakit_id, String kandungan) {
        this.harga = harga;
        this.nama_obat = nama_obat;
        this.penyakit_id = penyakit_id;
        this.kandungan = kandungan;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getNama_obat() {
        return nama_obat;
    }

    public void setNama_obat(String nama_obat) {
        this.nama_obat = nama_obat;
    }

    public String getPenyakit_id() {
        return penyakit_id;
    }

    public void setPenyakit_id(String penyakit_id) {
        this.penyakit_id = penyakit_id;
    }

    public String getKandungan() {
        return kandungan;
    }

    public void setKandungan(String kandungan) {
        this.kandungan = kandungan;
    }
}
