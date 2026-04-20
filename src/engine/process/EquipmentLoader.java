package engine.process;

import data.model.Equipment;
import data.model.EquipmentType;
import engine.mobile.Player;
import log.LoggerUtility;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EquipmentLoader {
    private static final Logger logger = LoggerUtility.getLogger(EquipmentLoader.class);
    private static EquipmentLoader instance;
    private List<Equipment> basicList = new ArrayList<Equipment>();
    private List<Equipment> fusedList = new ArrayList<Equipment>();

    private EquipmentLoader() {
        load();
    }

    public static EquipmentLoader getInstance() {
        if (instance == null) {
            instance = new EquipmentLoader();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private void load() {
        try {
            InputStream is = getClass().getResourceAsStream(
                    "/game_config/equipment/equipement.json");
            if (is == null) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            br.close();

            String json = sb.toString();

            // Extraire le bloc "basic"
            String basicBlock = extractBlock(json, "basic");
            parseList(basicBlock, false);

            // Extraire le bloc "fused"
            String fusedBlock = extractBlock(json, "fused");
            parseList(fusedBlock, true);

        } catch (Exception e) {
            
        }
    }

    // Extrait le contenu du tableau JSON correspondant a la cle
    private String extractBlock(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) return "[]";
        int start = json.indexOf("[", keyIndex);
        if (start == -1) return "[]";
        int depth = 0;
        for (int i = start; i < json.length(); i++) {
            if (json.charAt(i) == '[') depth++;
            else if (json.charAt(i) == ']') {
                depth--;
                if (depth == 0) return json.substring(start, i + 1);
            }
        }
        return "[]";
    }

    // Decoupe le tableau en objets et les parse
    private void parseList(String arrayJson, boolean fused) {
        // Enlever les crochets
        arrayJson = arrayJson.trim();
        if (arrayJson.startsWith("[")) arrayJson = arrayJson.substring(1);
        if (arrayJson.endsWith("]"))   arrayJson = arrayJson.substring(0, arrayJson.length() - 1);

        // Decouper en objets {}
        int depth = 0;
        int start = -1;
        for (int i = 0; i < arrayJson.length(); i++) {
            char c = arrayJson.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    String obj = arrayJson.substring(start, i + 1);
                    Equipment eq = parseObject(obj, fused);
                    if (eq != null) {
                        if (fused) fusedList.add(eq);
                        else       basicList.add(eq);
                    }
                    start = -1;
                }
            }
        }
    }

    // Parse un objet JSON simple en Equipment
    private Equipment parseObject(String obj, boolean fused) {
        try {
            int    id           = parseInt(getVal(obj, "id"));
            String name         = getVal(obj, "name");
            EquipmentType type  = EquipmentType.valueOf(getVal(obj, "type"));
            int    attackBonus  = parseInt(getVal(obj, "attackBonus"));
            int    defenseBonus = parseInt(getVal(obj, "defenseBonus"));
            String description  = getVal(obj, "description");

            if (fused) {
                int req1 = parseInt(getVal(obj, "req1"));
                int req2 = parseInt(getVal(obj, "req2"));
                return new Equipment(id, name, type, attackBonus, defenseBonus,
                                     description, req1, req2);
            } else {
                int price = parseInt(getVal(obj, "price"));
                return new Equipment(id, name, type, attackBonus, defenseBonus,
                                     price, description);
            }
        } catch (Exception e) {
           logger.error("Erreur de parsing sur l'objet : " + e.getMessage());
           logger.debug("Contenu de l'objet en cause : " + obj);
           return null;
        }
    }

    // Extrait la valeur d une cle dans un objet JSON
    private String getVal(String obj, String key) {
        String search = "\"" + key + "\"";
        int ki = obj.indexOf(search);
        if (ki == -1) return "";
        int colon = obj.indexOf(":", ki);
        if (colon == -1) return "";
        int vs = colon + 1;
        while (vs < obj.length() && obj.charAt(vs) == ' ') vs++;
        if (vs >= obj.length()) return "";
        if (obj.charAt(vs) == '"') {
            int ve = obj.indexOf('"', vs + 1);
            if (ve == -1) return "";
            return obj.substring(vs + 1, ve);
        } else {
            int ve = vs;
            while (ve < obj.length() && obj.charAt(ve) != ',' && obj.charAt(ve) != '}') ve++;
            return obj.substring(vs, ve).trim();
        }
    }

    private int parseInt(String s) {
        if (s == null || s.isEmpty()) return 0;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return 0; }
    }

    public List<Equipment> getBasicList() { return basicList; }
    public List<Equipment> getFusedList()  { return fusedList; }
}