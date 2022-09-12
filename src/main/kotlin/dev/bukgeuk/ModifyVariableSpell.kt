package dev.bukgeuk

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.Spell
import com.nisovin.magicspells.util.MagicConfig
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.mariuszgromada.math.mxparser.Expression

class ModifyVariableSpell(config: MagicConfig?, spellName: String): Spell(config, spellName) {
    private val variableName = getConfigString("variable", "")
    private val expression: String = getConfigString("expression", "")
    private val subSpellName: String = getConfigString("spell", "")
    private val subSpell = MagicSpells.getSpellByInternalName(subSpellName)

    override fun castSpell(caster: LivingEntity?, state: SpellCastState?, power: Float, args: Array<out String>?): PostCastAction {
        if (caster is Player) {
            val value = Expression(MagicSpells.doVariableReplacements(caster, expression)).calculate()
            MagicSpells.getVariableManager().set(variableName, caster, value)
        }

        subSpell?.castSpell(caster, state, power, args)

        return PostCastAction.HANDLE_NORMALLY
    }

    override fun canCastWithItem(): Boolean {
        return true
    }

    override fun canCastByCommand(): Boolean {
        return true
    }

}