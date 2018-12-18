package com.dinosoft.diadok;

public class DataUser
{
    private String username,nama_lengkap,jenis_kelamin,no_telfon,tanggal_lahir,alamat,email,password,saldo,foto_profil;

    public DataUser()
    {

    }

    public DataUser(String username, String nama_lengkap, String jenis_kelamin, String no_telfon, String tanggal_lahir, String alamat, String email, String password, String saldo, String foto_profil)
    {
        this.username = username;
        this.nama_lengkap = nama_lengkap;
        this.jenis_kelamin = jenis_kelamin;
        this.no_telfon = no_telfon;
        this.tanggal_lahir = tanggal_lahir;
        this.alamat = alamat;
        this.email = email;
        this.password = password;
        this.saldo = saldo;
        this.foto_profil = foto_profil;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getNama_lengkap()
    {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap)
    {
        this.nama_lengkap = nama_lengkap;
    }

    public String getJenis_kelamin()
    {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin)
    {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getNo_telfon()
    {
        return no_telfon;
    }

    public void setNo_telfon(String no_telfon)
    {
        this.no_telfon = no_telfon;
    }

    public String getTanggal_lahir()
    {
        return tanggal_lahir;
    }

    public void setTanggal_lahir(String tanggal_lahir)
    {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getAlamat()
    {
        return alamat;
    }

    public void setAlamat(String alamat)
    {
        this.alamat = alamat;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSaldo()
    {
        return saldo;
    }

    public void setSaldo(String saldo)
    {
        this.saldo = saldo;
    }

    public String getFoto_profil()
    {
        return foto_profil;
    }

    public void setFoto_profil(String foto_profil)
    {
        this.foto_profil = foto_profil;
    }
}
