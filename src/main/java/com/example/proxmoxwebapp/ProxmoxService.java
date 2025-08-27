package com.example.proxmoxwebapp;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;



@Service
public class ProxmoxService {

    @Value("${proxmox.api.token}")
    private String Token;

    private final String apiUrl = "https://proxmox.ecoraline.dev/api2/json";
    private final String apiToken = "PVEAPIToken=root@pam!mitoken=aeac7680-d3dc-40cc-88c7-e2bc956df1e6";
    private final String node = "proxmox-lab";
    private final String templateId = "100"; // ID del template base

    // ---------------- Clonar VM ----------------
    public void clonarVM(int newid, String nombre) throws Exception {
        String body = "newid=" + newid +
                "&name=" + nombre +
                "&target=" + node +
                "&full=1&storage=local-lvm";

        HttpResponse<String> response = Unirest.post(apiUrl + "/nodes/" + node + "/qemu/" + templateId + "/clone")
                .header("Authorization", apiToken)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "ProxmoxWebApp")
                .body(body)
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Error clonando VM: " + response.getBody());
        }
    }

    // ---------------- Iniciar VM ----------------
    public void startVM(int vmid) throws Exception {
        HttpResponse<String> response = Unirest.post(apiUrl + "/nodes/" + node + "/qemu/" + vmid + "/status/start")
                .header("Authorization", apiToken)
                .header("User-Agent", "ProxmoxWebApp")
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Error iniciando VM: " + response.getBody());
        }
    }

    // ---------------- Actualizar vm_id ----------------
    public void updateVMID(int vmid) throws Exception {
        JSONArray cmdArray = new JSONArray();
        cmdArray.put("bash");
        cmdArray.put("-lc");
        cmdArray.put("echo '" + vmid + "' > /etc/vm_id && systemctl restart tunnel.service");

        JSONObject agentBody = new JSONObject();
        agentBody.put("command", cmdArray);

        HttpResponse<String> response = Unirest.post(apiUrl + "/nodes/" + node + "/qemu/" + vmid + "/agent/exec")
                .header("Authorization", apiToken)
                .header("Content-Type", "application/json")
                .header("User-Agent", "ProxmoxWebApp")
                .body(agentBody.toString())
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Error actualizando vm_id: " + response.getBody());
        }
    }

    // ---------------- Apagar VM ----------------
    public void stopVM(int vmid) throws Exception {
        HttpResponse<String> response = Unirest.post(apiUrl + "/nodes/" + node + "/qemu/" + vmid + "/status/stop")
                .header("Authorization", apiToken)
                .header("User-Agent", "ProxmoxWebApp")
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Error apagando VM: " + response.getBody());
        }
    }

    // ---------------- Eliminar VM ----------------
    public void deleteVM(int vmid) throws Exception {
        HttpResponse<String> response = Unirest.delete(apiUrl + "/nodes/" + node + "/qemu/" + vmid)
                .header("Authorization", apiToken)
                .header("User-Agent", "ProxmoxWebApp")
                .asString();

        if (!response.isSuccess()) {
            throw new Exception("Error eliminando VM: " + response.getBody());
        }
    }

    // ---------------- Listar VMs ----------------
    public List<VMData> listVMs() throws Exception {
        HttpResponse<String> response = Unirest.get(apiUrl + "/nodes/" + node + "/qemu")
                .header("Authorization", apiToken)
                .header("User-Agent", "ProxmoxWebApp")
                .asString();

        List<VMData> lista = new ArrayList<>();
        if (response.isSuccess()) {
            JSONObject json = new JSONObject(response.getBody());
            JSONArray data = json.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject vm = data.getJSONObject(i);
                int vmid = vm.getInt("vmid");
                String nombre = vm.getString("name");
                if (vmid > 100 && vmid <200){
                    if(vmid == 103 || vmid == 120) continue; // Saltar el ID 103 y 120
                    lista.add(new VMData(vmid, nombre));
                }
            }
        }
        System.out.println(lista);
        return lista;
    }
}
