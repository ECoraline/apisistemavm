package com.example.proxmoxwebapp;

public class VMData {
    private int vmid;
    private String nombre;

    public VMData(int vmid, String nombre) {
        this.vmid = vmid;
        this.nombre = nombre;
    }

    public int getVmid() {
        return vmid;
    }

    public String getNombre() {
        return nombre;
    }
}
