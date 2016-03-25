package com.github.badfalcon.Easter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class EasterMain extends JavaPlugin {

    static String messagePrefix = "[egghunt]";

    private EasterCommandExecutor easterExecutor;
    private EasterRespawnCE easterRespawnCE;

    static Scoreboard board;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();

        new EasterBoardManager(this).setScoreboard();
        easterExecutor = new EasterCommandExecutor(this);
        getCommand("egghunt").setExecutor(easterExecutor);
        getCommand("egghunt").setTabCompleter(easterExecutor);
        easterRespawnCE = new EasterRespawnCE(this);
        getCommand("r").setExecutor(easterRespawnCE);
        getCommand("r").setTabCompleter(easterRespawnCE);

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new EasterListener(this), this);
    }

}
