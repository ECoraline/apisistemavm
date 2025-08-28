package com.example.proxmoxwebapp.model;

public class VMData {
    private int vmid;
    private String nombre;
    private String status;

    public VMData(int vmid, String nombre, String status) {
        this.vmid = vmid;
        this.nombre = nombre;
        this.status = status;
    }

    public int getVmid() {
        return vmid;
    }

    public void setVmid(int vmid) {
        this.vmid = vmid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
