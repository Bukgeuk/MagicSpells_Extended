package dev.bukgeuk.instant

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.spells.InstantSpell
import com.nisovin.magicspells.util.MagicConfig
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class ActionbarSpell(config: MagicConfig?, spellName: String): InstantSpell(config, spellName) {
    private val message = getConfigString("message", "")

    override fun castSpell(caster: LivingEntity?, state: SpellCastState?, power: Float, args: Array<out String>?): PostCastAction {
        if (caster is Player) {
            val text = MagicSpells.doVariableReplacements(caster, message)
            val component = LegacyComponentSerializer.legacyAmpersand().deserialize(text)
            caster.sendActionBar(component)
        }

        return PostCastAction.HANDLE_NORMALLY
    }
}