package net.eratiem.serveraccessmanager

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import net.eratiem.eralogger.addToEraLogger
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED
import java.util.logging.Logger
import javax.inject.Inject

@Plugin(
  id = "serveraccessmanager",
  name = "ServerAccessManager",
  version = "1.0.0.alpha1",
  description = "Restricts Server-Access",
  authors = ["Motzkiste"],
  dependencies = [Dependency(id = "kotlinprovider"), Dependency(id = "eralogger"), Dependency(id = "luckperms")]
)
class ServerAccessManagerPlugin @Inject constructor(
  private val proxy: ProxyServer,
  private val logger: Logger
) {

  @Subscribe
  fun onInitialize(event: ProxyInitializeEvent) {
    logger.addToEraLogger()
    proxy.eventManager.register(this, ServerPreConnectEvent::class.java) { onServerAccess(it) }

    logger.info("ServerAccessManager enabled!")
  }

  @Subscribe
  fun onShutdown(event: ProxyShutdownEvent) {
    logger.info("ServerAccessManager disabled!")
  }

  private fun onServerAccess(event: ServerPreConnectEvent) = with(event) {
    if (!isUserAllowedToConnectToServer(player.uniqueId, result.server.get().serverInfo.name)) {
      result = ServerResult.denied()

      if (!player.currentServer.isPresent) {
        player.disconnect(Component.text(NOT_ALLOWED_MSG, RED))
      } else {
        player.sendMessage(Component.text("[$PREFIX] ").append(Component.text(NOT_ALLOWED_MSG, RED)))
      }
    }
  }
}