package net.eratiem.serveraccessmanager.tools

import net.luckperms.api.LuckPerms
import java.util.*

interface PermissionChecker {
    val luckPerms: LuckPerms

    fun isUserAllowedToConnectToServer(uuid: UUID, serverName: String): Boolean {
        var isAllowed = false

        luckPerms.userManager.loadUser(uuid).thenAcceptAsync { user ->
            isAllowed = user.cachedData.permissionData.checkPermission("sam.${serverName.lowercase()}").asBoolean()
        }

        return isAllowed
    }
}