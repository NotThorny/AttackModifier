package thorny.grasscutters.AttackModifier.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.command.Command.TargetRequirement;

import java.util.List;


// Command usage
@Command(label = "attack", aliases = "at", usage = "[gadgetId]", targetRequirement = TargetRequirement.NONE)
public class AttackModifierCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {

        /*
         * Command usage available to check the gadgets before adding them
         * Just spawns the gadget where the player is standing, given the id
         */

        // Spawn a gadget at the players location and in the direction faced with /at gadgetId 
        var scene = targetPlayer.getScene();
        var pos = targetPlayer.getPosition();
        var rot = targetPlayer.getRotation();
        int thing = Integer.parseInt(args.get(0));

        EntityGadget entity = new EntityGadget(scene, thing, pos, rot);
        scene.addEntity(entity);

    }

    public static void addAttack(GameSession session, int skillId){
        // Get position
        var scene = session.getPlayer().getScene();
        var pos = session.getPlayer().getPosition();
        var rot = session.getPlayer().getRotation();

        int addedAttack = 0; // Default of no gadget
        
        // Currently will only damage the player
        switch (skillId) { // For Raiden
            case 10521: // Basic attack
                addedAttack = 42906105;
                break;
            case 10522: // Elemental skill
                addedAttack = 42906108;
                break;
            case 10525: // Burst
                addedAttack = 42906119;
                break;
            default:
                // Do nothing
                break;
        }

        // Try to set position in front of player to not get hit
        var radius = Math.sqrt(1 * 0.2 / Math.PI);
        var target = pos;
        double angle = rot.getY();
        double r = Math.sqrt(Math.random() * radius * radius);
        target.addX((float) (r * Math.cos(angle))).addZ((float) (r * Math.sin(angle)));
        pos.set(target);
        
        // Only spawn on match
        if(addedAttack != 0){
            EntityGadget att = new EntityGadget(scene, addedAttack, pos, rot);

            // Try to make it not hurt self
            scene.addEntity(att);
            att.setFightProperty(2001, 0);
            att.setFightProperty(1, 0);
        }
    }
}
