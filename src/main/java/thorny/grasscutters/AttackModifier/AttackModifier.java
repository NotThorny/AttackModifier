package thorny.grasscutters.AttackModifier;

import emu.grasscutter.plugin.Plugin;
import emu.grasscutter.server.event.EventHandler;
import emu.grasscutter.server.event.HandlerPriority;
import emu.grasscutter.server.event.game.ReceivePacketEvent;
import thorny.grasscutters.AttackModifier.utils.*;

public final class AttackModifier extends Plugin {
    private static AttackModifier instance;
    public ConfigParser config;

    public static AttackModifier getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        // Set the plugin instance.
        instance = this;
        this.config = new ConfigParser();
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

    public void getConfig() {
        config.getConfig();
    }

    public void reloadConfig() {
        config.loadConfig();
    }

}