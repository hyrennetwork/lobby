package net.hyren.lobby.misc.server.utils

import net.hyren.core.shared.servers.data.Server
import net.hyren.core.spigot.misc.hologram.Hologram
import net.hyren.lobby.misc.server.info.*
import org.bukkit.entity.Giant

/**
 * @author Gutyerrez
 */
object ServerConfigurationUtils {

	fun initServer(
		server: Server,
		npcMap: MutableMap<Server, Giant>,
		hologramsMap: MutableMap<Server, Hologram>
	) {
		npcMap[server] = server.spawnNPC()

		hologramsMap[server] = server.spawnHologram()
	}

}