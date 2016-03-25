package com.github.badfalcon.Easter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

public class EasterListener implements Listener {

    EasterMain plugin;
    World world;

    public EasterListener(EasterMain plugin) {
        this.plugin = plugin;
        world = Bukkit.getServer().getWorlds().get(0);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            new PlayerJoinEvent(player, null);
        }
        checkConfig(plugin.getConfig());
    }

    public void checkConfig(FileConfiguration config) {
        if (config.contains("Spawn.Location")) {
            Vector respawn = config.getVector("Spawn.Location");
            float yaw = config.getFloatList("Spawn.Yaw").get(0);
            world.setMetadata("SpawnLocationX", new FixedMetadataValue(plugin, respawn.getX()));
            world.setMetadata("SpawnLocationY", new FixedMetadataValue(plugin, respawn.getY()));
            world.setMetadata("SpawnLocationZ", new FixedMetadataValue(plugin, respawn.getZ()));
            world.setMetadata("SpawnLocationYaw", new FixedMetadataValue(plugin, yaw));
            world.setMetadata("SpawnLocationSet", new FixedMetadataValue(plugin, true));
            Bukkit.getLogger().info(EasterMain.messagePrefix + "準備完了");
        } else {
            Bukkit.getLogger().info(EasterMain.messagePrefix + "ゲームを開始するにはスポーン地点を設定してください");
        }
        if (config.contains("Field.LocationA") && config.contains("Field.LocationB")) {
            Vector locationA = config.getVector("Field.LocationA");
            world.setMetadata("FieldLocationAX", new FixedMetadataValue(plugin, locationA.getX()));
            world.setMetadata("FieldLocationAY", new FixedMetadataValue(plugin, locationA.getY()));
            world.setMetadata("FieldLocationAZ", new FixedMetadataValue(plugin, locationA.getZ()));
            world.setMetadata("FieldLocationASet", new FixedMetadataValue(plugin, true));

            Vector locationB = config.getVector("Field.LocationB");
            world.setMetadata("FieldLocationBX", new FixedMetadataValue(plugin, locationB.getX()));
            world.setMetadata("FieldLocationBY", new FixedMetadataValue(plugin, locationB.getY()));
            world.setMetadata("FieldLocationBZ", new FixedMetadataValue(plugin, locationB.getZ()));
            world.setMetadata("FieldLocationBSet", new FixedMetadataValue(plugin, true));
        }

    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        World world = Bukkit.getWorlds().get(0);
        if (world.hasMetadata("ingame")) {
            world.removeMetadata("ingame", plugin);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setScoreboard(EasterMain.board);
        if (world.hasMetadata("gameStart")) {
            if (player.getLastPlayed() < world.getMetadata("gameStart").get(0).asLong()) {
                player.sendMessage("played before");
                EasterMain.board.getObjective("score").getScore(player).setScore(0);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removeMetadata("CurrentLocationX", plugin);
        player.removeMetadata("CurrentLocationY", plugin);
        player.removeMetadata("CurrentLocationZ", plugin);
        player.removeMetadata("CurrentLocationYaw", plugin);
        player.removeMetadata("CurrentLocationSet", plugin);
        player.removeMetadata("PreviousLocationX", plugin);
        player.removeMetadata("PreviousLocationY", plugin);
        player.removeMetadata("PreviousLocationZ", plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (player.getGameMode() == GameMode.SURVIVAL) {

            player.sendMessage(action.name());

            if (action == Action.LEFT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                Material blockMaterial = block.getType();

                if (world.hasMetadata("ingame")) {
                    if (blockMaterial == Material.DRAGON_EGG) {
                        event.setCancelled(true);
                        block.breakNaturally();
                    }
                }
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                Material blockMaterial = block.getType();
                if (blockMaterial == Material.WOOD_BUTTON || blockMaterial == Material.STONE_BUTTON|| blockMaterial == Material.WOODEN_DOOR|| blockMaterial == Material.TRAP_DOOR||blockMaterial == Material.FENCE_GATE|| blockMaterial == Material.TRAP_DOOR) {
                } else {
                    if (world.hasMetadata("ingame")) {
                        if (blockMaterial == Material.DRAGON_EGG) {
                            if (isPlaying(player)) {
                                event.setCancelled(true);
                                block.breakNaturally();
                            } else {
                                event.setCancelled(true);
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    } else {
                        event.setCancelled(true);
                    }
                }
            } else if (action == Action.PHYSICAL) {
                Material blockMaterial = event.getClickedBlock().getType();
                if (blockMaterial == Material.STONE_PLATE || blockMaterial == Material.WOOD_PLATE) {

                } else {
                    event.setCancelled(true);
                }
            }
        } else if (player.getGameMode() == GameMode.CREATIVE) {
            if (action == Action.LEFT_CLICK_BLOCK) {
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                Material materialInHand = event.getMaterial();
                if (materialInHand == Material.GHAST_TEAR) {
                    Location loc = block.getLocation();
                    if (loc != null) {
                        if (player.hasMetadata("CurrentLocationSet")) {
                            player.setMetadata("PreviousLocationX", new FixedMetadataValue(plugin, player.getMetadata("CurrentLocationX").get(0).asDouble()));
                            player.setMetadata("PreviousLocationY", new FixedMetadataValue(plugin, player.getMetadata("CurrentLocationY").get(0).asDouble()));
                            player.setMetadata("PreviousLocationZ", new FixedMetadataValue(plugin, player.getMetadata("CurrentLocationZ").get(0).asDouble()));
                        }
                        player.setMetadata("CurrentLocationX", new FixedMetadataValue(plugin, loc.getBlockX()));
                        player.setMetadata("CurrentLocationY", new FixedMetadataValue(plugin, loc.getBlockY()));
                        player.setMetadata("CurrentLocationZ", new FixedMetadataValue(plugin, loc.getBlockZ()));
                        float yaw = convertYaw(player.getLocation().getYaw());
                        player.setMetadata("CurrentLocationYaw", new FixedMetadataValue(plugin, yaw));
                        player.setMetadata("CurrentLocationSet", new FixedMetadataValue(plugin, true));
                        player.sendMessage(EasterMain.messagePrefix + "x:" + loc.getBlockX() + "  y:" + loc.getBlockY() + "  z:" + loc.getBlockZ()+" を選択しました");
                    }
                } else if (materialInHand == Material.FEATHER) {
                    player.sendMessage(block.getType().name());
                }
            } else if (action == Action.PHYSICAL) {
                Material blockMaterial = event.getClickedBlock().getType();
                if (blockMaterial == Material.STONE_PLATE || blockMaterial == Material.WOOD_PLATE) {

                }
            }
        }
    }

    public boolean isPlaying(Player player) {
        World world = Bukkit.getWorlds().get(0);
        if (player.getGameMode() == GameMode.SURVIVAL) {
            if (world.hasMetadata("ingame")) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isPlaying(event.getPlayer())) {
            if (event.getItemDrop().getItemStack().getType() == Material.DRAGON_EGG) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (isPlaying(player)) {
            if (event.getItem().getItemStack().getType() == Material.DRAGON_EGG) {
                int amount = event.getItem().getItemStack().getAmount();
                player.giveExpLevels(amount);
                Score personal = player.getScoreboard().getObjective("score").getScore(player);
                int newScore = personal.getScore() + amount;
                personal.setScore(newScore);
                EasterMain.board.getObjective("time").getScore(player).setScore(newScore);
            }
        }
    }

    public float convertYaw(float yaw) {
        if (-135 < yaw && yaw <= -45) {
            return -90;
        } else if (-45 < yaw && yaw <= 45) {
            return 0;
        } else if (45 < yaw && yaw <= 135) {
            return 90;
        } else {
            return 180;
        }
    }
}


