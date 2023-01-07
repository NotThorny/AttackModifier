package thorny.grasscutters.AttackModifier.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.command.Command.TargetRequirement;
import thorny.grasscutters.AttackModifier.AddAttack;
import thorny.grasscutters.AttackModifier.AttackModifier;
import thorny.grasscutters.AttackModifier.utils.*;

import java.util.ArrayList;
import java.util.List;

// Command usage
@Command(label = "attack", aliases = "at", usage = "on|off|remove|reload \n set n|e|q [gadgetId]", targetRequirement = TargetRequirement.PLAYER)
public class AttackModifierCommand implements CommandHandler {
    static ArrayList<Integer> blacklistUIDs = AttackModifier.getInstance().config.getBlacklist();
    public static final Config gadgetConfig = AttackModifier.getInstance().config.getGadgetConfig();

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
        int newGadget = -1;
        String state;
        String avatarName = targetPlayer.getTeamManager().getCurrentAvatarEntity().getAvatar().getAvatarData().getName().toLowerCase() + "Ids";
        int uid = targetPlayer.getUid();        

        state = args.get(0);
        try {
            thing = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
        }

        // Change whether added attacks should be on or not
        if (state.equals("off")) {
            if(blacklistUIDs.contains(uid)){
                CommandHandler.sendMessage(targetPlayer, "Added attacks already disabled!");
            }else{blacklistUIDs.add(uid);
                AttackModifier.getInstance().saveBlacklist(blacklistUIDs);
                CommandHandler.sendMessage(targetPlayer, "Disabled added attacks!");}   
        }

        if (state.equals("on")) {
            if(blacklistUIDs.contains(uid)){
                blacklistUIDs.remove(Integer.valueOf(uid));
                AttackModifier.getInstance().saveBlacklist(blacklistUIDs);
                CommandHandler.sendMessage(targetPlayer, "Enabled added attacks!");
            }else{CommandHandler.sendMessage(targetPlayer, "Added attacks already enabled!!");}    
        }

        if (state.equals("remove")) {
            userCalled = true;
            AddAttack.removeGadgets(scene);
            CommandHandler.sendMessage(targetPlayer, "Removed all active gadgets!");
        }
        if (state.equals("reload")) {
            userCalled = true;
            AttackModifier.getInstance().reloadConfig();
            CommandHandler.sendMessage(targetPlayer, "Reloaded config!");
        }
        if (state.equals("set")){
            var attackType = args.get(1).toLowerCase();
            try{newGadget = Integer.parseInt(args.get(2));}catch(Exception e){sendUsageMessage(targetPlayer); return;}
            AddAttack.setGadget(targetPlayer, avatarName, uid, attackType, newGadget);
            CommandHandler.sendMessage(targetPlayer, "Set new gadget!");
        }

        EntityGadget entity = new EntityGadget(scene, thing, pos, rot);
        scene.addEntity(entity);

    }
} // AttackModifierCommand