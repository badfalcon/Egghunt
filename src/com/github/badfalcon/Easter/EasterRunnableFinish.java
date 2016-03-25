package com.github.badfalcon.Easter;

import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by falcon on 2016/03/22.
 */
public class EasterRunnableFinish extends BukkitRunnable {

    private EasterMain plugin;

    public EasterRunnableFinish(EasterMain plugin) {
        this.plugin = plugin;
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
            BarAPI.setMessage(player, "残り時間  終了");

            player.setLevel(0);
            player.getInventory().clear();

        }
        EasterMain.board.getObjective("time").setDisplayName("残り時間  終了");

        World world = Bukkit.getWorlds().get(0);
        world.removeMetadata("ingame", plugin);

        double spawnLocationX = world.getMetadata("SpawnLocationX").get(0).asDouble();
        double spawnLocationY = world.getMetadata("SpawnLocationY").get(0).asDouble();
        double spawnLocationZ = world.getMetadata("SpawnLocationZ").get(0).asDouble();
        float spawnLocationYaw = world.getMetadata("SpawnLocationYaw").get(0).asFloat();
        Location spawn = new Location(world, spawnLocationX, spawnLocationY + 1, spawnLocationZ, spawnLocationYaw, 0);

        List<Integer> score = new ArrayList<Integer>();
        List<String> name = new ArrayList<String>();
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {


            //send player to spawn point
            player.teleport(spawn);

            player.sendMessage(EasterMain.messagePrefix + "ゲーム終了！");

            int pScore = player.getScoreboard().getObjective("score").getScore(player).getScore();
            player.sendMessage(EasterMain.messagePrefix + "あなたの得点：" + pScore);
            if (i == 0) {
                score.add(pScore);
                name.add(player.getName());

            } else {
                for (int j = 0; j < i; j++) {
                    if (pScore > score.get(j)) {
                        score.add(j, pScore);
                        name.add(j, player.getName());
                    }
                }
            }
            i++;
        }

        EasterBoardManager.showScore();

        for (Player player : Bukkit.getOnlinePlayers()) {

            player.sendMessage(String.valueOf(score.size()));

            if (score.size() >= 1) {
                player.sendMessage(EasterMain.messagePrefix + "1st.  " + score.get(0) + "  " + name.get(0));
            }
            if (score.size() >= 2) {
                player.sendMessage(EasterMain.messagePrefix + "2nd.  " + score.get(1) + "  " + name.get(1));
            }
            if (score.size() >= 3) {
                player.sendMessage(EasterMain.messagePrefix + "3rd.  " + score.get(2) + "  " + name.get(2));
            }
        }

    }
}
