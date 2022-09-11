package dev.bukgeuk.listener

import com.nisovin.magicspells.MagicSpells
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectionEventHandler(private val healthScale: Double,
                             private val AtkVariableName: String, private val AtkIncreaseGap: Double,
                             private val AgiVariableName: String, private val AgiGap: Double
                             ): Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val vm = MagicSpells.getVariableManager()
        val scale = vm.getVariable("meta_health_scale")
        val atk = vm.getVariable("meta_attribute_generic_attack_damage_base")
        val atkLevel = vm.getValue(AtkVariableName, event.player)
        val atkValue = atkLevel * (2 * AtkIncreaseGap + (atkLevel - 1) * AtkIncreaseGap) / 2
        val agi = vm.getVariable("meta_attribute_generic_movement_speed_base")
        val agiLevel = vm.getValue(AgiVariableName, event.player)
        val agiValue = agiLevel * AgiGap

        if (scale.getValue(event.player) != healthScale) scale.set(event.player, healthScale)

        atk.set(event.player, atk.getValue(event.player) + atkValue)
        agi.set(event.player, agi.getValue(event.player) + agiValue)
    }
}