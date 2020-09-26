package com.ismynr.ngekas.entity;

public class Kas {
    private String kas_id;
    private String kas_type;
    private String kas_total;
    private String kas_info;
    private String kas_date;

    public Kas(String kas_id, String kas_type, String kas_total, String kas_info, String kas_date) {
        this.kas_id = kas_id;
        this.kas_type = kas_type;
        this.kas_total = kas_total;
        this.kas_info = kas_info;
        this.kas_date = kas_date;
    }

    public Kas(String kas_type, String kas_total, String kas_info, String kas_date) {
        this.kas_type = kas_type;
        this.kas_total = kas_total;
        this.kas_info = kas_info;
        this.kas_date = kas_date;
    }

    public String getKas_id() {
        return kas_id;
    }

    public void setKas_id(String kas_id) {
        this.kas_id = kas_id;
    }

    public String getKas_type() {
        return kas_type;
    }

    public void setKas_type(String kas_type) {
        this.kas_type = kas_type;
    }

    public String getKas_total() {
        return kas_total;
    }

    public void setKas_total(String kas_total) {
        this.kas_total = kas_total;
    }

    public String getKas_info() {
        return kas_info;
    }

    public void setKas_info(String kas_info) {
        this.kas_info = kas_info;
    }

    public String getKas_date() {
        return kas_date;
    }

    public void setKas_date(String kas_date) {
        this.kas_date = kas_date;
    }
}
