package net.eratiem.serveraccessmanager

import net.eratiem.eralogger.addToEraLogger
import net.md_5.bungee.api.ChatColor.RED
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler

class ServerAccessManagerPlugin : Plugin() {
  init {
    logger.addToEraLogger()
  }

  override fun onEnable() {
    proxy.pluginManager.registerListener(this, serverAccessListener)

    logger.info("ServerAccessManager enabled!")
  }

  override fun onDisable() {
    proxy.pluginManager.unregisterListener(serverAccessListener)

    logger.info("ServerAccessManager disabled!")
  }

  private val serverAccessListener = object : Listener {
    @EventHandler
    fun onServerConnect(event: ServerConnectEvent) = with(event) {
      if (!isUserAllowedToConnectToServer(player.uniqueId, target.name)) {
        isCancelled = true

        if (player.server != null) {
          val msg = TextComponent("[$PREFIX] ")
          msg.addExtra(TextComponent(NOT_ALLOWED_MSG).apply { color = RED })

          player.sendMessage(msg)
        }
      }
    }
  }
}