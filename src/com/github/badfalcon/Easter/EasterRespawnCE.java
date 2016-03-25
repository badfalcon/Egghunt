package com.github.badfalcon.Easter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by falcon on 2016/03/23.
 */
public class EasterRespawnCE implements CommandExecutor, TabCompleter {

    EasterMain plugin;

    public EasterRespawnCE(EasterMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            World world = Bukkit.getServer().getWorlds().get(0);

            if (world.hasMetadata("ingame")) {
                double spawnLocationX = world.getMetadata("SpawnLocationX").get(0).asDouble();
                double spawnLocationY = world.getMetadata("SpawnLocationY").get(0).asDouble();
                double spawnLocationZ = world.getMetadata("SpawnLocationZ").get(0).asDouble();
                float spawnLocationYaw = world.getMetadata("SpawnLocationYaw").get(0).asFloat();
                Location spawn = new Location(world, spawnLocationX, spawnLocationY + 1, spawnLocationZ, spawnLocationYaw, 0);

                Player player = (Player) sender;
                player.teleport(spawn);
            } else {
                sender.sendMessage(EasterMain.messagePrefix +"このコマンドはゲーム進行中のみ利用可能です");
            }
            return true;
        } else {
            sender.sendMessage(EasterMain.messagePrefix + "このコマンドはプレイヤー専用です");
            return false;
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<String>();
        if (command.getName().equalsIgnoreCase("r")) {

        }
        return null;

    }
}
