package dev.bukgeuk

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.Spell
import com.nisovin.magicspells.util.MagicConfig
import dev.bukgeuk.listener.ConnectionEventHandler
import dev.bukgeuk.listener.DamageEventHandler
import org.bukkit.entity.LivingEntity

class InitSpell(config: MagicConfig?, spellName: String?) : Spell(config, spellName) {
    private val defenseConstant = getConfigDouble("defense-constant", 150.0)
    private val defenseVariableName = getConfigString("defense-variable", "")
    private val healthScale = getConfigDouble("health-scale", 20.0)

    init {
        MagicSpells.registerEvents(DamageEventHandler(defenseVariableName, defenseConstant))
        MagicSpells.registerEvents(ConnectionEventHandler(healthScale))
    }

    override fun castSpell(p0: LivingEntity?, p1: SpellCastState?, p2: Float, p3: Array<out String>?): PostCastAction {
        return PostCastAction.HANDLE_NORMALLY
    }

    override fun canCastWithItem(): Boolean {
        return false
    }

    override fun canCastByCommand(): Boolean {
        return false
    }

}