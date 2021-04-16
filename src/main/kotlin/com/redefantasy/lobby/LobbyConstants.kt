package com.redefantasy.lobby

import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.spigot.world.WorldCuboid
import com.redefantasy.lobby.misc.utils.ServerConnectorUtils
import org.bukkit.event.player.PlayerInteractEvent
import java.util.function.Function

/**
 * @author Gutyerrez
 */
object LobbyConstants {

    val SERVERS_WORLD_CUBOIDS = mapOf(
        Pair(
            CoreProvider.Cache.Local.SERVERS.provide().fetchByName("FACTIONS_PHOENIX"),
            WorldCuboid(
                -2,
                91,
                -73,
                2,
                95,
                -75
            )
        )
    )

    val SERVER_CUBOID = Function<PlayerInteractEvent, Unit> {
        val player = it.player
        val clickedBlock = it.clickedBlock

        println("a")

        val entry = SERVERS_WORLD_CUBOIDS.entries.stream().filter { entry ->
            entry.value.contains(
                clickedBlock.x,
                clickedBlock.y,
                clickedBlock.z
            )
        }.findFirst().orElse(null)

        println("b")

        if (entry !== null) {
            println("c")

            val server = entry.key

            if (server === null) return@Function

            println("d")

            ServerConnectorUtils.connect(
                player,
                server
            )
        }
    }

}