package com.example.proxmoxwebapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vm")
@CrossOrigin(origins = "*")
public class VMController {

    @Autowired
    private ProxmoxService service;

    @PostMapping("/crearClone")
    public String crearClone(@RequestParam int newid, @RequestParam String nombre) {
        try {
            service.clonarVM(newid, nombre);
            return "{\"ok\":true}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\""+e.getMessage()+"\"}";
        }
    }

    @PostMapping("/startVM")
    public String startVM(@RequestParam int vmid) {
        try {
            service.startVM(vmid);
            return "{\"ok\":true}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\""+e.getMessage()+"\"}";
        }
    }

    @PostMapping("/updateVMID")
    public String updateVMID(@RequestParam int vmid) {
        try {
            service.updateVMID(vmid);
            return "{\"ok\":true}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\""+e.getMessage()+"\"}";
        }
    }

    @PostMapping("/stopVM")
    public String stopVM(@RequestParam int vmid) {
        try {
            service.stopVM(vmid);
            return "{\"ok\":true}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\""+e.getMessage()+"\"}";
        }
    }

    @PostMapping("/deleteVM")
    public String deleteVM(@RequestParam int vmid) {
        try {
            service.deleteVM(vmid);
            return "{\"ok\":true}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\""+e.getMessage()+"\"}";
        }
    }

    @GetMapping("/list")
    public List<VMData> listVMs() throws Exception {
        return service.listVMs();
    }
}
