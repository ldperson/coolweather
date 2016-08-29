package com.exerice.administrator.coolweather.model;

/**
 * Created by Administrator on 2016-8-29.
 */
public class Province {
    private int id;
    private String provinceNme;
    private String provinceCode;

    public int getId() {
        return id;
    }

    public String getProvinceNme() {
        return provinceNme;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setProvinceNme(String provinceNme) {
        this.provinceNme = provinceNme;
    }
}
