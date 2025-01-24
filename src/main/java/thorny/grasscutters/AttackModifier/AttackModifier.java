package thorny.grasscutters.AttackModifier;

import java.util.ArrayList;

import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.plugin.Plugin;
import emu.grasscutter.server.event.EventHandler;
import emu.grasscutter.server.event.HandlerPriority;
import emu.grasscutter.server.event.game.ReceivePacketEvent;
import thorny.grasscutters.AttackModifier.utils.Config;
import thorny.grasscutters.AttackModifier.utils.ConfigParser;

public final class AttackModifier extends Plugin {
    private static AttackModifier instance;
    private ConfigParser config;
    private int skillSuccPacketId;

    public static AttackModifier getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        // Set the plugin instance.
        instance = this;
        this.config = new ConfigParser();
        setSkillSuccPacketId();
        this.getLogger().info("Loaded yay");
    }

    @Override
    public void onEnable() {
        new EventHandler<>(ReceivePacketEvent.class)
                .priority(HandlerPriority.NORMAL)
                .listener(EventListener::onPacket)
                .register(this);

        // Register commands.
        this.getHandle().registerCommand(new thorny.grasscutters.AttackModifier.commands.AttackModifierCommand());

        // Log a plugin status message.
        this.getLogger().info("The Attack Modifier plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // Log a plugin status message.
        this.getLogger().info("Attack Modifier has been disabled.");
    }

    // Modified from PacketOpcodeUtils
    private void setSkillSuccPacketId() {
        var fields = PacketOpcodes.class.getFields();
        for (var f : fields) {
            if (f.getType().equals(int.class)) {
                if (f.getName().equals("EvtDoSkillSuccNotify")) {
                    try {
                        this.skillSuccPacketId = f.getInt(null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    // Found packet, stop checking
                    return;
                }
            }
        }
    }

    public int getSkillSuccPacketId() {
        return this.skillSuccPacketId;
    }

    public Config getConfig() {
        return this.config.getGadgetConfig();
    }

    public void setConfig(ConfigParser config) {
        this.config = config;
    }

    public void reloadConfig(Config updated) {
        this.config.setConfig(updated);
        this.config.loadConfig();
        instance = this;
    }

    public void reloadConfig(int uid) {
        this.config.loadGadgetConfig(uid);
        instance = this;
    }

    public int getConfigUID() {
        return this.config.getGadgetConfigUid();
    }

    public void saveGadgetConfig(Config updated, int uid) {
        this.config.saveGadgetList(updated, uid);
        this.config.loadGadgetConfig(uid);
    }

    public ArrayList<Integer> getBlackList() {
        return this.config.getBlacklist();
    }

    public void saveBlacklist(ArrayList<Integer> blacklistUIDs) {
        this.config.saveBlacklist(blacklistUIDs);
        this.config.loadBlacklist();
    }

}