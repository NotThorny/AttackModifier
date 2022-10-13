package thorny.grasscutters.AttackModifier.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.VisionTypeOuterClass.VisionType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneEntityDisappearNotify;
import emu.grasscutter.utils.Position;
import emu.grasscutter.command.Command.TargetRequirement;
import emu.grasscutter.data.excels.AvatarSkillDepotData;

import java.util.ArrayList;
import java.util.List;


// Command usage
@Command(label = "attack", aliases = "at", usage = "on|off \n [gadgetId]", targetRequirement = TargetRequirement.NONE)
public class AttackModifierCommand implements CommandHandler {

    static List<EntityGadget> activeGadgets = new ArrayList<>(); // Current gadgets
    static List<EntityGadget> removeGadgets = new ArrayList<>(); // To be removed gadgets

    public static boolean toAdd = true;

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {

        /*
         * Command usage available to check the gadgets before adding them
         * Just spawns the gadget where the player is standing, given the id
         * Also allows turning on/off added attacks
         */

        // Spawn a gadget at the players location and in the direction faced with /at gadgetId 
        var scene = targetPlayer.getScene();
        var pos = targetPlayer.getPosition();
        var rot = targetPlayer.getRotation();
        int thing = 0;
        String state = "on";
        
        try {
            state = args.get(0);
        } catch (Exception e) {
            thing = Integer.parseInt(args.get(0));
        }
        
        // Change whether added attacks should be on or not
        if(state.equals("off")){
            toAdd = false;
        }
        if(state.equals("on")){
            toAdd = true;
        }

        EntityGadget entity = new EntityGadget(scene, thing, pos, rot);
        scene.addEntity(entity);

    }

    public static void addAttack(GameSession session, int skillId){

        if(toAdd){

        int addedAttack = -1; // Default of no gadget
        int usedAttack = -1; // Default of no attack

        // Get avatar info
        Avatar avatar = session.getPlayer().getTeamManager().getCurrentAvatarEntity().getAvatar();
        AvatarSkillDepotData skillDepot = avatar.getSkillDepot();
        int avatarId = avatar.getAvatarId();

        // Check what skill type was used
        if (skillId == (skillDepot.getSkills().get(0))) {
            usedAttack = 0;
        }
        if (skillId == (skillDepot.getSkills().get(1))) {
            usedAttack = 1;
        }
        if (skillId == (skillDepot.getEnergySkill())) {
            usedAttack = 2;
        }

        // Use attack for specific avatar
        switch (avatarId) {
            default -> usedAttack = -1;
            case 10000052 -> { // Raiden
                switch (usedAttack) {
                    default -> addedAttack = -1;
                    case 0 -> addedAttack = 42906105; // Normal attack
                    case 1 -> addedAttack = 42906108; // Elemental skill
                    case 2 -> addedAttack = 42906119; // Burst
                }
            }
            // Dummy example case for additional avatars
            case 10000063 -> { // Shenhe
                switch (usedAttack) {
                    default -> addedAttack = -1;
                    case 0 -> addedAttack = 41069031; // Normal attack
                    case 1 -> addedAttack = 41069021; // Elemental skill
                    case 2 -> addedAttack = 41022001; // Burst
                }
            } // Shenhe
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
        if(usedAttack == 0){
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
    } // if toAdd
} // addAttack
} // AttackModifierCommand
