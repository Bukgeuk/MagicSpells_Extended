package dev.bukgeuk.instant

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.spells.InstantSpell
import com.nisovin.magicspells.spells.instant.ParticleProjectileSpell
import com.nisovin.magicspells.util.MagicConfig
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import kotlin.math.min

class MultipleParticleProjectileSpell(config: MagicConfig?, spellName: String?): InstantSpell(config, spellName) {
    private var PPSpell: ParticleProjectileSpell? = null
    private val horizontalRotations: List<Double> = try {
        getConfigStringList("horizontal-rotations", emptyList<String>()).map { it.toDouble() }
    } catch (e: NumberFormatException) {
        MagicSpells.error("MultipleParticleProjectileSpell(Extended) '${this.internalName}' has invalid horizontal rotations")
        emptyList()
    }
    private val verticalRotations: List<Float> = try {
        getConfigStringList("vertical-rotations", emptyList<String>()).map { it.toFloat() }
    } catch (e: NumberFormatException) {
        MagicSpells.error("MultipleParticleProjectileSpell(Extended) '${this.internalName}' has invalid vertical rotations")
        emptyList()
    }
    private val spell: String = getConfigString("spell", "")

    override fun initialize() {
        super.initialize()

        val s = MagicSpells.getSpellByInternalName(spell)
        if (s == null || s !is ParticleProjectileSpell) {
            MagicSpells.error("MultipleParticleProjectileSpell(Extended) '${this.internalName}' has invalid spell")
        } else {
            PPSpell = s
        }
    }

    override fun castSpell(caster: LivingEntity?, state: SpellCastState?, power: Float, args: Array<out String>?): PostCastAction {
        if (PPSpell != null) {
            for (i in 0 until min(horizontalRotations.count(), verticalRotations.count())) {
                PPSpell!!.horizontalRotation = horizontalRotations[i]
                PPSpell!!.setVerticalRotation(verticalRotations[i])
                PPSpell!!.castSpell(caster, state, power, args)
            }
        }

        return PostCastAction.HANDLE_NORMALLY
    }
}