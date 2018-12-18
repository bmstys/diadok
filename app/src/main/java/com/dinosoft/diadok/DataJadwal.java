package com.dinosoft.diadok;

public class DataJadwal
{
    private String dokter_id,hari,lokasi,waktu_mulai,waktu_akhir;

    public DataJadwal()
    {

    }

    public DataJadwal(String dokter_id, String hari, String lokasi, String waktu_mulai, String waktu_akhir) {
        this.dokter_id = dokter_id;
        this.hari = hari;
        this.lokasi = lokasi;
        this.waktu_mulai = waktu_mulai;
        this.waktu_akhir = waktu_akhir;
    }

    public String getDokter_id() {
        return dokter_id;
    }

    public void setDokter_id(String dokter_id) {
        this.dokter_id = dokter_id;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getWaktu_mulai() {
        return waktu_mulai;
    }

    public void setWaktu_mulai(String waktu_mulai) {
        this.waktu_mulai = waktu_mulai;
    }

    public String getWaktu_akhir() {
        return waktu_akhir;
    }

    public void setWaktu_akhir(String waktu_akhir) {
        this.waktu_akhir = waktu_akhir;
    }
}
