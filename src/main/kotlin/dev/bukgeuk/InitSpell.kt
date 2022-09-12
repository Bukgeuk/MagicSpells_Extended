package dev.bukgeuk

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.Spell
import com.nisovin.magicspells.util.MagicConfig
import dev.bukgeuk.listener.ConnectionEventHandler
import dev.bukgeuk.listener.DamageEventHandler
import dev.bukgeuk.listener.ItemChangeEventHandler
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity

class InitSpell(config: MagicConfig?, spellName: String?) : Spell(config, spellName) {
    private val defConstant = getConfigDouble("def-constant", 150.0)
    private val defVariableName = getConfigString("def-variable", "")
    private val defBaseVariableName = getConfigString("def-base-variable", "")
    private val mpVariableName = getConfigString("mp-variable", "")
    private val mpBaseVariableName = getConfigString("mp-base-variable", "")
    private val matkVariableName = getConfigString("matk-variable", "")
    private val matkBaseVariableName = getConfigString("matk-base-variable", "")
    private val healthScale = getConfigDouble("health-scale", 20.0)
    private val hpLevelVariableName = getConfigString("hp-level-variable", "")
    private val atkLevelVariableName = getConfigString("atk-level-variable", "")
    private val agiLevelVariableName = getConfigString("agi-level-variable", "")
    private val hpIncreaseGap = getConfigDouble("hp-increase-gap", 0.0)
    private val atkIncreaseGap = getConfigDouble("atk-increase-gap", 0.0)
    private val agiGap = getConfigDouble("agi-gap", 0.0)

    companion object {
        val attributeMpKey = NamespacedKey(MagicSpells.plugin, "attributeMp")
        val attributeMpOperationKey = NamespacedKey(MagicSpells.plugin, "attributeMpOperation")
        val attributeMatkKey = NamespacedKey(MagicSpells.plugin, "attributeMatk")
        val attributeMatkOperationKey = NamespacedKey(MagicSpells.plugin, "attributeMatkOperation")
        val attributeDefKey = NamespacedKey(MagicSpells.plugin, "attributeDef")
        val attributeDefOperationKey = NamespacedKey(MagicSpells.plugin, "attributeDefOperation")
    }

    init {
        MagicSpells.registerEvents(DamageEventHandler(defVariableName, defConstant))
        MagicSpells.registerEvents(ConnectionEventHandler(healthScale, hpLevelVariableName, hpIncreaseGap, atkLevelVariableName, atkIncreaseGap, agiLevelVariableName, agiGap))
        MagicSpells.registerEvents(ItemChangeEventHandler(mpVariableName, mpBaseVariableName, defVariableName, defBaseVariableName, matkVariableName, matkBaseVariableName))
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