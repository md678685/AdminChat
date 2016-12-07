/*
 * Copyright (C) 2013 Spencer Alderman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogue.adminchat;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.rogue.adminchat.channel.Channel;
import com.rogue.adminchat.channel.ChannelManager;
import com.rogue.adminchat.channel.ChannelNotFoundException;
import com.rogue.adminchat.channel.SenderMutedException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author 1Rogue
 * @author MD678685
 * @version 1.5.0
 * @since 1.2.0
 */
public class AdminListener implements Listener {

    private final AdminChat plugin;

    public AdminListener(AdminChat plugin) {
        this.plugin = plugin;
    }

    /**
     * Makes players who have toggled adminchat send chat to the appropriate
     * channels
     *
     * @param event AsyncPlayerChatEvent instance
     * @version 1.5.0
     * @since 1.2.0
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        final Map<String, String> toggled;
        final Player player = event.getPlayer();
        String chan = player.getMetadata("adminchat-toggled").get(0).asString();
        try {
            event.setCancelled(true);
            this.plugin.getChannelManager().getChannel(chan).sendMessage(event.getPlayer(), event.getMessage());
        } catch (ChannelNotFoundException e) {
            this.plugin.communicate(event.getPlayer(), "Could not find the channel you were toggled in! This should not happen!");
            this.plugin.getLogger().log(Level.SEVERE, "Could not find the channel " + player.getName() + " is toggled in! This should not happen!", e);
        } catch (SenderMutedException e) {
            this.plugin.communicate(event.getPlayer(), "You are muted in this channel!");
            this.plugin.getLogger().info(player.getName() + " is muted in " + chan + " - message: " + event.getMessage());
        }
    }

    /**
     * Sends a notification to ops/players with all of the plugin's permissions
     *
     * @param e The join event
     * @version 1.5.0
     * @since 1.2.0
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("adminchat.updatenotice")) {
            if (plugin.isOutOfDate()) {
                plugin.communicate(e.getPlayer(), "An update is available for Adminchat!");
            }
        }
        Map<String, Channel> channels = this.plugin.getChannelManager().getChannels();
        for (String channelName : channels.keySet()) {
            if (e.getPlayer().hasPermission("adminchat.channel." + channelName + ".autojoin")) {
                channels.get(channelName).addMember(e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Map<String, Channel> channels = this.plugin.getChannelManager().getChannels();
        for (String channelName : channels.keySet()) {
            Channel channel = channels.get(channelName);
            if (channel.getMembers().contains(e.getPlayer())) {
                channel.removeMember(e.getPlayer());
            }
            if (channel.isMuted(e.getPlayer())) {
                channel.unmuteSender(e.getPlayer());
            }
        }
    }
}