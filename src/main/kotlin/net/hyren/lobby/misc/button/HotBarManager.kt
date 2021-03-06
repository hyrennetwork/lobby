package net.hyren.lobby.misc.button

import net.hyren.core.shared.CoreProvider
import net.hyren.core.shared.misc.preferences.PLAYER_VISIBILITY
import net.hyren.core.shared.misc.preferences.PreferenceState
import net.hyren.lobby.misc.button.player.visibility.button.PlayerVisibilityOffHotBarButton
import net.hyren.lobby.misc.button.player.visibility.button.PlayerVisibilityOnHotBarButton
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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
                .logSubscriberExceptions(true)
                .throwSubscriberException(false)
                .build()

            bus.register(it)

            BUTTONS.add(it)
            BUS[it] = bus
        }
    }

    fun getHotBarButton(itemStack: ItemStack?) = this.BUTTONS.stream()
        .filter { it.icon.isSimilar(itemStack) }
        .findFirst()
        .orElse(null)

    fun getEventBus(hotBarButton: HotBarButton) = this.BUS[hotBarButton]

    fun giveToPlayer(player: Player) {
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)!!

        player.inventory.clear()
        player.inventory.heldItemSlot = 4
        player.inventory.armorContents = null

        this.BUTTONS.forEach { hotBarButton ->
            if (
                hotBarButton is PlayerVisibilityOnHotBarButton && user.getPreferences().find { it == PLAYER_VISIBILITY }?.preferenceState === PreferenceState.DISABLED
            ) {
                return@forEach
            } else if (
                hotBarButton is PlayerVisibilityOffHotBarButton && user.getPreferences().find { it == PLAYER_VISIBILITY }?.preferenceState === PreferenceState.ENABLED
            ) {
                return@forEach
            }

            player.inventory.setItem(
                hotBarButton.slot,
                hotBarButton.icon
            )
        }
    }

    fun giveHotBarButtonToPlayer(
        hotBarButton: HotBarButton,
        player: Player
    ) {
        player.inventory.setItem(
            hotBarButton.slot,
            hotBarButton.icon
        )
    }

}