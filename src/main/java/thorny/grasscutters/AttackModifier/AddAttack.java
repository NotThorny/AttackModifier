package thorny.grasscutters.AttackModifier;

import java.util.ArrayList;
import java.util.List;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.excels.avatar.AvatarSkillDepotData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.Position;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.proto.VisionTypeOuterClass.VisionType;
import emu.grasscutter.server.game.GameSession;
import thorny.grasscutters.AttackModifier.commands.AttackModifierCommand;
import thorny.grasscutters.AttackModifier.utils.CharacterAvatar;
import thorny.grasscutters.AttackModifier.utils.CharacterAvatar.SkillIds;
import thorny.grasscutters.AttackModifier.utils.Config;

public class AddAttack {
    static ArrayList<Integer> blacklistUIDs = AttackModifier.getInstance().getBlackList();
    static List<EntityGadget> activeGadgets = new ArrayList<>(); // Current gadgets
    static List<EntityGadget> removeGadgets = new ArrayList<>(); // To be removed gadgets
    public static int x = 0;
    public static int y = 0;
    public static int z = 0;

    public static void setXYZ(int x, int y, int z) {
        AddAttack.x = x;
        AddAttack.y = y;
        AddAttack.z = z;
    }

    public static void addAttack(GameSession session, int skillId, int uid) {

        if (!(blacklistUIDs.contains(uid))) {
            int fileUid = AttackModifier.getInstance().getConfigUID();

            if (!(fileUid == uid)) {
                Grasscutter.getLogger().debug("[AttackModifier] Loaded player " + uid + " config");
                AttackModifier.getInstance().reloadConfig(uid);
            }

            int addedAttack; // Gadget to add
            int usedAttack = -1; // Default of no attack

            // Get avatar info
            Avatar avatar = session.getPlayer().getTeamManager().getCurrentAvatarEntity().getAvatar();
            AvatarSkillDepotData skillDepot = avatar.getSkillDepot();

            if (skillDepot == null) {
                Grasscutter.getLogger().debug("[AttackModifier] Attempted to get null skill data, skipping.");
                return;
            }

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
            String curName = avatar.getAvatarData().getName().toLowerCase();

            AttackModifier.getInstance().reloadConfig(uid);

            var currentAvatar = AttackModifier.getInstance().getConfig().getCharacters().get(curName);

            // Create new entry if it does not exist
            if (currentAvatar == null) {
                CharacterAvatar temp = new CharacterAvatar(0, 0, 0);
                currentAvatar = temp;

                // Add the new entry
                AttackModifier.getInstance().getConfig().getCharacters().put(curName, currentAvatar);
                AttackModifier.getInstance().saveGadgetConfig(AttackModifier.getInstance().getConfig(), uid);
            }

            if (currentAvatar.getSkills() == null) {
                Grasscutter.getLogger().debug("Skills are null!" + AttackModifier.getInstance().getConfig().toCleanString());
            }

            // Universal switch
            switch (usedAttack) {
                default -> addedAttack = 0;
                case 0 -> addedAttack = currentAvatar.getSkills().normalAtk(); // Normal attack
                case 1 -> addedAttack = currentAvatar.getSkills().elementalSkill(); // Elemental skill
                case 2 -> addedAttack = currentAvatar.getSkills().elementalBurst(); // Burst
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

            // Optional xyz args
            if (x != 0 || y != 0 || z != 0) {
                target.addX(x);
                target.addY(y);
                target.addZ(z);
            }

            // Only spawn on match
            if (addedAttack != 0) {
                EntityGadget att = new EntityGadget(scene, addedAttack, target, rot);

                // Silly way to track gadget alive time
                int currTime = (int) (System.currentTimeMillis() - 1665393100);
                att.setGroupId(currTime);

                activeGadgets.add(att);

                scene.addEntity(att);

                // For future use, this may be helpful in preventing self-damage
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
            } // if
        } // for
          // Remove gadgets and clean list
        activeGadgets.removeAll(removeGadgets);
        removeGadgets.clear();
        AttackModifierCommand.userCalled = false;
    } // removeGadgets

    public static void setGadget(Player targetPlayer, String avatarName, int uid, String attackType, int newGadget) {
        // Get config
        //AttackModifier.getInstance().config.loadGadgetConfig(uid, false);
        Config gadgetConfig = AttackModifier.getInstance().getConfig();

        // Get character
        var changedChar = gadgetConfig.getCharacters().get(avatarName);

        SkillIds charSkills;

        // If it is a new character
        if (changedChar == null) {
            changedChar = new CharacterAvatar(0, 0, 0);
        }

        charSkills = changedChar.getSkills();

        switch (attackType) {
            default -> CommandHandler.sendMessage(targetPlayer, "/at set n|e|q [gadgetId]");
            case "n" -> charSkills.setNormalAtk(newGadget); // Normal attack
            case "e" -> charSkills.setElementalSkill(newGadget); // Elemental skill
            case "q" -> charSkills.setElementalBurst(newGadget); // Burst
        }

        // Set new skills
        changedChar.setSkills(charSkills);

        // Add new values
        gadgetConfig.getCharacters().put(avatarName, changedChar);

        // Save changes
        AttackModifier.getInstance().saveGadgetConfig(gadgetConfig, uid);
    }
}
