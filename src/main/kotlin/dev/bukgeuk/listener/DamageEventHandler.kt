package dev.bukgeuk.listener

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.util.MagicConfig
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class DamageEventHandler(private val defenseVariableName: String, private val defenseConstant: Double): Listener {
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entityType == EntityType.PLAYER) {
            val def = MagicSpells.getVariableManager().getValue(defenseVariableName, event.entity as Player)
            MagicSpells.log("def : $def")
            val rate = def / (def + defenseConstant)
            event.damage *= (1 - rate)
            MagicSpells.log("edited damage: ${event.damage}")
        }
    }

}