package dev.bukgeuk.targeted

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.events.MagicSpellsEntityDamageByEntityEvent
import com.nisovin.magicspells.events.SpellApplyDamageEvent
import com.nisovin.magicspells.handlers.DebugHandler
import com.nisovin.magicspells.spells.DamageSpell
import com.nisovin.magicspells.spells.TargetedEntitySpell
import com.nisovin.magicspells.spells.TargetedSpell
import com.nisovin.magicspells.util.MagicConfig
import com.nisovin.magicspells.util.Util
import com.nisovin.magicspells.util.compat.CompatBasics
import com.nisovin.magicspells.util.compat.EventUtil
import org.bukkit.EntityEffect
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.mariuszgromada.math.mxparser.Expression
import java.lang.NumberFormatException
import java.util.*

class PainSpell(config: MagicConfig?, spellName: String?) : TargetedSpell(config, spellName), TargetedEntitySpell, DamageSpell {
    private val spellDamageType = getConfigString("spell-damage-type", "")
    private val defaultDamage: Double
    private val multiplier: Double
    private var damageType: DamageCause? = null
    private val damageExpression: String
    private val ignoreArmor: Boolean
    private val checkPlugins: Boolean
    private val avoidDamageModification: Boolean
    private val tryAvoidingAntiCheatPlugins: Boolean
    override fun castSpell(
        caster: LivingEntity,
        state: SpellCastState,
        power: Float,
        args: Array<String>?
    ): PostCastAction {
        return if (state == SpellCastState.NORMAL) {
            val target = this.getTargetedEntity(caster, power)
            if (target == null) {
                this.noTarget(caster)
            } else {
                val done: Boolean = if (caster is Player) {
                    CompatBasics.exemptAction({
                        causePain(
                            caster,
                            target.target as LivingEntity,
                            target.power
                        )
                    }, caster, CompatBasics.activeExemptionAssistant.painExemptions) as Boolean
                } else {
                    causePain(caster, target.target as LivingEntity, target.power)
                }
                if (!done) {
                    this.noTarget(caster)
                } else {
                    this.sendMessages(caster, target.target as LivingEntity, args)
                    PostCastAction.NO_MESSAGES
                }
            }
        } else {
            PostCastAction.HANDLE_NORMALLY
        }
    }

    override fun castAtEntity(caster: LivingEntity, target: LivingEntity, power: Float): Boolean {
        return if (!validTargetList.canTarget(caster, target)) false else causePain(caster, target, power)
    }

    override fun castAtEntity(target: LivingEntity, power: Float): Boolean {
        return if (!validTargetList.canTarget(target)) false else causePain(null as LivingEntity?, target, power)
    }

    override fun getSpellDamageType(): String {
        return spellDamageType
    }

    private fun causePain(caster: LivingEntity?, target: LivingEntity?, power: Float): Boolean {
        return if (target == null) {
            false
        } else if (target.isDead) {
            false
        } else {
            var damage = if (damageExpression == "" || caster !is Player) defaultDamage else {
               try {
                   Expression(MagicSpells.doVariableReplacements(caster, damageExpression)).calculate()
               } catch (e: NumberFormatException) {
                   MagicSpells.error("PainSpell(Extended) '${this.internalName}' has invalid damage expression")
                   defaultDamage
               }
            }
            damage *= multiplier

            var localDamage = damage * power.toDouble()
            if (checkPlugins) {
                val event = MagicSpellsEntityDamageByEntityEvent(caster, target, damageType, localDamage, this)
                EventUtil.call(event)
                if (event.isCancelled) {
                    return false
                }
                if (!avoidDamageModification) {
                    localDamage = event.damage
                }
                target.lastDamageCause = event
            }
            val event = SpellApplyDamageEvent(this, caster, target, localDamage, damageType, spellDamageType)
            EventUtil.call(event)
            localDamage = event.finalDamage
            if (ignoreArmor) {
                var health = target.health
                if (health > Util.getMaxHealth(target)) {
                    health = Util.getMaxHealth(target)
                }
                health -= localDamage
                if (health < 0.0) {
                    health = 0.0
                }
                if (health > Util.getMaxHealth(target)) {
                    health = Util.getMaxHealth(target)
                }
                if (health == 0.0 && caster is Player) {
                    target.killer = caster
                }
                target.health = health
                target.lastDamage = localDamage
                this.playSpellEffects(caster, target)
                target.playEffect(EntityEffect.HURT)
                true
            } else {
                if (tryAvoidingAntiCheatPlugins) {
                    target.damage(localDamage)
                } else {
                    target.damage(localDamage, caster)
                }
                this.playSpellEffects(caster, target)
                true
            }
        }
    }

    init {
        val damageTypeName = getConfigString("damage-type", "ENTITY_ATTACK")
        damageType = try {
            DamageCause.valueOf(damageTypeName.uppercase(Locale.getDefault()))
        } catch (var5: IllegalArgumentException) {
            DebugHandler.debugBadEnumValue(DamageCause::class.java, damageTypeName)
            DamageCause.ENTITY_ATTACK
        }
        defaultDamage = getConfigDouble("default-damage", 4.0)
        multiplier = getConfigDouble("multiplier", 1.0)
        damageExpression = getConfigString("damage-expression", "")
        ignoreArmor = getConfigBoolean("ignore-armor", false)
        checkPlugins = getConfigBoolean("check-plugins", true)
        avoidDamageModification = getConfigBoolean("avoid-damage-modification", true)
        tryAvoidingAntiCheatPlugins = getConfigBoolean("try-avoiding-anticheat-plugins", false)
    }
}
