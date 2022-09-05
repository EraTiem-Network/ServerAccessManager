package net.eratiem.serveraccessmanager.tools

import net.luckperms.api.LuckPerms
import java.util.*

interface PermissionChecker {
    val luckPerms: LuckPerms

    fun isUserAllowedToConnectToServer(uuid: UUID, serverName: String): Boolean {
        luckPerms.userManager.loadUser(uuid).join().let { user ->
            return user.cachedData.permissionData.checkPermission("sam.${serverName.lowercase()}").asBoolean()
        }
    }
}