package com.github.badfalcon.Easter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

/**
 * Created by falcon on 2016/03/21.
 */
public class EasterBoardManager {

    EasterMain plugin;
    static Scoreboard board;

    public EasterBoardManager(EasterMain plugin) {
        this.plugin = plugin;
        board = EasterMain.board;
    }

    World world = Bukkit.getWorlds().get(0);

    public void setScoreboard() {
        Objective score = board.registerNewObjective("score", "dummy");
        Objective time = board.registerNewObjective("time", "dummy");

        int MaxTime = plugin.getConfig().getInt("Game.GameTime") * 60;
        int gamemin = MaxTime / 60;
        int gamesec = MaxTime % 60;

        String gamesecString;
        if (gamesec < 10) {
            gamesecString = "0" + String.valueOf(gamesec);
        } else {
            gamesecString = String.valueOf(gamesec);
        }

        time.setDisplayName("残り時間  " + gamemin + ":" + gamesecString);
        score.setDisplayName("points");
    }

    public void resetScore() {
        Objective scores = board.getObjective("score");
        for (Player player : Bukkit.getOnlinePlayers()) {
            Score score = scores.getScore(player);
            score.setScore(0);
        }
    }

    public static void showScore() {
        Objective score = board.getObjective("score");
        Objective time = board.getObjective("time");
        score.setDisplaySlot(DisplaySlot.BELOW_NAME);
        score.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        time.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public static void hideScore() {
        board.clearSlot(DisplaySlot.BELOW_NAME);
        board.clearSlot(DisplaySlot.PLAYER_LIST);
        board.clearSlot(DisplaySlot.SIDEBAR);
    }

    public static void hideScore(DisplaySlot slot) {
        board.clearSlot(slot);
    }

}
