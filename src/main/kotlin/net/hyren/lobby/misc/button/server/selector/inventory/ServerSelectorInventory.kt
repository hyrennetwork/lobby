package net.hyren.lobby.misc.button.server.selector.inventory

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.applications.ApplicationType
import net.hyren.core.spigot.CoreSpigotProvider
import net.hyren.core.spigot.inventory.CustomInventory
import net.hyren.lobby.misc.utils.ServerConnectorUtils
import org.bukkit.entity.Player

/**
 * @author Gutyerrez
 */
class ServerSelectorInventory : CustomInventory(
    "Selecione o servidor",
    3 * 9
) {

    private val SLOTS = arrayOf(
        arrayOf(13),
        arrayOf(11, 15),
        arrayOf(10, 13, 16)
    )

    init {
        val servers = CoreProvider.Cache.Local.SERVERS.provide().fetchAll().filter {
            CoreSpigotProvider.Cache.Local.SERVER_CONFIGURATION.provide().fetchByServer(it) !== null
        }.filter {
            CoreProvider.Cache.Local.APPLICATIONS.provide().fetchByServerAndApplicationType(
                it,
                ApplicationType.SERVER_SPAWN
            ) != null
        }.toSet()

        val slots = SLOTS[
                if (servers.size >= SLOTS.size) {
                    SLOTS.lastIndex
                } else {
                    servers.size - 1
                }
        ]

        servers.forEachIndexed { index, server ->
            val slot = slots[index]

            setItem(
                slot,
                CoreSpigotProvider.Cache.Local.SERVER_CONFIGURATION.provide().fetchByServer(
                    server
                )?.icon
            ) { it ->
                val player = it.whoClicked as Player

                ServerConnectorUtils.connect(
                    player,
                    server
                )
            }
        }
    }

}