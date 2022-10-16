package thorny.grasscutters.AttackModifier.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.proto.VisionTypeOuterClass.VisionType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneEntityDisappearNotify;
import emu.grasscutter.utils.Position;
import emu.grasscutter.command.Command.TargetRequirement;
import emu.grasscutter.data.excels.AvatarSkillDepotData;
import thorny.grasscutters.AttackModifier.AttackModifier;
import thorny.grasscutters.AttackModifier.utils.*;
import thorny.grasscutters.AttackModifier.utils.Config.characters;

import java.util.ArrayList;
import java.util.List;

// Command usage
@Command(label = "attack", aliases = "at", usage = "on|off|remove|reload \n set n|e|q [gadgetId]", targetRequirement = TargetRequirement.NONE)
public class AttackModifierCommand implements CommandHandler {
    private static final Config config = AttackModifier.getInstance().config.getConfig();

    static List<EntityGadget> activeGadgets = new ArrayList<>(); // Current gadgets
    static List<EntityGadget> removeGadgets = new ArrayList<>(); // To be removed gadgets

    public static boolean toAdd = true; // Default state to add attacks
    public static boolean userCalled = false; // Whether removeGadget was called by the user

    public void execute(Player sender, Player targetPlayer, List<String> args) {

        /*
         * Command usage available to check the gadgets before adding them
         * Just spawns the gadget where the player is standing, given the id
         * Also allows turning on/off added attacks and removing all active gadgets
         */

        // Spawn a gadget at the players location and in the direction faced with /at
        // gadgetId
        var scene = targetPlayer.getScene();
        var pos = targetPlayer.getPosition();
        var rot = targetPlayer.getRotation();
        int thing = 0;
        String state = "on";

        state = args.get(0);
        try {
            thing = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
        }

        // Change whether added attacks should be on or not
        if (state.equals("off")) {
            toAdd = false;
            CommandHandler.sendMessage(targetPlayer, "Disabled added attacks!");
        }
        if (state.equals("on")) {
            toAdd = true;
            CommandHandler.sendMessage(targetPlayer, "Enabled added attacks!");
        }
        if (state.equals("remove")) {
            userCalled = true;
            removeGadgets(scene);
            CommandHandler.sendMessage(targetPlayer, "Removed all active gadgets!");
        }
        if (state.equals("reload")) {
            userCalled = true;
            AttackModifier.getInstance().reloadConfig();
            CommandHandler.sendMessage(targetPlayer, "Reloaded config!");
        }

        EntityGadget entity = new EntityGadget(scene, thing, pos, rot);
        scene.addEntity(entity);

    }

    public static void addAttack(GameSession session, int skillId) {

        if (toAdd) {

            int addedAttack = 0; // Default of no gadget
            int usedAttack = -1; // Default of no attack

            // Get avatar info
            Avatar avatar = session.getPlayer().getTeamManager().getCurrentAvatarEntity().getAvatar();
            AvatarSkillDepotData skillDepot = avatar.getSkillDepot();

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

            // Get current avatar name
            String curName = avatar.getAvatarData().getName().toLowerCase() + "Ids";
            characters currentAvatar = null;

            // Get avatar from config
            try {
                currentAvatar = getCharacter.getCurrent(curName);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                // Should only be called when config is missing entry for the active character
                Grasscutter.getLogger().info("Invalid or missing config for: " + curName);
                e.printStackTrace();
            }

            // Universal switch
            switch (usedAttack) {
                default -> addedAttack = 0;
                case 0 -> addedAttack = currentAvatar.skill.normalAtk; // Normal attack
                case 1 -> addedAttack = currentAvatar.skill.elementalSkill; // Elemental skill
                case 2 -> addedAttack = currentAvatar.skill.elementalBurst; // Burst
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
            if (usedAttack == 0) {
                target.addX((float) (r * Math.sin(Math.PI / 180 * angle)));
                target.addZ((float) (r * Math.cos(Math.PI / 180 * angle)));
            }

            // Only spawn on match
            if (addedAttack != 0) {
                EntityGadget att = new EntityGadget(scene, addedAttack, target, rot);

                // Silly way to track gadget alive time
                int currTime = (int) (System.currentTimeMillis() - 1665393100);
                att.setGroupId(currTime);

                activeGadgets.add(att);

                // Try to make it not hurt self
                scene.addEntity(att);
                att.setFightProperty(2001, 0);
                att.setFightProperty(1, 0);

            }
            // Remove all gadgets when list not empty
            if (!activeGadgets.isEmpty()) {
                removeGadgets(scene);
            } // if
        } // if toAdd
    } // addAttack

    private static void removeGadgets(Scene scene) {
        for (EntityGadget gadget : activeGadgets) {

            // When gadgets have lived for 15 sec
            if (userCalled || (int) (System.currentTimeMillis() - 1665393100) > (gadget.getGroupId() + 15000)) {
                // Add to removal list
                removeGadgets.add(gadget);

                // Remove entity
                scene.removeEntity(gadget, VisionType.VISION_TYPE_REMOVE);
                scene.broadcastPacket(new PacketSceneEntityDisappearNotify(gadget, VisionType.VISION_TYPE_REMOVE));
            } // if
        } // for
          // Remove gadgets and clean list
        activeGadgets.removeAll(removeGadgets);
        removeGadgets.clear();
        userCalled = false;
    } // removeGadgets

    public static class getCharacter {
        public static characters getCurrent(String curName)
                throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
            characters curr = new characters();
            var me = config.getClass().getDeclaredField(curName);
            me.setAccessible(true);
            curr = (characters) me.get(config);
            return curr;
        }
    }
} // AttackModifierCommand