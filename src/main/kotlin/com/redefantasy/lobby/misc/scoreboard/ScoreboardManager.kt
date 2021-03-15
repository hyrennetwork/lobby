package com.redefantasy.lobby.misc.scoreboard

import com.google.common.collect.Queues
import com.redefantasy.core.shared.CoreProvider
import com.redefantasy.core.shared.users.storage.table.UsersTable
import com.redefantasy.core.spigot.misc.scoreboard.bukkit.GroupScoreboard
import com.redefantasy.lobby.LobbyPlugin
import com.redefantasy.lobby.LobbyProvider
import com.redefantasy.lobby.user.data.LobbyUser
import org.apache.commons.lang3.StringUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.id.EntityID
import java.util.function.Consumer

/**
 * @author Gutyerrez
 */
object ScoreboardManager {

    val UPDATE_SCOREBOARD = Consumer<LobbyUser> {
        this.update(
            it.player,
            Slot.ONLINE_PLAYERS
        )
    }

    init {
        val queue = Queues.newConcurrentLinkedQueue<Player>()

        Bukkit.getScheduler().runTaskTimer(
            LobbyPlugin.instance,
            {
               if (queue.isEmpty() && Bukkit.getOnlinePlayers().isEmpty()) return@runTaskTimer

                if (queue.isEmpty() && Bukkit.getOnlinePlayers().isNotEmpty()) {
                    queue.addAll(Bukkit.getOnlinePlayers())
                }

                val player = queue.poll()
                val lobbyUser = LobbyProvider.Cache.Local.LOBBY_USERS.provide().fetchById(
                    player.uniqueId
                )

                if (lobbyUser !== null) this.UPDATE_SCOREBOARD.accept(lobbyUser)
            },
            0,
            5
        )
    }

    fun construct(player: Player) {
        val user = LobbyProvider.Cache.Local.LOBBY_USERS.provide().fetchById(player.uniqueId)!!

        val fancyGroupName = user.getHighestGroup().getFancyDisplayName()
        val scoreboard = LobbyScoreboard()

        scoreboard.registerTeams()

        scoreboard.setTitle("§6§lREDE FANTASY")
        scoreboard.set(15, "§0")
        scoreboard.set(13, "§f Grupo: $fancyGroupName")
        scoreboard.set(12, "§1")

        this.update(
            player,
            Slot.ONLINE_PLAYERS,
            Slot.SERVER_LIST
        )

        val bukkitApplicationName = CoreProvider.application.displayName.split(" ")[1]

        scoreboard.set(3, "§2")
        scoreboard.set(2, "§f Saguão: §7#$bukkitApplicationName")
        scoreboard.set(1, "§3")
        scoreboard.set(0, "§e  loja.redefantasy.com")

        user.scoreboard = scoreboard

        Bukkit.getOnlinePlayers().forEach {
            val targetUser = LobbyProvider.Cache.Local.LOBBY_USERS.provide().fetchById(
                EntityID(
                    it.uniqueId,
                    UsersTable
                )
            )!!

            val groupBoard = targetUser.scoreboard as GroupScoreboard

            groupBoard.registerUser(user)
            scoreboard.registerUser(targetUser)
        }

        scoreboard.send(
            arrayOf(
                player
            )
        )
    }

    fun update(player: Player, vararg slots: Slot) {
        val user = LobbyProvider.Cache.Local.LOBBY_USERS.provide().fetchById(player.uniqueId)!!

        val scoreboard = user.scoreboard

        for (slot in slots) {
            when (slot) {
                Slot.ONLINE_PLAYERS -> {
                    val onlineUsers = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchUsers()

                    scoreboard.set(14, "§f Online: §7${onlineUsers.size}")
                    break
                }
                Slot.SERVER_LIST -> {
                    var i = 11

                    CoreProvider.Cache.Local.SERVERS.provide().fetchAll().forEach {
                        val onlineUsers = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchUsersByServer(it)

                        scoreboard.set(
                            i, "§f ${
                                StringUtils.replaceEach(
                                    it.displayName,
                                    arrayOf(
                                        "Rankup",
                                        "Factions"
                                    ),
                                    arrayOf(
                                        "R.",
                                        "F."
                                    )
                                )
                            }: §a${onlineUsers.size}"
                        )

                        if (i >= 4) i--
                    }
                }
            }
        }
    }

    enum class Slot {

        ONLINE_PLAYERS, SERVER_LIST

    }

}