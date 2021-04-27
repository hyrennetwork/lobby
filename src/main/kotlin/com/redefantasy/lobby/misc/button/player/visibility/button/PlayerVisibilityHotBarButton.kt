package com.redefantasy.lobby.misc.button.player.visibility.button

import com.redefantasy.core.shared.CoreConstants
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.echo.packets.UserPreferencesUpdatedPacket
import com.redefantasy.core.shared.misc.preferences.PLAYER_VISIBILITY
import com.redefantasy.core.shared.misc.preferences.PreferenceState
import com.redefantasy.core.shared.misc.utils.TimeCode
import com.redefantasy.core.shared.users.data.User
import com.redefantasy.core.shared.users.preferences.storage.dto.UpdateUserPreferencesDTO
import com.redefantasy.core.spigot.misc.utils.ItemBuilder
import com.redefantasy.lobby.misc.button.HotBarButton
import com.redefantasy.lobby.misc.preferences.post
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

/**
 * @author Gutyerrez
 */
class PlayerVisibilityOnHotBarButton : HotBarButton(
    ItemBuilder(Material.INK_SACK)
        .name(
            "§fJogadores: §aON"
        ).durability(
            10
        ).lore(
            arrayOf(
                "§7Clique para que os usuários",
                "§7desapareçam!"
            )
        ).build(),
    7
) {

    @Subscribe
    fun on(
        event: PlayerInteractEvent
    ) {
        val player = event.player
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)!!

        user.switchPlayerVisibilityState()
    }

}

class PlayerVisibilityOffHotBarButton : HotBarButton(
    ItemBuilder(Material.INK_SACK)
        .name(
            "§fJogadores: §cOFF"
        ).durability(
            8
        ).lore(
            arrayOf(
                "§7Clique para que os usuários",
                "§7apareçam!"
            )
        ).build(),
    7
) {

    @Subscribe
    fun on(
        event: PlayerInteractEvent
    ) {
        val player = event.player
        val user = CoreProvider.Cache.Local.USERS.provide().fetchById(player.uniqueId)!!

        user.switchPlayerVisibilityState()
    }

}

private fun User.switchPlayerVisibilityState() {
    val preference = this.getPreferences().find { it == PLAYER_VISIBILITY }!!

    if (CoreConstants.COOLDOWNS.inCooldown(this, preference.name)) {
        val player = Bukkit.getPlayer(this.getUniqueId())

        player.sendMessage(
            ComponentBuilder()
                .append("§cAguarde ${TimeCode.toText(
                    CoreConstants.COOLDOWNS.getRemainingTime(this, preference.name),
                    1
                )}")
                .append(" §cpara fazer isso novamente.")
                .create()
        )
        return
    }

    val switchPreferenceState = when (preference.preferenceState) {
        PreferenceState.ENABLED -> PreferenceState.DISABLED
        PreferenceState.DISABLED -> PreferenceState.ENABLED
        else -> TODO("not implemented-yet")
    }

    preference.preferenceState = switchPreferenceState

    CoreProvider.Repositories.Postgres.USERS_PREFERENCES_REPOSITORY.provide().update(
        UpdateUserPreferencesDTO(
            this.id,
            this.getPreferences()
        )
    )

    val packet = UserPreferencesUpdatedPacket(
        this.id,
        this.getPreferences()
    )

    CoreProvider.Databases.Redis.ECHO.provide().publishToAll(packet)

    CoreConstants.COOLDOWNS.start(this, preference.name, TimeUnit.SECONDS.toMillis(3))

    preference.post(this)
}
