package net.hyren.lobby.misc.button.lobby.selector.button

import net.hyren.core.shared.CoreProvider
import net.hyren.core.spigot.misc.utils.ItemBuilder
import net.hyren.lobby.misc.button.HotBarButton
import net.hyren.lobby.misc.button.lobby.selector.inventory.LobbySelectorInventory
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.greenrobot.eventbus.Subscribe

/**
 * @author Gutyerrez
 */
class LobbySelectorHotBarButton : HotBarButton(
    ItemBuilder(Material.NETHER_STAR)
        .name("§aSelecionar saguão")
        .lore(
            arrayOf(
                "§7Clique para escolher",
                "§7um saguão."
            )
        )
        .build(),
    6
) {

    @Subscribe
    fun on(
        event: PlayerInteractEvent
    ) {
        val player = event.player
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)!!

        player.openInventory(
            LobbySelectorInventory(user)
        )
    }

}