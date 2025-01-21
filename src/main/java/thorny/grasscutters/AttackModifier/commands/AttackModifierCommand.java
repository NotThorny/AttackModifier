package thorny.grasscutters.AttackModifier.commands;

import java.util.ArrayList;
import java.util.List;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.Command.TargetRequirement;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import thorny.grasscutters.AttackModifier.AddAttack;
import thorny.grasscutters.AttackModifier.AttackModifier;
import thorny.grasscutters.AttackModifier.utils.Config;

// Command usage
@Command(label = "attack", aliases = "at", usage = "on|off|remove \n set n|e|q [gadgetId]", targetRequirement = TargetRequirement.PLAYER)
public class AttackModifierCommand implements CommandHandler {
    static ArrayList<Integer> blacklistUIDs = AttackModifier.getInstance().getBlackList();
    public static final Config gadgetConfig = AttackModifier.getInstance().getConfig();

    public static boolean toAdd = true; // Default state to add attacks
    public static boolean userCalled = false; // Whether removeGadget was called by the user

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {

        /*
         * Command usage available to check the gadgets before adding them
         * Just spawns the gadget where the player is standing, given the id
         * Also allows turning on/off added attacks and removing all active gadgets
         */

        // Spawn a gadget at the players location and in the direction faced with /at gadgetId
        var scene = targetPlayer.getScene();
        var pos = targetPlayer.getPosition();
        var rot = targetPlayer.getRotation();
        int thing = 0;
        int newGadget;
        String state;
        String avatarName = targetPlayer.getTeamManager().getCurrentAvatarEntity().getAvatar().getAvatarData().getName()
                .toLowerCase();
        int uid = targetPlayer.getUid();
        int x = 0;
        int y = 0;
        int z = 0;

        state = args.get(0);
        try {
            thing = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
        }

        // Change whether added attacks should be on or not
        if (state.equals("off")) {
            if (blacklistUIDs.contains(uid)) {
                CommandHandler.sendMessage(targetPlayer, "Added attacks already disabled!");
            } else {
                blacklistUIDs.add(uid);
                AttackModifier.getInstance().saveBlacklist(blacklistUIDs);
                CommandHandler.sendMessage(targetPlayer, "Disabled added attacks!");
            }
        }

        if (state.equals("on")) {
            if (blacklistUIDs.contains(uid)) {
                blacklistUIDs.remove(Integer.valueOf(uid));
                AttackModifier.getInstance().saveBlacklist(blacklistUIDs);
                CommandHandler.sendMessage(targetPlayer, "Enabled added attacks!");
            } else {
                CommandHandler.sendMessage(targetPlayer, "Added attacks already enabled!!");
            }
        }

        if (state.equals("remove")) {
            userCalled = true;
            AddAttack.removeGadgets(scene);
            CommandHandler.sendMessage(targetPlayer, "Removed all active gadgets!");
        }
        if (state.equals("set")) {
            var attackType = args.get(1).toLowerCase();
            try {
                newGadget = Integer.parseInt(args.get(2));
            } catch (NumberFormatException e) {
                sendUsageMessage(targetPlayer);
                return;
            }
            try {
                if (args.size() > 3) {
                    for (var a : args) {
                        if (a.startsWith("x")) {
                            x = Integer.parseInt(a.substring(1));
                        }
                        if (a.startsWith("y")) {
                            y = Integer.parseInt(a.substring(1));
                        }
                        if (a.startsWith("z")) {
                            z = Integer.parseInt(a.substring(1));
                        }
                    }
                    AddAttack.setXYZ(x, y, z);
                    CommandHandler.sendMessage(targetPlayer, "Set spawn coordinates of: x" + x + ", y" + y + ", z" + z);
                }
            } catch (NumberFormatException e) {
                CommandHandler.sendMessage(targetPlayer,
                        "Coordinates may be invalid. Ensure they match the format of x123 y123 z123. Only the desired x, y, or z is required.\n If you only want to change y, include just y123.");
            }

            AddAttack.setGadget(targetPlayer, avatarName, uid, attackType, newGadget);
            CommandHandler.sendMessage(targetPlayer, "Set new gadget!");
            return;
        }

        EntityGadget entity = new EntityGadget(scene, thing, pos, rot);
        scene.addEntity(entity);

    }
} // AttackModifierCommand