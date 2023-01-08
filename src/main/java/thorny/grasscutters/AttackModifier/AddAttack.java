package thorny.grasscutters.AttackModifier;

import java.util.ArrayList;
import java.util.List;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.excels.AvatarSkillDepotData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.proto.VisionTypeOuterClass.VisionType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSceneEntityDisappearNotify;
import emu.grasscutter.utils.Position;
import thorny.grasscutters.AttackModifier.commands.AttackModifierCommand;
import thorny.grasscutters.AttackModifier.utils.Config;
import thorny.grasscutters.AttackModifier.utils.Config.characters;

public class AddAttack {

    static ArrayList<Integer> blacklistUIDs = AttackModifier.getInstance().config.getBlacklist();
    static List<EntityGadget> activeGadgets = new ArrayList<>(); // Current gadgets
    static List<EntityGadget> removeGadgets = new ArrayList<>(); // To be removed gadgets

    public static void addAttack(GameSession session, int skillId, int uid) {

        if (!(blacklistUIDs.contains(uid))) {
            int fileUid = AttackModifier.getInstance().config.getGadgetConfigUid();

            if (!(fileUid == uid)) {
                AttackModifier.getInstance().config.loadGadgetConfig(uid);
            }
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
                currentAvatar = AddAttack.getCharacter.getCurrent(curName);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                    | IllegalAccessException e) {
                // Should only be called when config is missing entry for the active character
                Grasscutter.getLogger().info("Invalid or missing config for: " + curName);
                e.printStackTrace();
                return;
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
                // att.setFightProperty(2001, 0);
                // att.setFightProperty(1, 0);

            }
            // Remove all gadgets when list not empty
            if (!activeGadgets.isEmpty()) {
                removeGadgets(scene);
            } // if
        } // if toAdd
    } // addAttack

    public static void removeGadgets(Scene scene) {
        for (EntityGadget gadget : activeGadgets) {

            // When gadgets have lived for 15 sec
            if (AttackModifierCommand.userCalled
                    || (int) (System.currentTimeMillis() - 1665393100) > (gadget.getGroupId() + 15000)) {
                // Add to removal list
                removeGadgets.add(gadget);

                // Remove entity
                scene.removeEntity(gadget, VisionType.VISION_TYPE_REMOVE);
                //scene.broadcastPacket(new PacketSceneEntityDisappearNotify(gadget, VisionType.VISION_TYPE_REMOVE));
            } // if
        } // for
          // Remove gadgets and clean list
        activeGadgets.removeAll(removeGadgets);
        removeGadgets.clear();
        AttackModifierCommand.userCalled = false;
    } // removeGadgets

    public static class getCharacter {
        public static characters getCurrent(String curName)
                throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
            characters curr = new characters();
            var me = AttackModifier.getInstance().config.getGadgetConfig().getClass().getDeclaredField(curName);
            me.setAccessible(true);
            curr = (characters) me.get(AttackModifier.getInstance().config.getGadgetConfig());
            return curr;
        }
    }
    
    public static void setGadget(Player targetPlayer, String avatarName, int uid, String attackType, int newGadget) {
        characters avatarToChange = null;
        Config gadgetConfig = AttackModifier.getInstance().config.getGadgetConfig();
        try {
            avatarToChange = AddAttack.getCharacter.getCurrent(avatarName);
            CommandHandler.sendMessage(targetPlayer, "Setting " + attackType + " to " + newGadget);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            CommandHandler.sendMessage(targetPlayer, "Failed to set gadget! Change in plugins/AttackModifier/config.json");
        }
        switch (attackType) {
            default ->  CommandHandler.sendMessage(targetPlayer, "/at set n|e|q [gadgetId]");
            case "n" -> avatarToChange.skill.normalAtk = newGadget; // Normal attack
            case "e" -> avatarToChange.skill.elementalSkill = newGadget; // Elemental skill
            case "q" -> avatarToChange.skill.elementalBurst = newGadget; // Burst
        }
        AttackModifier.getInstance().saveGadgetConfig(gadgetConfig, uid);
    }
}
