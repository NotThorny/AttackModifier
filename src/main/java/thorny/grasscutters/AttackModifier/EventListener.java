package thorny.grasscutters.AttackModifier;

import com.google.protobuf.InvalidProtocolBufferException;

import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.EvtDoSkillSuccNotifyOuterClass.EvtDoSkillSuccNotify;
import emu.grasscutter.server.event.EventHandler;
import emu.grasscutter.server.event.HandlerPriority;
import emu.grasscutter.server.event.game.ReceivePacketEvent;
import thorny.grasscutters.AttackModifier.commands.AttackModifierCommand;

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
            if (event.getPacketId() == PacketOpcodes.EvtDoSkillSuccNotify){
                EvtDoSkillSuccNotify notify = null;
                try {
                    notify = EvtDoSkillSuccNotify.parseFrom(event.getPacketData());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

                // Get packet info
                var session = event.getGameSession();
                int skillId = notify.getSkillId();

                // Send to addAttack
                AttackModifierCommand.addAttack(session, skillId);
            }
        }
}
