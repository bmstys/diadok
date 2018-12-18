package com.dinosoft.diadok;

public class DataDiagnosa
{
    private String kategori_penyakit,penyakit,waktu_diagnosa,tanggal_diagnosa,foto_diagnosa,user_id,diagnosa_ke;

    public DataDiagnosa()
    {

    }

    public DataDiagnosa(String kategori_penyakit, String penyakit, String waktu_diagnosa, String tanggal_diagnosa, String foto_diagnosa, String user_id, String diagnosa_ke)
    {
        this.kategori_penyakit = kategori_penyakit;
        this.penyakit = penyakit;
        this.waktu_diagnosa = waktu_diagnosa;
        this.tanggal_diagnosa = tanggal_diagnosa;
        this.foto_diagnosa = foto_diagnosa;
        this.user_id = user_id;
        this.diagnosa_ke = diagnosa_ke;
    }

    public String getKategori_penyakit()
    {
        return kategori_penyakit;
    }

    public void setKategori_penyakit(String kategori_penyakit)
    {
        this.kategori_penyakit = kategori_penyakit;
    }

    public String getPenyakit()
    {
        return penyakit;
    }

    public void setPenyakit(String penyakit)
    {
        this.penyakit = penyakit;
    }

    public String getWaktu_diagnosa()
    {
        return waktu_diagnosa;
    }

    public void setWaktu_diagnosa(String waktu_diagnosa)
    {
        this.waktu_diagnosa = waktu_diagnosa;
    }

    public String getTanggal_diagnosa()
    {
        return tanggal_diagnosa;
    }

    public void setTanggal_diagnosa(String tanggal_diagnosa)
    {
        this.tanggal_diagnosa = tanggal_diagnosa;
    }

    public String getFoto_diagnosa()
    {
        return foto_diagnosa;
    }

    public void setFoto_diagnosa(String foto_diagnosa)
    {
        this.foto_diagnosa = foto_diagnosa;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getDiagnosa_ke() {
        return diagnosa_ke;
    }

    public void setDiagnosa_ke(String diagnosa_ke) {
        this.diagnosa_ke = diagnosa_ke;
    }
}
