package com.example.proxmoxwebapp;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import kong.unirest.Unirest;

@RestController
@RequestMapping("/vm")
@CrossOrigin(origins = "*")
public class VMController {

    @Autowired
    private ProxmoxService service;

    // Crear VM (clonar + iniciar)
    @PostMapping("/crear")
    public String crearVM(@RequestParam int newid, @RequestParam String nombre) {
        try {
            String vmid = service.crearVM(newid, nombre);
            return "{\"ok\":true,\"vmid\":" + vmid + "}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    // Actualizar vm_id y reiniciar túnel
    @PostMapping("/actualizar-vmid")
    public String actualizarVMID(@RequestParam int vmid) {
        try {
            // Construir comando
            JSONObject agentBody = new JSONObject();
            agentBody.put("command", new String[]{
                    "bash",
                    "-lc",
                    "echo '" + vmid + "' > /etc/vm_id && systemctl restart tunnel.service"
            });

            Unirest.post(service.getApiUrl() + "/nodes/" + service.getNode() + "/qemu/" + vmid + "/agent/exec")
                    .header("Authorization", service.getApiToken())
                    .header("Content-Type","application/json")
                    .body(agentBody.toString())
                    .asString();

            int sshPort = 2222 + vmid;
            return "{\"ok\":true,\"ssh\":\"ssh -p " + sshPort + " ubuntu@52.155.56.140\"}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    @PostMapping("/apagar")
    public String apagarVM(@RequestParam String vmid) {
        try {
            Unirest.post(service.getApiUrl() + "/nodes/" + service.getNode() + "/qemu/" + vmid + "/status/shutdown")
                    .header("Authorization", service.getApiToken())
                    .asString();
            return "{\"ok\":true}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\""+e.getMessage()+"\"}";
        }
    }

    @PostMapping("/eliminar")
    public String eliminarVM(@RequestParam String vmid) {
        try {
            Unirest.delete(service.getApiUrl() + "/nodes/" + service.getNode() + "/qemu/" + vmid)
                    .header("Authorization", service.getApiToken())
                    .asString();
            return "{\"ok\":true}";
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\""+e.getMessage()+"\"}";
        }
    }
    @GetMapping("/listarPuertos")
    public String listarVMsPuertos() {
        try {
            JSONArray vms = service.listarVMsConPuertos();
            return vms.toString();
        } catch (Exception e) {
            return "{\"ok\":false,\"error\":\""+e.getMessage()+"\"}";
        }
    }

}
