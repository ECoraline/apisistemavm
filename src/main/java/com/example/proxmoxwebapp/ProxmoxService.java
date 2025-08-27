package com.example.proxmoxwebapp;

import kong.unirest.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProxmoxService {

    @Value("${proxmox.api.token}")
    private String token;

    private final String apiUrl = "https://proxmox.ecoraline.dev/api2/json";
    private final String apiToken = token;
    private final String node = "proxmox-lab";
    private final String templateId = "100"; // ID de tu template

    public String getApiUrl() { return apiUrl; }
    public String getApiToken() { return apiToken; }
    public String getNode() { return node; }

    // Clonar + iniciar VM
    public String crearVM(int newid, String nombre) throws Exception {

        // 1️⃣ Clonar template
        String cloneBody = "newid=" + newid + "&name=" + nombre + "&target=" + node + "&full=1&storage=local-lvm";
        String cloneResp = Unirest.post(apiUrl + "/nodes/" + node + "/qemu/" + templateId + "/clone")
                .header("Authorization", apiToken)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "SpringBoot")
                .body(cloneBody)
                .asString()
                .getBody();

        // Extraer vmid desde la respuesta tipo UPID:...
        String vmid = cloneResp.split(":")[5]; // Proxmox devuelve UPID:node:taskid:...:qmclone:100:...
        if (vmid == null || vmid.isEmpty()) vmid = String.valueOf(newid);
        System.out.println("Clonada VM con ID: " + vmid);

        Thread.sleep(9000); // espera 5 segundos, puedes ajustar el tiempo

        // 2️⃣ Iniciar VM
        Unirest.post(apiUrl + "/nodes/" + node + "/qemu/" + newid + "/status/start")
                .header("Authorization", apiToken)
                .asString();

        return vmid;
    }
    public JSONArray listarVMsConPuertos() throws Exception {
        String resp = Unirest.get(apiUrl + "/nodes/" + node + "/qemu")
                .header("Authorization", apiToken)
                .asString()
                .getBody();

        JSONObject json = new JSONObject(resp);
        JSONArray vms = json.getJSONArray("data");

        int BASE_SSH = 2222;
        int BASE_HTTP = 3000;

        JSONArray result = new JSONArray();

        for (int i = 0; i < vms.length(); i++) {
            JSONObject vm = vms.getJSONObject(i);
            JSONObject vmInfo = new JSONObject();
            vmInfo.put("vmid", vm.getInt("vmid"));
            vmInfo.put("name", vm.getString("name"));
            vmInfo.put("status", vm.getString("status"));
            vmInfo.put("sshPort", BASE_SSH + vm.getInt("vmid"));
            vmInfo.put("httpPort", BASE_HTTP + vm.getInt("vmid"));
            result.put(vmInfo);
        }

        return result;
    }
}
