package thorny.grasscutters.AttackModifier;

import com.google.protobuf.InvalidProtocolBufferException;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.proto.EvtDoSkillSuccNotifyOuterClass.EvtDoSkillSuccNotify;
import emu.grasscutter.server.event.EventHandler;
import emu.grasscutter.server.event.HandlerPriority;
import emu.grasscutter.server.event.game.ReceivePacketEvent;

/**
 * A class containing all event handlers.
 * Syntax in event handler methods are similar to CraftBukkit.
 * To register an event handler, create a new instance of {@link EventHandler}.
 * Pass through the event class you want to handle. (ex. `new
 * EventHandler<>(PlayerJoinEvent.class);`)
 * You can change the point at which the handler method is invoked with
 * {@link EventHandler#priority(HandlerPriority)}.
 * You can set whether the handler method should be invoked when another plugin
 * cancels the event with {@link EventHandler#ignore(boolean)}.
 */
public final class EventListener {
    public static void onPacket(ReceivePacketEvent event) {
        if (event.getPacketId() == AttackModifier.getInstance().getSkillSuccPacketId()) {
            EvtDoSkillSuccNotify notify = null;
            try {
                notify = EvtDoSkillSuccNotify.parseFrom(event.getPacketData());
            } catch (InvalidProtocolBufferException e) {
                Grasscutter.getLogger().error("Failed to parse packet data for EvtDoSkillSuccNotify");
            }

            // Sanity check
            if (notify == null) {
                return;
            }

            // Get packet info
            var session = event.getGameSession();
            int skillId = notify.getSkillId();
            int uuid = session.getPlayer().getUid();

            // Send to addAttack
            AddAttack.addAttack(session, skillId, uuid);
        }
    }
}
