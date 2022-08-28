package net.eratiem.serveraccessmanager.bungee

import net.eratiem.serveraccessmanager.tools.PermissionChecker
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler

class ServerAccessManagerPlugin(
    override val luckPerms: LuckPerms = LuckPermsProvider.get()
) : Plugin(), PermissionChecker {
    private val serverAccessListener: Listener = ServerAccessListener()

    override fun onEnable() {
        proxy.pluginManager.registerListener(this, serverAccessListener)
    }

    override fun onDisable() {
        proxy.pluginManager.unregisterListener(serverAccessListener)
    }

    private class ServerAccessListener(
        override val luckPerms: LuckPerms = LuckPermsProvider.get()
    ) : Listener, PermissionChecker {
        @EventHandler
        fun onServerConnect(event: ServerConnectEvent) {
            val player: ProxiedPlayer = event.player

            if (!isUserAllowedToConnectToServer(player.uniqueId, event.target.name))
                event.isCancelled = true
        }
    }
}