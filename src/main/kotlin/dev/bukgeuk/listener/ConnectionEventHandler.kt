package dev.bukgeuk.listener

import com.nisovin.magicspells.MagicSpells
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectionEventHandler(private val healthScale: Double,
                             private val hpVariableName: String, private val hpIncreaseGap: Double,
                             private val atkVariableName: String, private val atkIncreaseGap: Double,
                             private val agiVariableName: String, private val agiGap: Double
                             ): Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val vm = MagicSpells.getVariableManager()
        val p = event.player

        val scale = vm.getVariable("meta_health_scale")

        val hp = vm.getVariable("meta_attribute_generic_max_health_base")
        val hpLevel = vm.getValue(hpVariableName, p)
        val hpValue = hpLevel * (2 * hpIncreaseGap + (hpLevel - 1) * hpIncreaseGap) / 2

        val atk = vm.getVariable("meta_attribute_generic_attack_damage_base")
        val atkLevel = vm.getValue(atkVariableName, p)
        val atkValue = atkLevel * (2 * atkIncreaseGap + (atkLevel - 1) * atkIncreaseGap) / 2

        val agi = vm.getVariable("meta_attribute_generic_movement_speed_base")
        val agiLevel = vm.getValue(agiVariableName, p)
        val agiValue = agiLevel * agiGap

        if (scale.getValue(p) != healthScale) scale.set(p, healthScale)

        hp.set(p, hp.getValue(p) + hpValue)
        atk.set(p, atk.getValue(p) + atkValue)
        agi.set(p, agi.getValue(p) + agiValue)
    }
}