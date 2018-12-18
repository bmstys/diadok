package com.dinosoft.diadok;

public class DataBooking
{
    private String hari,jadwal_id,lokasi,nama_dokter,no_urut,pasien_id,user_id,waktu,tanggal;

    public DataBooking()
    {

    }

    public DataBooking(String hari, String jadwal_id, String lokasi, String nama_dokter, String no_urut, String pasien_id, String user_id, String waktu, String tanggal)
    {
        this.hari = hari;
        this.jadwal_id = jadwal_id;
        this.lokasi = lokasi;
        this.nama_dokter = nama_dokter;
        this.no_urut = no_urut;
        this.pasien_id = pasien_id;
        this.user_id = user_id;
        this.waktu = waktu;
        this.tanggal = tanggal;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getJadwal_id() {
        return jadwal_id;
    }

    public void setJadwal_id(String jadwal_id) {
        this.jadwal_id = jadwal_id;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getNama_dokter() {
        return nama_dokter;
    }

    public void setNama_dokter(String nama_dokter) {
        this.nama_dokter = nama_dokter;
    }

    public String getNo_urut() {
        return no_urut;
    }

    public void setNo_urut(String no_urut) {
        this.no_urut = no_urut;
    }

    public String getPasien_id() {
        return pasien_id;
    }

    public void setPasien_id(String pasien_id) {
        this.pasien_id = pasien_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
