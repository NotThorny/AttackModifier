package thorny.grasscutters.AttackModifier.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.utils.JsonUtils;
import thorny.grasscutters.AttackModifier.AttackModifier;

public final class ConfigParser {

    private Config gadgetConfig;
    private int gadgetConfigUid;
    private ArrayList<Integer> blacklistUID = new ArrayList<>();
    private final String configPath = Grasscutter.getConfig().folderStructure.plugins + "AttackModifier";
    private final File blacklistFile = new File(this.configPath + "/blacklist.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();

    public ConfigParser() {
        // Load config
        this.loadConfig();
        this.loadBlacklist();
    }

    public Config getGadgetConfig() {
        return this.gadgetConfig;
    }

    public ArrayList<Integer> getBlacklist() {
        return this.blacklistUID;
    }

    public int getGadgetConfigUid() {
        return this.gadgetConfigUid;
    }

    public void setConfig(Config config) {
        this.gadgetConfig = config;
    }

    public void reloadInstance() {
        AttackModifier.getInstance().onLoad();
    }

    public void loadConfig() {
        loadGadgetConfig(0, false);
        Grasscutter.getLogger().info("[AttackModifier] Config Loaded!");

        if (!saveGadgetList(gadgetConfig, 0)) {
            Grasscutter.getLogger().error("[AttackModifier] Unable to save config file.");
        }

        Grasscutter.getLogger().info("[AttackModifier] Plugin loaded successfully!");
    }

    public void loadBlacklist() {
        try (FileReader file = new FileReader(this.blacklistFile)) {
            this.blacklistUID = gson.fromJson(file, listType);
        } catch (Exception e) {
            saveBlacklist(null);
        }
    }

    public void loadGadgetConfig(int uid) {
        File gadgetFile = new File(this.configPath, "/" + uid + ".json");
        try (FileReader file = new FileReader(gadgetFile)) {
            this.gadgetConfig = gson.fromJson(file, Config.class);

            this.gadgetConfigUid = uid;
        } catch (Exception e) {
            this.gadgetConfig = new Config();
            this.gadgetConfig.setDefaults();
            saveGadgetList(gadgetConfig, uid);
        }
    }

    public void loadGadgetConfig(int uid, boolean looping) {
        File gadgetFile = new File(this.configPath, "/" + uid + ".json");
        try (FileReader file = new FileReader(gadgetFile)) {
            this.gadgetConfig = gson.fromJson(file, Config.class);

            // For configs that slip through
            if (JsonUtils.encode(gadgetConfig).isEmpty() || JsonUtils.encode(gadgetConfig).equals("{}")) {
                throw new JsonSyntaxException("Invalid syntax");
            }

            this.gadgetConfigUid = uid;
        } catch (JsonSyntaxException e) { // Old configs
            Grasscutter.getLogger().info("Old config detected, fixing!");

            try (FileReader file2 = new FileReader(gadgetFile)) {
                File dir = new File(this.configPath);
                File[] dirList = dir.listFiles();

                // Load with old config class
                OldConfig old = gson.fromJson(file2, OldConfig.class);
                // Why use many word when few do trick
                var changed = """
                              {
                                "characters": """ + JsonUtils.encode(old) + "\n}";
                // Change names to match current
                changed = changed.replaceAll("Ids", "");
                // Write changes
                try (FileWriter filew = new FileWriter(gadgetFile)) {
                    filew.write(changed);
                } catch (Exception ear) {
                    Grasscutter.getLogger().error("Unable to save fixed config! It will be reset.");
                    return;
                }

                // Don't go into this while already looping
                if (!looping) {
                    if (dirList != null) {
                        for (File child : dirList) {
                            if (child.getName().contains("blacklist") || child.getName().contains("config")) {
                                continue;
                            }

                            loadGadgetConfig(Integer.parseInt(child.getName().replace(".json", "")), true);
                        }
                    }
                }

                if (this.gadgetConfig == null) {
                    // Worst case, reset the config to avoid locking instance as null
                    Grasscutter.getLogger().info("AttackModifier config unable to be fixed or loaded properly, resetting it!");
                    reloadInstance();
                }
            } catch (Exception er) {
                // This should never happen
                Grasscutter.getLogger().warn(
                        "Your AttackModifier configs have failed during updating, and may experience major problems such as a full reset!");
            }
        } catch (JsonIOException | IOException e) { // Missing configs
            this.gadgetConfig = new Config();
            this.gadgetConfig.setDefaults();
            saveGadgetList(gadgetConfig, uid);
        }
    }

    public boolean saveGadgetList(Config updated, int uid) {
        File dir = new File(this.configPath);
        File gadgetFile = new File(this.configPath, "/" + uid + ".json");

        if (!dir.exists() || !dir.isDirectory()) {
            if (!new java.io.File(String.valueOf(dir)).mkdirs()) {
                return false;
            }
        }

        try (FileWriter file = new FileWriter(gadgetFile)) {
            if (updated == null) {
                file.write(JsonUtils.encode(this.gadgetConfig));
            } else {
                file.write(JsonUtils.encode(updated));
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean saveBlacklist(ArrayList<Integer> updated) {
        File dir = new File(this.configPath);

        if (!dir.exists() || !dir.isDirectory()) {
            if (!new java.io.File(String.valueOf(dir)).mkdirs()) {
                return false;
            }
        }

        try (FileWriter file = new FileWriter(this.blacklistFile)) {
            if (updated == null) {
                file.write(JsonUtils.encode(this.blacklistUID));
            } else {
                file.write(JsonUtils.encode(updated));
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
