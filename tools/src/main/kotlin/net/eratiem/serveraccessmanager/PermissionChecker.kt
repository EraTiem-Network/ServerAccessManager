package net.eratiem.serveraccessmanager

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import java.util.UUID

val luckPerms: LuckPerms
  get() = LuckPermsProvider.get()

fun isUserAllowedToConnectToServer(uuid: UUID, serverName: String): Boolean {
  luckPerms.userManager.loadUser(uuid).join().let { user ->
    return user.cachedData.permissionData.checkPermission("sam.${serverName.lowercase()}").asBoolean()
  }
}