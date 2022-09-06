package net.eratiem.serveraccessmanager.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import net.eratiem.eralogger.tools.EraLogger
import net.eratiem.serveraccessmanager.tools.PREFIX
import net.eratiem.serveraccessmanager.tools.PermissionChecker
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.slf4j.Logger
import javax.inject.Inject

@Plugin(
    id = "serveraccessmanager",
    name = "ServerAccessManager",
    version = "1.0.0",
    description = "Restricts Server-Access",
    authors = ["Motzkiste"],
    dependencies = [Dependency(id = "kotlinprovider"), Dependency(id = "eralogger"), Dependency(id = "luckperms")]
)
class ServerAccessManagerPlugin @Inject constructor(
    private val proxy: ProxyServer,
    name: String,
    logger: Logger
) {
    private lateinit var serverAccessListener: ServerAccessListener
    private val logger = EraLogger.getInstance(name, logger)

    init {
        logger.info(name)
    }

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        serverAccessListener = ServerAccessListener()
        proxy.eventManager.register(this, serverAccessListener)
        logger.info("ServerAccessManager enabled!")
    }

    @Subscribe
    fun onShutdown(event: ProxyShutdownEvent) {
        if (this::serverAccessListener.isInitialized)
            proxy.eventManager.unregisterListener(this, serverAccessListener)
    }


    private class ServerAccessListener(
        override val luckPerms: LuckPerms = LuckPermsProvider.get()
    ) : PermissionChecker {
        private val notAllowedMsg: Component =
            Component.text("You are not allowed to join this server!", NamedTextColor.RED)

        @Subscribe
        fun onServerAccess(event: ServerPreConnectEvent) {
            if (!isUserAllowedToConnectToServer(event.player.uniqueId, event.result.server.get().serverInfo.name)) {
                event.result = ServerResult.denied()

                if (!event.player.currentServer.isPresent) {
                    event.player.disconnect(notAllowedMsg)
                } else {
                    event.player.sendMessage(Component.text("[$PREFIX] ").append(notAllowedMsg))
                }
            }
        }
    }
}