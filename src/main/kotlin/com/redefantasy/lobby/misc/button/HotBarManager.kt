package com.redefantasy.lobby.misc.button

import org.bukkit.Material
import org.bukkit.entity.Player
import org.greenrobot.eventbus.EventBus

/**
 * @author Gutyerrez
 */
object HotBarManager {

    private val BUTTONS = mutableListOf<HotBarButton>()
    private val BUS = mutableMapOf<HotBarButton, EventBus>()

    fun registerHotBarButton(
        vararg buttons: HotBarButton
    ) {
        buttons.forEach {
            val bus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .throwSubscriberException(true)
                .build()

            bus.register(it)

            BUTTONS.add(it)
            BUS[it] = bus
        }
    }

    fun giveToPlayer(player: Player) {
        player.inventory.clear()

        player.inventory.heldItemSlot = 4

        player.inventory.armorContents.forEach { it.type = Material.AIR }

        this.BUTTONS.forEach {
            player.inventory.setItem(
                it.slot,
                it.icon
            )
        }
    }

}