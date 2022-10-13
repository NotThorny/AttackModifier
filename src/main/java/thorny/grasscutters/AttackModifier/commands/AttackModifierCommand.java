package thorny.grasscutters.AttackModifier.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.VisionTypeOuterClass.VisionType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneEntityDisappearNotify;
import emu.grasscutter.utils.Position;
import emu.grasscutter.command.Command.TargetRequirement;

import java.util.ArrayList;
import java.util.List;


// Command usage
@Command(label = "attack", aliases = "at", usage = "[gadgetId]", targetRequirement = TargetRequirement.NONE)
public class AttackModifierCommand implements CommandHandler {

    static List<EntityGadget> activeGadgets = new ArrayList<>(); // Current gadgets
    static List<EntityGadget> removeGadgets = new ArrayList<>(); // To be removed gadgets

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

        int addedAttack = 0; // Default of no gadget
        String attType = ""; // Default of no type
        
        // Currently will only damage the player
        switch (skillId) { // For Raiden
            case 10521: // Basic attack
                addedAttack = 42906105;
                attType = "basic";
                break;
            case 10522: // Elemental skill
                addedAttack = 42906108;
                attType = "elemental";
                break;
            case 10525: // Burst
                addedAttack = 42906119;
                attType = "burst";
                break;
            default:
                // Do nothing
                break;
        }

        // Get position
        var scene = session.getPlayer().getScene();
        Position pos = new Position(session.getPlayer().getPosition());
        Position rot = new Position(session.getPlayer().getRotation());
        var r = 3;

        // Try to set position in front of player to not get hit
        double angle = rot.getY();
        Position target = new Position(pos);

        // Only change gadget pos for basic attack
        if(attType.equals("basic")){
            target.addX((float) (r * Math.sin(Math.PI/180 * angle)));
            target.addZ((float) (r * Math.cos(Math.PI/180 * angle)));
        }
        
        // Only spawn on match
        if(addedAttack != 0){
            EntityGadget att = new EntityGadget(scene, addedAttack, target, rot);

            // Silly way to track gadget alive time
            int currTime = (int)(System.currentTimeMillis() - 1665393100);
            att.setGroupId(currTime);
            
            activeGadgets.add(att);
            // Try to make it not hurt self
            scene.addEntity(att);
            att.setFightProperty(2001, 0);
            att.setFightProperty(1, 0);
            
        }
        // Remove all gadgets when list not empty
        if(!activeGadgets.isEmpty()){
            for (EntityGadget gadget : activeGadgets) {

                // When gadgets have lived for 15 sec
                if((int)(System.currentTimeMillis() - 1665393100) > (gadget.getGroupId()+15000)){
                    // Add to removal list
                    removeGadgets.add(gadget);
                    
                    // Remove entity
                    scene.removeEntity(gadget, VisionType.VISION_TYPE_REMOVE);
                    scene.broadcastPacket(new PacketSceneEntityDisappearNotify(gadget, VisionType.VISION_TYPE_REMOVE));
                }
            }
            // Remove gadgets and clean list
            activeGadgets.removeAll(removeGadgets);
            removeGadgets.clear();
        } // if
    } // addAttack
} // AttackModifierCommand
