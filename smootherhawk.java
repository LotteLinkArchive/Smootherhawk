package me.gtx;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown extends JavaPlugin implements Listener {

    private Map<UUID, Long> map = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        if(!this.getDataFolder().exists()) {
            if(!this.getDataFolder().mkdir()) throw new RuntimeException("Failed to create data folder!");
        }
        File config = new File(this.getDataFolder().getAbsolutePath() + "/config.yml");
        try {
            if(!config.exists()) {
                if(!config.createNewFile()) throw new RuntimeException("Failed to create config file!");
                this.getConfig().set("message", "Join cooldown: Please wait another 30 seconds before joining.");
                this.getConfig().set("delay", 30000);
                this.getConfig().save(config);
            }
            this.getConfig().load(config);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if(!this.map.containsKey(event.getPlayer().getUniqueId())) this.map.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(this.map.containsKey(event.getPlayer().getUniqueId())) {
            long time = this.map.get(event.getPlayer().getUniqueId());
            if(System.currentTimeMillis() - time > this.getConfig().getInt("delay")) {
                this.map.remove(event.getPlayer().getUniqueId());
            } else {
                event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("message")));
            }
        }
    }



}
