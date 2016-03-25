package com.github.badfalcon.Easter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class EasterCommandExecutor implements CommandExecutor, TabCompleter {

    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfex) {
            return false;
        }
    }

    EasterMain plugin;
    BukkitTask gameTimeCount;
    BukkitTask gameEnd;


    public EasterCommandExecutor(EasterMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        World world = Bukkit.getServer().getWorlds().get(0);

        FileConfiguration config = plugin.getConfig();

        if (args.length == 0) {
            sender.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
            return false;

        } else if (args[0].equals("help")) {
            sender.sendMessage(EasterMain.messagePrefix
                    + "エッグハント ヘルプ\n"
                    + "/r - リスポーン(ゲーム中のみ)\n"
                    + "/egghunt help - ヘルプの表示\n"
                    + "/egghunt set spawn - スポーン地点を設定\n"
                    + "/egghunt set field - 卵の生成範囲の設定\n"
                    + "/egghunt start - ゲームの開始\n"
                    + "/egghunt stop - ゲームの終了\n"
                    + "/egghunt eggs set - 卵の生成\n"
                    + "/egghunt eggs clear - 卵の消去\n"
                    + "/egghunt option inLava [bool] - 溶岩内に卵を生成するか\n"
                    + "/egghunt option inWater [bool] - 水中に卵を生成するか\n"
                    + "/egghunt option onSnow [bool] - 雪の上に卵を生成するか\n"
                    + "/egghunt option eggsPossibility [int] - 卵の生成確率を1/[int]に設定");

        } else if (args[0].equals("set")) {
            if (world.hasMetadata("ingame")) {
                sender.sendMessage(EasterMain.messagePrefix + "ゲーム中にはこのコマンドは実行できません");
                return false;
            }

            if (sender instanceof Player) {
                if (args[1] == null) {
                    sender.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
                    return false;
                }
                final Player player = (Player) sender;
                if (!player.hasMetadata("CurrentLocationX")) {
                    player.sendMessage(EasterMain.messagePrefix + "位置が選択されていません");
                    return false;
                }
                double currentLocationX = player.getMetadata("CurrentLocationX").get(0).asDouble();
                double currentLocationY = player.getMetadata("CurrentLocationY").get(0).asDouble();
                double currentLocationZ = player.getMetadata("CurrentLocationZ").get(0).asDouble();
                float currentLocationYaw = player.getMetadata("CurrentLocationYaw").get(0).asFloat();

                List<Float> locyaw1 = new ArrayList<Float>();
                locyaw1.add(currentLocationYaw);

                // lobby

                if (args[1].equals("spawn")) {
                    if (args.length != 2) {
                        player.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
                        return false;
                    }
                    world.setMetadata("SpawnLocationX", new FixedMetadataValue(plugin, currentLocationX));
                    world.setMetadata("SpawnLocationY", new FixedMetadataValue(plugin, currentLocationY));
                    world.setMetadata("SpawnLocationZ", new FixedMetadataValue(plugin, currentLocationZ));
                    world.setMetadata("SpawnLocationYaw", new FixedMetadataValue(plugin, currentLocationYaw));
                    world.setMetadata("SpawnLocationSet", new FixedMetadataValue(plugin, true));
                    config.set("Spawn.Location", new Vector(currentLocationX, currentLocationY, currentLocationZ));
                    config.set("Spawn.Yaw", locyaw1);
                    plugin.saveConfig();
                    player.sendMessage(EasterMain.messagePrefix + "スポーン地点を\nX:" + currentLocationX + "\nY:" + currentLocationY + "\nZ:" + currentLocationZ + "\nに設定しました");
                    return true;
                }

                // spawn

                else if (args[1].equals("field")) {
                    if (args.length != 2) {
                        player.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
                        return false;
                    }
                    if (!player.hasMetadata("PreviousLocationX")) {
                        player.sendMessage(EasterMain.messagePrefix + "範囲が選択されていません");
                        return false;
                    }

                    world.setMetadata("FieldLocationAX", new FixedMetadataValue(plugin, currentLocationX));
                    world.setMetadata("FieldLocationAY", new FixedMetadataValue(plugin, currentLocationY));
                    world.setMetadata("FieldLocationAZ", new FixedMetadataValue(plugin, currentLocationZ));
                    world.setMetadata("FieldLocationASet", new FixedMetadataValue(plugin, true));
                    config.set("Field.LocationA", new Vector(currentLocationX, currentLocationY, currentLocationZ));

                    double previousLocationX = player.getMetadata("PreviousLocationX").get(0).asDouble();
                    double previousLocationY = player.getMetadata("PreviousLocationY").get(0).asDouble();
                    double previousLocationZ = player.getMetadata("PreviousLocationZ").get(0).asDouble();

                    world.setMetadata("FieldLocationBX", new FixedMetadataValue(plugin, previousLocationX));
                    world.setMetadata("FieldLocationBY", new FixedMetadataValue(plugin, previousLocationY));
                    world.setMetadata("FieldLocationBZ", new FixedMetadataValue(plugin, previousLocationZ));
                    world.setMetadata("FieldLocationBSet", new FixedMetadataValue(plugin, true));
                    config.set("Field.LocationB", new Vector(previousLocationX, previousLocationY, previousLocationZ));
                    plugin.saveConfig();
                    player.sendMessage(EasterMain.messagePrefix + "卵の生成範囲を\nX:" + currentLocationX + "~" + previousLocationX + "\nY:" + currentLocationY + "~" + previousLocationY + "\nZ:" + currentLocationZ + "~" + previousLocationZ + "\nに設定しました");
                    return true;
                }

            } else {
                sender.sendMessage(EasterMain.messagePrefix + "このコマンドはプレイヤー専用です");
                return false;
            }
        } else if (args[0].equals("start")) {

            if (args.length != 1) {
                sender.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
                return false;
            }

            if (world.hasMetadata("ingame")) {
                sender.sendMessage(EasterMain.messagePrefix + "ゲーム中にはこのコマンドは実行できません");
                return false;
            }

            //check config spawn point
            if (!world.hasMetadata("SpawnLocationSet")) {
                sender.sendMessage(EasterMain.messagePrefix + "スポーン地点が設定されていません");
                return false;
            }

            EasterBoardManager boardManager = new EasterBoardManager(plugin);
            boardManager.resetScore();

            EasterBoardManager.showScore();
            world.setMetadata("ingame", new FixedMetadataValue(plugin, true));

            ItemStack[] sb = new ItemStack[36];

            double spawnLocationX = world.getMetadata("SpawnLocationX").get(0).asDouble();
            double spawnLocationY = world.getMetadata("SpawnLocationY").get(0).asDouble();
            double spawnLocationZ = world.getMetadata("SpawnLocationZ").get(0).asDouble();
            float spawnLocationYaw = world.getMetadata("SpawnLocationYaw").get(0).asFloat();
            Location spawn = new Location(world, spawnLocationX, spawnLocationY + 1, spawnLocationZ, spawnLocationYaw, 0);

            world.setMetadata("gameStart", new FixedMetadataValue(plugin, new Date().getTime()));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(EasterMain.messagePrefix + "ゲームスタート！");
                player.getInventory().setContents(sb);
                player.teleport(spawn);
            }
            gameTimeCount = new EasterGameCountdown(plugin).runTaskTimer(plugin, 0, 20);
            int gameTime = 20 * (60 * config.getInt("Game.GameTime"));
            gameEnd = new EasterRunnableFinish(plugin).runTaskLater(this.plugin, gameTime);
            return true;

        } else if (args[0].equals("stop")) {

            sender.sendMessage("stop");

            if (world.hasMetadata("ingame") && gameEnd != null) {
                Bukkit.getServer().getScheduler().cancelTask(gameEnd.getTaskId());
                Bukkit.getServer().getScheduler().cancelTask(gameTimeCount.getTaskId());
                gameEnd = new EasterRunnableFinish(plugin).runTask(plugin);
                return true;
            } else {
                sender.sendMessage(EasterMain.messagePrefix + "ゲームが進行中ではありません");
                return false;
            }

        } else if (args[0].equals("eggs")) {
            if (args.length != 2) {
                sender.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
                return false;
            }

            if (args[1] == null) {
                sender.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
                return false;
            }

            if (world.hasMetadata("ingame")) {
                sender.sendMessage(EasterMain.messagePrefix + "ゲーム中にはこのコマンドは実行できません");
                return false;
            }

            if (!args[1].equals("clear") && !args[1].equals("set")) {

                sender.sendMessage(EasterMain.messagePrefix + "不明なコマンド");
                return false;

            } else {
                if (!world.hasMetadata("FieldLocationAX") || !world.hasMetadata("FieldLocationBX")) {
                    sender.sendMessage(EasterMain.messagePrefix + "範囲が設定されていません");
                    return false;
                }

                Location fieldLocationA = new Location(world, world.getMetadata("FieldLocationAX").get(0).asInt(), world.getMetadata("FieldLocationAY").get(0).asInt(), world.getMetadata("FieldLocationAZ").get(0).asInt());
                Location fieldLocationB = new Location(world, world.getMetadata("FieldLocationBX").get(0).asInt(), world.getMetadata("FieldLocationBY").get(0).asInt(), world.getMetadata("FieldLocationBZ").get(0).asInt());
                double temp;

                if (fieldLocationA.getX() > fieldLocationB.getX()) {
                    temp = fieldLocationA.getX();
                    fieldLocationA.setX(fieldLocationB.getX());
                    fieldLocationB.setX(temp);
                }

                if (fieldLocationA.getY() > fieldLocationB.getY()) {
                    temp = fieldLocationA.getY();
                    fieldLocationA.setY(fieldLocationB.getY());
                    fieldLocationB.setY(temp);
                }

                if (fieldLocationA.getZ() > fieldLocationB.getZ()) {
                    temp = fieldLocationA.getZ();
                    fieldLocationA.setZ(fieldLocationB.getZ());
                    fieldLocationB.setZ(temp);
                }

                switch (args[1]) {
                    case "clear":

                        for (int Y = fieldLocationA.getBlockY() + 1; Y <= fieldLocationB.getBlockY() + 1; Y++) {
                            for (int Z = fieldLocationA.getBlockZ(); Z <= fieldLocationB.getBlockZ(); Z++) {
                                for (int X = fieldLocationA.getBlockX(); X <= fieldLocationB.getBlockX(); X++) {
                                    Block block = world.getBlockAt(X, Y, Z);
                                    if (block.getType() == Material.DRAGON_EGG) {
                                        block.setType(Material.AIR);
                                    }
                                }
                            }
                        }

                        sender.sendMessage(EasterMain.messagePrefix + "卵を消去しました");

                        return true;


                    case "set":

                        int all = plugin.getConfig().getInt("Possibility");

                        for (int Y = fieldLocationA.getBlockY() + 1; Y <= fieldLocationB.getBlockY() + 1; Y++) {
                            for (int Z = fieldLocationA.getBlockZ(); Z <= fieldLocationB.getBlockZ(); Z++) {
                                for (int X = fieldLocationA.getBlockX(); X <= fieldLocationB.getBlockX(); X++) {
                                    Block block = world.getBlockAt(X, Y, Z);
                                    Block block_ = world.getBlockAt(X, Y - 1, Z);
                                    if (isValidAirBlock(block) && isValidGroundBlock(block_)) {
                                        double possibility = Math.random() * all;
                                        if (possibility > all - 1) {
                                            block.setType(Material.DRAGON_EGG);
                                        }
                                    }
                                }
                            }
                        }

                        sender.sendMessage(EasterMain.messagePrefix + "卵を生成しました");

                        return true;

                    default:

                        sender.sendMessage(EasterMain.messagePrefix + ChatColor.RED + "不明なコマンド");
                        return false;

                }
            }

        } else if (args[0].equals("option")) {

            if (args.length != 3) {
                sender.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
                return false;
            }

            if (args[2] == null) {
                sender.sendMessage(EasterMain.messagePrefix + "パラメータエラー");
                return false;
            }
            switch (args[1]) {
                case "inLava":
                    if (args[2].equalsIgnoreCase("true")) {
                        sender.sendMessage(EasterMain.messagePrefix + "溶岩内への生成を" + true + "に設定しました");
                        plugin.getConfig().set("Block.inLava", true);
                    } else if (args[2].equalsIgnoreCase("false")) {
                        sender.sendMessage(EasterMain.messagePrefix + "溶岩内への生成を" + false + "に設定しました");
                        plugin.getConfig().set("Block.inLava", false);
                    } else {
                        sender.sendMessage(EasterMain.messagePrefix + "<" + args[2] + ">はtrueかfalseでなければなりません");
                        return false;
                    }
                    break;
                case "inWater":

                    if (args[2].equalsIgnoreCase("true")) {

                        sender.sendMessage(EasterMain.messagePrefix + "水中への生成を" + true + "に設定しました");
                        plugin.getConfig().set("Block.inWater", true);

                    } else if (args[2].equalsIgnoreCase("false")) {

                        sender.sendMessage(EasterMain.messagePrefix + "水中への生成を" + false + "に設定しました");
                        plugin.getConfig().set("Block.inWater", false);

                    } else {

                        sender.sendMessage(EasterMain.messagePrefix + "<" + args[2] + ">はtrueかfalseでなければなりません");
                        return false;

                    }

                    break;
                case "onSnow":

                    if (args[2].equalsIgnoreCase("true")) {

                        sender.sendMessage(EasterMain.messagePrefix + "雪の上への生成" + true + "に設定しました");
                        plugin.getConfig().set("Block.inLava", true);

                    } else if (args[2].equalsIgnoreCase("false")) {

                        sender.sendMessage(EasterMain.messagePrefix + "雪の上への生成を" + false + "に設定しました");
                        plugin.getConfig().set("Block.inLava", false);

                    } else {

                        sender.sendMessage(EasterMain.messagePrefix + "<" + args[2] + ">はtrueかfalseでなければなりません");
                        return false;

                    }
                    break;
                case "eggsPossibility":

                    // /easter option eggsPossibility

                    if (!isInteger(args[2])) {
                        sender.sendMessage(EasterMain.messagePrefix + "<" + args[2] + ">は整数でなければなりません");
                        return false;
                    }

                    sender.sendMessage(EasterMain.messagePrefix + "卵の生成確率を1/" + args[2] + "に設定しました");
                    plugin.getConfig().set("Possibility", Integer.parseInt(args[2]));

                    break;
                default:

                    sender.sendMessage(EasterMain.messagePrefix + "不明なコマンド");
                    return false;

            }
            plugin.saveConfig();
            return true;
        }
        return false;
    }

    public boolean isValidAirBlock(Block block) {
        Material m = block.getType();
        if (m == Material.AIR) {
            return true;
        }
        if (m == Material.LONG_GRASS) {
            return true;
        }
        if (plugin.getConfig().getBoolean("Block.onSnow")) {
            if (m == Material.SNOW) {
                return true;
            }
        }
        if (plugin.getConfig().getBoolean("Block.inLava")) {
            if (m == Material.STATIONARY_LAVA) {
                return true;
            }
        }
        if (plugin.getConfig().getBoolean("Block.inWater")) {
            if (m == Material.STATIONARY_WATER) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidGroundBlock(Block block) {
        Material m = block.getType();
        if (m == Material.AIR) {
            return false;
        }
        if (m == Material.STATIONARY_WATER) {
            return false;
        }
        if (m == Material.WATER) {
            return false;
        }
        if (m == Material.STATIONARY_LAVA) {
            return false;
        }
        if (m == Material.LAVA) {
            return false;
        }
        if (m == Material.LONG_GRASS) {
            return false;
        }
        if (m == Material.YELLOW_FLOWER) {
            return false;
        }
        if (m == Material.RED_ROSE) {
            return false;
        }
        if (m == Material.SNOW) {
            return false;
        }
        if (m == Material.LEAVES) {
            return false;
        }
        if (m == Material.CROPS) {
            return false;
        }
        if (m == Material.POTATO) {
            return false;
        }
        if (m == Material.CARROT) {
            return false;
        }
        if (m == Material.PUMPKIN_STEM) {
            return false;
        }
        if (m == Material.MELON_STEM) {
            return false;
        }
        if (m == Material.DRAGON_EGG) {
            return false;
        }
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<String>();
        if (command.getName().equalsIgnoreCase("egghunt")) {

            // help

            if ("help".startsWith(args[0])) {
                if (args[0].equalsIgnoreCase("help")) {

                    // last argument

                } else {
                    tab.add("help");
                }
            }

            // set

            if ("set".startsWith(args[0])) {
                if (args[0].equalsIgnoreCase("set")) {

                    // field

                    if ("field".startsWith(args[1])) {
                        if (args[1].equalsIgnoreCase("field")) {

                            // last argument

                        } else {
                            tab.add("field");
                        }
                    }

                    // spawn

                    if ("spawn".startsWith(args[1])) {
                        if (args[1].equalsIgnoreCase("spawn")) {

                            // last argument

                        } else {
                            tab.add("spawn");
                        }
                    }

                } else {
                    tab.add("set");
                }
            }


            // start

            if ("start".startsWith(args[0])) {
                if (args[0].equalsIgnoreCase("start")) {

                    // last argument

                } else {
                    tab.add("start");
                }
            }

            // stop

            if ("stop".startsWith(args[0])) {
                if (args[0].equalsIgnoreCase("stop")) {

                    // last argument

                } else {
                    tab.add("stop");
                }
            }

            // eggs

            if ("eggs".startsWith(args[0])) {
                if (args[0].equalsIgnoreCase("eggs")) {

                    // set

                    if ("set".startsWith(args[1])) {
                        if (args[1].equalsIgnoreCase("set")) {

                            // last argument

                        } else {
                            tab.add("set");
                        }
                    }

                    // clear

                    if ("clear".startsWith(args[1])) {
                        if (args[1].equalsIgnoreCase("clear")) {

                            // last argument

                        } else {
                            tab.add("clear");
                        }
                    }


                } else {
                    tab.add("eggs");
                }
            }

            // option

            if ("option".startsWith(args[0])) {
                if (args[0].equalsIgnoreCase("option")) {

                    // onSnow

                    if ("onSnow".startsWith(args[1])) {
                        if (args[1].equalsIgnoreCase("onSnow")) {

                            // true

                            if ("true".startsWith(args[2])) {
                                if (args[2].equalsIgnoreCase("true")) {

                                    // last argument

                                } else {
                                    tab.add("true");
                                }
                            }

                            // false

                            if ("false".startsWith(args[2])) {
                                if (args[2].equalsIgnoreCase("false")) {

                                    // last argument

                                } else {
                                    tab.add("false");
                                }
                            }
                        } else {
                            tab.add("onSnow");
                        }
                    }

                    // inLava

                    if ("inLava".startsWith(args[1])) {
                        if (args[1].equalsIgnoreCase("inLava")) {

                            // true

                            if ("true".startsWith(args[2])) {
                                if (args[2].equalsIgnoreCase("true")) {

                                    // last argument

                                } else {
                                    tab.add("true");
                                }
                            }

                            // false

                            if ("false".startsWith(args[2])) {
                                if (args[2].equalsIgnoreCase("false")) {

                                    // last argument

                                } else {
                                    tab.add("false");
                                }
                            }
                        } else {
                            tab.add("inLava");
                        }
                    }

                    // inWater

                    if ("inWater".startsWith(args[1])) {
                        if (args[1].equalsIgnoreCase("inWater")) {

                            // true

                            if ("true".startsWith(args[2])) {
                                if (args[2].equalsIgnoreCase("true")) {

                                    // last argument

                                } else {
                                    tab.add("true");
                                }
                            }

                            // false

                            if ("false".startsWith(args[2])) {
                                if (args[2].equalsIgnoreCase("false")) {

                                    // last argument

                                } else {
                                    tab.add("false");
                                }
                            }
                        } else {
                            tab.add("inWater");
                        }
                    }

                    // eggsPossibility

                    if ("eggsPossibility".startsWith(args[1])) {
                        if (args[1].equalsIgnoreCase("eggsPossibility")) {

                            // last argument

                        } else {
                            tab.add("eggsPossibility");
                        }
                    }

                } else {
                    tab.add("option");
                }
            }

            return tab;
        }
        return null;
    }

}
