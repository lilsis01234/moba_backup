package engine.process;

import data.model.*;
import engine.mobile.CCEffect;
import engine.mobile.DamageEffect;
import engine.mobile.SpellStrategy;
import engine.mobile.SupportEffect;

import java.io.*;
import java.util.*;

public class JsonDataProvider {
    
    private static final String HEROES_FILE = "/game_config/heroes/heroes.json";
    
    private List<Hero> heroes;
    private List<Category> categories;
    private Map<Integer, List<Spell>> heroSpells;
    
    public JsonDataProvider() throws IOException {
        loadData();
    }
    
    private void loadData() throws IOException {
        // Initialize categories
        categories = new ArrayList<>();
        categories.add(new Category("Force"));
        categories.add(new Category("Agilite"));
        categories.add(new Category("Intelligence"));
        
        // Load heroes and spells from JSON
        heroes = new ArrayList<>();
        heroSpells = new HashMap<>();
        
        String json = readJSONFile();
        parseHeroes(json);
    }
    
    private String readJSONFile() throws IOException {
        StringBuilder json = new StringBuilder();
        InputStream is = getClass().getResourceAsStream(HEROES_FILE);
        if (is == null) throw new IOException("Fichier introuvable : /game_config/heroes/heroes.json");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }
        }
        return json.toString();
    }
    
    private static boolean isWhitespaceOrComma(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == ',';
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\t';
    }

    private static boolean isDelimiter(char c) {
        return c == ',' || isWhitespace(c);
    }

    private void parseHeroes(String json) {
       
        List<Map<String, String>> heroObjects = parseJsonArray(json);
        
        for ( Map<String, String> heroMap : heroObjects) {
            Hero hero = new Hero();         
            //heroes
            hero.setName(heroMap.get("name"));
            hero.setSpriteFile((heroMap.get("spriteFile")));
            hero.setHistory(heroMap.get("history"));
            hero.setCategoryId(getCategoryId(heroMap.get("category")));
            hero.setMaxHp(parseInt(heroMap.get("maxHp")));
            hero.setAttack(parseInt(heroMap.get("attack")));
            hero.setDefense(parseInt(heroMap.get("defense")));
            hero.setAttackSpeed(parseDouble(heroMap.get("attackSpeed")));
            hero.setMaxMana(parseInt(heroMap.get("maxMana")));
            hero.setAtkRange(parseDouble(heroMap.get("attackRange")));
            hero.setSpeed(parseDouble(heroMap.get("speed")));
                        
            heroes.add(hero);
            
            // spells
            List<Spell> spells = new ArrayList<>();
            List<Map<String, String>> spellObjects = parseJsonArray(heroMap.get("spells"));
            for (Map<String, String> spellMap : spellObjects) {
                Spell spell = new Spell();
                spell.setHeroId(hero.getId());
                spell.setName(spellMap.get("name"));
                spell.setDescription(spellMap.get("description"));
                Spell.Type spellType = switch (spellMap.get("type")) {
                case "dmg" -> Spell.Type.DAMAGE;
                case "CC"  -> Spell.Type.CROWD_CONTROL;
                case "SP"  -> Spell.Type.SUPPORT;
                default    -> Spell.Type.DAMAGE;
                };
                spell.setType(spellType);
                spell.setDamage(parseInt(spellMap.get("damage")));
                spell.setCooldown(parseDouble(spellMap.get("cooldown")));
                spell.setManaCost(parseInt(spellMap.get("manaCost")));
                //THIS IS A PLACEHOLDER, the effect value shall be calculated later
                spell.setEffect(createEffect(spell.getType(), spell.getDamage()));
                spells.add(spell);
            }
            heroSpells.put(hero.getId(), spells);
            for (Spell s : spells) hero.addSpell(s);
        }
    }
    
    private List<Map<String, String>> parseJsonArray(String json) {
        List<Map<String, String>> result = new ArrayList<>();
   
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        
       
        int braceCount = 0;
        int start = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                  
                    if (i + 1 < json.length() && json.charAt(i + 1) == ',') {
                        String objStr = json.substring(start, i + 1);
                        result.add(parseJsonObject(objStr));
                        start = i + 2; 
                    } else if (i + 1 >= json.length()) {
                        String objStr = json.substring(start, i + 1);
                        result.add(parseJsonObject(objStr));
                    }
                }
            }
        }
        return result;
    }
    
    private Map<String, String> parseJsonObject(String json) {
        Map<String, String> result = new LinkedHashMap<>();
       
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        
        int pos = 0;
        while (pos < json.length()) {
            // Skip whitespace and commas
            while (pos < json.length() && isWhitespaceOrComma(json.charAt(pos))) {
                pos++;
            }
            if (pos >= json.length()) break;
           
            if (json.charAt(pos) != '"') break;
            int keyStart = pos + 1;
            int keyEnd = json.indexOf('"', keyStart);
            if (keyEnd == -1) break;
            String key = json.substring(keyStart, keyEnd);
            pos = keyEnd + 1;
            
            // Skip to colon
            while (pos < json.length() && json.charAt(pos) != ':') pos++;
            if (pos >= json.length()) break;
            pos++; // Skip colon
            
            // Skip whitespace
            while (pos < json.length() && isWhitespace(json.charAt(pos))) {
                pos++;
            }
            if (pos >= json.length()) break;
            
            // Parse value
            String value;
            if (json.charAt(pos) == '"') {
                int valStart = pos + 1;
                int valEnd = json.indexOf('"', valStart);
                if (valEnd == -1) break;
                value = json.substring(valStart, valEnd);
                pos = valEnd + 1;
            } else if (json.charAt(pos) == '[') {
                int arrayStart = pos;
                int braceCount = 0;
                while (pos < json.length()) {
                    if (json.charAt(pos) == '[') braceCount++;
                    else if (json.charAt(pos) == ']') braceCount--;
                    pos++;
                    if (braceCount == 0) break;
                }
                value = json.substring(arrayStart, pos);
            } else {
                int valStart = pos;
                while (pos < json.length() && !isDelimiter(json.charAt(pos))) {
                    pos++;
                }
                value = json.substring(valStart, pos);
            }
            
            result.put(key, value);
        }
        return result;
    }
        	
	private SpellStrategy createEffect(Spell.Type type, int amount) {
	    switch (type) {
	        case DAMAGE:        return new DamageEffect(amount);
	        case CROWD_CONTROL: return new CCEffect(amount);
	        case SUPPORT:       return new SupportEffect(amount);
	        default:            return new DamageEffect(amount);
	    }
    }

    private int parseInt(String s) {
        if (s == null || s.isEmpty()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private double parseDouble(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private int getCategoryId(String categoryName) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equals(categoryName)) {
                return i + 1;
            }
        }
        return 1; // Default to Force
    }
    
    public List<Hero> getAllHeroes() {
        return Collections.unmodifiableList(heroes);
    }
    
    public Hero getHeroById(int id) {
        for (Hero h : heroes) {
            if (h.getId() == id) return h;
        }
        return null;
    }
    
    public List<Spell> getSpellsForHero(int heroId) {
        return heroSpells.getOrDefault(heroId, Collections.emptyList());
    }
    
    public List<Category> getAllCategories() {
        return Collections.unmodifiableList(categories);
    }
    
    public Category getCategoryById(int id) {
        if (id >= 1 && id <= categories.size()) {
            return categories.get(id - 1);
        }
        return categories.get(0);
    }
    
    public Category getCategoryByName(String name) {
        for (Category c : categories) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
}