package dev.bukgeuk.listener

import com.nisovin.magicspells.MagicSpells
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectionEventHandler(private val healthScale: Double): Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val vm = MagicSpells.getVariableManager()
        val scale = vm.getVariable("meta_health_scale")
        if (scale.getValue(event.player) != healthScale) scale.set(event.player, healthScale)
    }
}