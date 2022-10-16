package thorny.grasscutters.AttackModifier.commands;

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
import java.util.ArrayList;
import java.util.List;

// Command usage
@Command(label = "attack", aliases = "at", usage = "on|off|remove \n [gadgetId]", targetRequirement = TargetRequirement.NONE)
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
                case 10000002 -> { // ayaya
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.ayakaIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.ayakaIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.ayakaIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000003 -> { // Jean
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.jeanIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.jeanIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.jeanIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000005 -> { // Traveler Male elementless
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.travelerMaleNoElementIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.travelerMaleNoElementIds.skill.elementalSkill; // Elemental
                        case 2 -> addedAttack = config.travelerMaleNoElementIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000006 -> { // lisa
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.lisaIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.lisaIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.lisaIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000007 -> { // Traveler female elementless
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.travelerFemaleNoElementIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.travelerFemaleNoElementIds.skill.elementalSkill; // Elemental
                        case 2 -> addedAttack = config.travelerFemaleNoElementIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000014 -> { // Barbara
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.barbaraIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.barbaraIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.barbaraIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000015 -> { // kaeya
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.kaeyaIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.kaeyaIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.kaeyaIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000016 -> { // Diluc
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.dilucIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.dilucIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.dilucIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000020 -> { // razor
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.razorIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.razorIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.razorIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000021 -> { // amber
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.amberIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.amberIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.amberIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000022 -> { // venti
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.ventiIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.ventiIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.ventiIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000023 -> { // xiangling
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.xianglingIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.xianglingIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.xianglingIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000024 -> { // beidou
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.beidouIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.beidouIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.beidouIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000025 -> { // xingqiu
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.xingqiuIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.xingqiuIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.xingqiuIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000026 -> { // xiao
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.xiaoIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.xiaoIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.xiaoIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000027 -> { // ningguang
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.ningguangIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.ningguangIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.ningguangIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000029 -> { // klee
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.kleeIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.kleeIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.kleeIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000030 -> { // zhongli
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.zhongliIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.zhongliIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.zhongliIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000031 -> { // fischl
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.fischlIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.fischlIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.fischlIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000032 -> { // bennett
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.bennettIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.bennettIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.bennettIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000033 -> { // tartaglia
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.tartagliaIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.tartagliaIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.tartagliaIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000034 -> { // noelle
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.noelleIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.noelleIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.noelleIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000035 -> { // qiqi
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.qiqiIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.qiqiIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.qiqiIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000036 -> { // chongyun
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.chongyunIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.chongyunIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.chongyunIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000037 -> { // ganyu
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.ganyuIds.skill.normalAtk; // Normal attack
                        case 1 -> addedAttack = config.ganyuIds.skill.elementalSkill; // Elemental skill
                        case 2 -> addedAttack = config.ganyuIds.skill.elementalBurst; // Burst
                    }
                }
                case 10000038 -> { // albedo
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.albedoIds.skill.normalAtk;
                        case 1 -> addedAttack = config.albedoIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.albedoIds.skill.elementalBurst;
                    }
                }
                case 10000039 -> { // diona
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.albedoIds.skill.normalAtk;
                        case 1 -> addedAttack = config.albedoIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.albedoIds.skill.elementalBurst;
                    }
                }
                case 10000041 -> { // mona
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.monaIds.skill.normalAtk;
                        case 1 -> addedAttack = config.monaIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.monaIds.skill.elementalBurst;
                    }
                }
                case 10000042 -> { // keqing
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.keqingIds.skill.normalAtk;
                        case 1 -> addedAttack = config.keqingIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.keqingIds.skill.elementalBurst;
                    }
                }
                case 10000043 -> { // sucrose
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.sucroseIds.skill.normalAtk;
                        case 1 -> addedAttack = config.sucroseIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.sucroseIds.skill.elementalBurst;
                    }
                }
                case 10000044 -> { // xinyan
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.xinyanIds.skill.normalAtk;
                        case 1 -> addedAttack = config.xinyanIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.xinyanIds.skill.elementalBurst;
                    }
                }
                case 10000045 -> { // rosaria
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.rosariaIds.skill.normalAtk;
                        case 1 -> addedAttack = config.rosariaIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.rosariaIds.skill.elementalBurst;
                    }
                }
                case 10000046 -> { // hutao
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.hutaoIds.skill.normalAtk;
                        case 1 -> addedAttack = config.hutaoIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.hutaoIds.skill.elementalBurst;
                    }
                }
                case 10000047 -> { // kazuha
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.kazuhaIds.skill.normalAtk;
                        case 1 -> addedAttack = config.kazuhaIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.kazuhaIds.skill.elementalBurst;
                    }
                }
                case 10000048 -> { // yanfei
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.yanfeiIds.skill.normalAtk;
                        case 1 -> addedAttack = config.yanfeiIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.yanfeiIds.skill.elementalBurst;
                    }
                }
                case 10000049 -> { // yoimiya
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.yoimiyaIds.skill.normalAtk;
                        case 1 -> addedAttack = config.yoimiyaIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.yoimiyaIds.skill.elementalBurst;
                    }
                }
                case 10000050 -> { // tohma
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.thomaIds.skill.normalAtk;
                        case 1 -> addedAttack = config.thomaIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.thomaIds.skill.elementalBurst;
                    }
                }
                case 10000051 -> { // eula
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.eulaIds.skill.normalAtk;
                        case 1 -> addedAttack = config.eulaIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.eulaIds.skill.elementalBurst;
                    }
                }
                case 10000052 -> { // raiden
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.raidenshogunIds.skill.normalAtk;
                        case 1 -> addedAttack = config.raidenshogunIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.raidenshogunIds.skill.elementalBurst;
                    }
                }
                case 10000053 -> { // sayu
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.sayuIds.skill.normalAtk;
                        case 1 -> addedAttack = config.sayuIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.sayuIds.skill.elementalBurst;
                    }
                }
                case 10000054 -> { // kokomi
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.kokomiIds.skill.normalAtk;
                        case 1 -> addedAttack = config.kokomiIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.kokomiIds.skill.elementalBurst;
                    }
                }
                case 10000055 -> { // gorou
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.gorouIds.skill.normalAtk;
                        case 1 -> addedAttack = config.gorouIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.gorouIds.skill.elementalBurst;
                    }
                }
                case 10000056 -> { // sara
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.kujousaraIds.skill.normalAtk;
                        case 1 -> addedAttack = config.kujousaraIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.kujousaraIds.skill.elementalBurst;
                    }
                }
                case 10000057 -> { // itto
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.kujousaraIds.skill.normalAtk;
                        case 1 -> addedAttack = config.kujousaraIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.kujousaraIds.skill.elementalBurst;
                    }
                }
                case 10000058 -> { // yae miko
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.kujousaraIds.skill.normalAtk;
                        case 1 -> addedAttack = config.kujousaraIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.kujousaraIds.skill.elementalBurst;
                    }
                }
                case 10000059 -> { // heizou
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.heizouIds.skill.normalAtk;
                        case 1 -> addedAttack = config.heizouIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.heizouIds.skill.elementalBurst;
                    }
                }
                case 10000060 -> { // yelan
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.yelanIds.skill.normalAtk;
                        case 1 -> addedAttack = config.yelanIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.yelanIds.skill.elementalBurst;
                    }
                }
                case 10000062 -> { // aloy
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.aloyIds.skill.normalAtk;
                        case 1 -> addedAttack = config.aloyIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.aloyIds.skill.elementalBurst;
                    }
                }
                case 10000063 -> { // shenhe
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.shenheIds.skill.normalAtk;
                        case 1 -> addedAttack = config.shenheIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.shenheIds.skill.elementalBurst;
                    }
                }
                case 10000064 -> { // yunjin
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.yunjinIds.skill.normalAtk;
                        case 1 -> addedAttack = config.yunjinIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.yunjinIds.skill.elementalBurst;
                    }
                }
                case 10000065 -> { // shinobu
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.kukishinobuIds.skill.normalAtk;
                        case 1 -> addedAttack = config.kukishinobuIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.kukishinobuIds.skill.elementalBurst;
                    }
                }
                case 10000066 -> { // ayato
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.ayatoIds.skill.normalAtk;
                        case 1 -> addedAttack = config.ayatoIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.ayatoIds.skill.elementalBurst;
                    }
                }
                case 10000067 -> { // collei
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.colleiIds.skill.normalAtk;
                        case 1 -> addedAttack = config.colleiIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.colleiIds.skill.elementalBurst;
                    }
                }
                case 10000068 -> { // dori
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.doriIds.skill.normalAtk;
                        case 1 -> addedAttack = config.doriIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.doriIds.skill.elementalBurst;
                    }
                }
                case 10000069 -> { // tighnari
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.tighnariIds.skill.normalAtk;
                        case 1 -> addedAttack = config.tighnariIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.tighnariIds.skill.elementalBurst;
                    }
                }
                case 10000070 -> { // nilou
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.nilouIds.skill.normalAtk;
                        case 1 -> addedAttack = config.nilouIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.nilouIds.skill.elementalBurst;
                    }
                }
                case 10000071 -> { // cyno
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.cynoIds.skill.normalAtk;
                        case 1 -> addedAttack = config.cynoIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.cynoIds.skill.elementalBurst;
                    }
                }
                case 10000072 -> { // candace
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.candaceIds.skill.normalAtk;
                        case 1 -> addedAttack = config.candaceIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.candaceIds.skill.elementalBurst;
                    }
                }
                case 10000073 -> { // nahida
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.nahidaIds.skill.normalAtk;
                        case 1 -> addedAttack = config.nahidaIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.nahidaIds.skill.elementalBurst;
                    }
                }
                case 10000074 -> { // layla
                    switch (usedAttack) {
                        default -> addedAttack = 0;
                        case 0 -> addedAttack = config.laylaIds.skill.normalAtk;
                        case 1 -> addedAttack = config.laylaIds.skill.elementalSkill;
                        case 2 -> addedAttack = config.laylaIds.skill.elementalBurst;
                    }
                }
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
} // AttackModifierCommand