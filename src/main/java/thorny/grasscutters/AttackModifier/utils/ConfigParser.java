package thorny.grasscutters.AttackModifier.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.utils.JsonUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

public final class ConfigParser {

    private Config config;
    private Config gadgetConfig;
    private int gadgetConfigUid;
    private ArrayList<Integer> blacklistUID = new ArrayList<>();
    private final String configPath = Grasscutter.getConfig().folderStructure.plugins + "AttackModifier";
    private final File configFile = new File(this.configPath + "/config.json");
    private final File blacklistFile = new File(this.configPath + "/blacklist.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();

    public ConfigParser() {
        this.loadConfig();
        this.loadGadgetConfig(0);
        this.loadBlacklist();
        // load config ofc
    }

    public Config getConfig() {
        return this.config;
    }

    public Config getGadgetConfig(){
        return this.gadgetConfig;
    }

    public ArrayList<Integer> getBlacklist(){
        return this.blacklistUID;
    }

    public int getGadgetConfigUid(){
        return this.gadgetConfigUid;
    }

    public void loadConfig() {
        try (FileReader file = new FileReader(this.configFile)) {
            this.config = gson.fromJson(file, Config.class);
            Grasscutter.getLogger().info("[AttackModifier] Config Loaded!");
        } catch (Exception e) {
            this.config = new Config();
            this.config.setDefaults();
            Grasscutter.getLogger().info("[AttackModifier] Basic config creating...");
        }

        if (!saveConfig()) {
            Grasscutter.getLogger().error("[AttackModifier] Unable to save config file.");
        }

        Grasscutter.getLogger().info("[AttackModifier] Plugin loaded successfully!");
    }

    public void loadBlacklist(){
        try (FileReader file = new FileReader(this.blacklistFile)) {
            this.blacklistUID = gson.fromJson(file, listType);
        } catch (Exception e) {saveBlacklist(null);}
    }

    public void loadGadgetConfig(int uid){
        File gadgetFile = new File(this.configPath, "/"+uid+".json");
        try (FileReader file = new FileReader(gadgetFile)) {
            this.gadgetConfig = gson.fromJson(file, Config.class);
            this.gadgetConfigUid = uid;
        } catch (Exception e) {
            this.gadgetConfig = new Config();
            this.gadgetConfig.setDefaults();
            saveGadgetList(gadgetConfig, uid);
        }
    }

    public boolean saveConfig() {
        File dir = new File(this.configPath);

        if (!dir.exists() || !dir.isDirectory()) {
            if (!new java.io.File(String.valueOf(dir)).mkdirs())
                return false;
        }

        try (FileWriter file = new FileWriter(this.configFile)) {
            file.write(gson.toJson(this.config));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean saveGadgetList(Config updated, int uid) {
        File dir = new File(this.configPath);
        File gadgetFile = new File(this.configPath, "/"+uid+".json");

        if (!dir.exists() || !dir.isDirectory()) {
            if (!new java.io.File(String.valueOf(dir)).mkdirs())
                return false;
        }

        try (FileWriter file = new FileWriter(gadgetFile)) {
            if(updated == null){file.write(JsonUtils.encode(this.config));}
            else{file.write(JsonUtils.encode(updated));}
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean saveBlacklist(ArrayList<Integer> updated) {
        File dir = new File(this.configPath);

        if (!dir.exists() || !dir.isDirectory()) {
            if (!new java.io.File(String.valueOf(dir)).mkdirs())
                return false;
        }

        try (FileWriter file = new FileWriter(this.blacklistFile)) {
            if(updated == null){file.write(JsonUtils.encode(this.blacklistUID));}
            else{file.write(JsonUtils.encode(updated));}
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
