package com.eo.badwordxeo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BadWordsPlugin extends JavaPlugin implements Listener {

    private List<String> badWords;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadBadWordsList();
        getServer().getPluginManager().registerEvents(this, this); // Daftarkan listener
        getLogger().info("Plugin BadWordsPlugin telah di-enable!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin BadWordsPlugin telah dinonaktifkan!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("setbadwords") && sender instanceof Player) {
            if (sender.hasPermission("badwords.set")) {
                if (args.length == 0) {
                    sender.sendMessage("Usage: /setbadwords <badword1> <badword2> ...");
                    return true;
                }

                List<String> newBadWords = new ArrayList<>();
                for (String arg : args) {
                    newBadWords.add(arg.toLowerCase());
                }
                getConfig().set("badwords", newBadWords);
                saveConfig();
                loadBadWordsList();
                sender.sendMessage("Daftar kata-kata yang dilarang berhasil diperbarui.");
                return true;
            } else {
                sender.sendMessage("You don't have permission to use this command.");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String[] words = message.split(" ");

        for (String word : words) {
            if (isBadWord(word.toLowerCase())) {
                event.setCancelled(true); //
                player.sendMessage("Pesanmu mengandung kata-kata yang tidak diperbolehkan.");
                getLogger().warning(player.getName() + " mencoba mengirim pesan dengan kata-kata yang dilarang.");
                return;
            }
        }
    }

    private void loadBadWordsList() {
        badWords = getConfig().getStringList("badwords");
    }

    private boolean isBadWord(String word) {
        return badWords.contains(word);
    }
}