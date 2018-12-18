package com.dinosoft.diadok;

public class DataDokter
{
    private String nama_dokter,spesialis_dokter,nomor_dokter,alamat_dokter,status_dokter,foto_dokter;

    public DataDokter()
    {

    }

    public DataDokter(String nama_dokter, String spesialis_dokter, String nomor_dokter, String alamat_dokter, String status_dokter, String foto_dokter)
    {
        this.nama_dokter = nama_dokter;
        this.spesialis_dokter = spesialis_dokter;
        this.nomor_dokter = nomor_dokter;
        this.alamat_dokter = alamat_dokter;
        this.status_dokter = status_dokter;
        this.foto_dokter = foto_dokter;
    }

    public String getNama_dokter() {
        return nama_dokter;
    }

    public void setNama_dokter(String nama_dokter) {
        this.nama_dokter = nama_dokter;
    }

    public String getSpesialis_dokter() {
        return spesialis_dokter;
    }

    public void setSpesialis_dokter(String spesialis_dokter) {
        this.spesialis_dokter = spesialis_dokter;
    }

    public String getNomor_dokter() {
        return nomor_dokter;
    }

    public void setNomor_dokter(String nomor_dokter) {
        this.nomor_dokter = nomor_dokter;
    }

    public String getAlamat_dokter() {
        return alamat_dokter;
    }

    public void setAlamat_dokter(String alamat_dokter) {
        this.alamat_dokter = alamat_dokter;
    }

    public String getStatus_dokter() {
        return status_dokter;
    }

    public void setStatus_dokter(String status_dokter) {
        this.status_dokter = status_dokter;
    }

    public String getFoto_dokter() {
        return foto_dokter;
    }

    public void setFoto_dokter(String foto_dokter) {
        this.foto_dokter = foto_dokter;
    }
}
