package com.github.badfalcon.Easter;

import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

/**
 * Created by falcon on 2016/03/21.
 */
public class EasterGameCountdown extends BukkitRunnable {
    private int maxtime;
    private int gametime;

    public EasterGameCountdown(EasterMain plugin) {
        maxtime = plugin.getConfig().getInt("Game.GameTime") * 60;
        gametime = plugin.getConfig().getInt("Game.GameTime") * 60;
    }

    public void run() {
        int gamemin = gametime / 60;
        int gamesec = gametime % 60;

        String gamesecString;
        if (gamesec < 10) {
            gamesecString = "0" + String.valueOf(gamesec);
        } else {
            gamesecString = String.valueOf(gamesec);
        }

        if (gametime == 60) {
            Bukkit.getServer().broadcastMessage(
                    EasterMain.messagePrefix + "----終了１分前----");
            EasterBoardManager.hideScore(DisplaySlot.SIDEBAR);
            EasterBoardManager.hideScore(DisplaySlot.PLAYER_LIST);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            Score time = EasterMain.board.getObjective("time").getScore(
                    Bukkit.getOfflinePlayer("Time"));
//            time.setScore(gametime);
            EasterMain.board.getObjective("time").setDisplayName("残り時間  " + gamemin + ":" + gamesecString);

            BarAPI.setMessage(player, "残り時間  " + gamemin + ":" + gamesecString);
            BarAPI.setHealth(player, (float) gametime / (float) maxtime * 100F);
            if (gametime <= 10) {
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            }
            if (gametime == 60) {
                player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1,
                        1);
            }
        }
        if (gametime > 0) {
            gametime--;
        } else {
            cancel();
        }
    }

}
