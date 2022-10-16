package thorny.grasscutters.AttackModifier.utils;

import com.google.gson.GsonBuilder;
import emu.grasscutter.Grasscutter;
import thorny.grasscutters.AttackModifier.AttackModifier;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Objects;

public final class ConfigParser {

    private Config config;
    private final String configPath = Grasscutter.getConfig().folderStructure.plugins + "AttackModifier";
    private final File configFile = new File(this.configPath + "/config.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigParser() {
        this.loadConfig();
        // load config ofc
    }

    public Config getConfig() {
        return this.config;
    }

    public void loadConfig() {
        try (FileReader file = new FileReader(this.configFile)) {
            this.config = gson.fromJson(file, Config.class);
            Grasscutter.getLogger().info("[AttackModifier] Config Loaded!");
        } catch (Exception e) {
            this.config = new Config();
            Grasscutter.getLogger().info("[AttackModifier] Basic config creating...");
        }

        if (!saveConfig()) {
            Grasscutter.getLogger().error("[AttackModifier] Unable to save config file.");
        }

        Grasscutter.getLogger().info("[AttackModifier] Plugin loaded successfully!");
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
}
