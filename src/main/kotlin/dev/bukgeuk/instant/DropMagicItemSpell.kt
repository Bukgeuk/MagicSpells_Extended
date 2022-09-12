package dev.bukgeuk.instant

import com.google.common.collect.Multimap
import com.google.common.collect.ArrayListMultimap
import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.spells.InstantSpell
import com.nisovin.magicspells.util.MagicConfig
import com.nisovin.magicspells.util.magicitems.MagicItems
import dev.bukgeuk.InitSpell
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor
import kotlin.math.pow
import kotlin.random.Random

class DropMagicItemSpell(config: MagicConfig?, spellName: String?): InstantSpell(config, spellName) {
    private val itemName = getConfigString("item", "")
    private val minHp = getConfigDouble("min-hp", 0.0)
    private val maxHp = getConfigDouble("max-hp", 0.0)
    private val hpOperation = getConfigString("hp-operation", "+")
    private val minMp = getConfigDouble("min-mp", 0.0)
    private val maxMp = getConfigDouble("max-mp", 0.0)
    private val mpOperation = getConfigString("mp-operation", "+")
    private val minAtk = getConfigDouble("min-atk", 0.0)
    private val maxAtk = getConfigDouble("max-atk", 0.0)
    private val atkOperation = getConfigString("atk-operation", "+")
    private val minAgi = getConfigDouble("min-agi", 0.0)
    private val maxAgi = getConfigDouble("max-agi", 0.0)
    private val agiOperation = getConfigString("agi-operation", "+")
    private val agiMultiplier = getConfigDouble("agi-multiplier", 0.003)
    private val minMatk = getConfigDouble("min-matk", 0.0)
    private val maxMatk = getConfigDouble("max-matk", 0.0)
    private val matkOperation = getConfigString("matk-operation", "+")
    private val minDef = getConfigDouble("min-def", 0.0)
    private val maxDef = getConfigDouble("max-def", 0.0)
    private val defOperation = getConfigString("def-operation", "+")
    private val itemToDrop: ItemStack

    init {
        val item = MagicItems.getMagicItemByInternalName(itemName)
        itemToDrop = if (item == null) {
            MagicSpells.error("DropMagicItemSpell(Extended) '${this.internalName}' has invalid magic item")
            ItemStack(Material.AIR)
        } else {
            item.itemStack
        }
    }

    private fun stringToOperation(str: String): AttributeModifier.Operation {
        return if (str == "*") AttributeModifier.Operation.MULTIPLY_SCALAR_1
        else AttributeModifier.Operation.ADD_NUMBER
    }

    private fun getRandomDouble(min: Double, max: Double, digit: Int): Double {
        val d = 10.0.pow(digit.toDouble())
        return floor(Random.nextDouble(min, max + 1 / d) * d) / d
    }

    private fun valueWithOperation(value: Double, operation: String, integer: Boolean): String {
        var str = if (value > 0) "+" else ""
        str += if (integer) value.toInt() else value
        if (operation == "*") str += "%"
        return str
    }

    private fun generateComponent(value: Double, operation: String, str: String, integer: Boolean = false): Component {
        val vstr = valueWithOperation(value, operation, integer)
        val component = Component.text("$str $vstr").decoration(TextDecoration.ITALIC, false)
        return when {
            value > 0 -> component.color(TextColor.fromHexString("#3498DB"))
            value < 0 -> component.color(TextColor.fromHexString("#E74C3C"))
            else -> component
        }
    }

    override fun castSpell(caster: LivingEntity?, state: SpellCastState?, power: Float, args: Array<out String>?): PostCastAction {
        val item = ItemStack(itemToDrop)

        val meta = item.itemMeta
        val lore: MutableList<Component> = if (meta.hasLore()) meta.lore() as MutableList<Component> else mutableListOf()
        val container = meta.persistentDataContainer
        val map: Multimap<Attribute, AttributeModifier> = ArrayListMultimap.create()

        var rand = getRandomDouble(minHp, maxHp, 0)
        if (rand != 0.0) {
            map.put(Attribute.GENERIC_MAX_HEALTH, AttributeModifier("generic.max_health", rand, stringToOperation(hpOperation)))
            lore.add(generateComponent(rand, hpOperation, "HP", true))
        }

        rand = getRandomDouble(minMp, maxMp, 0)
        if (rand != 0.0) {
            container.set(InitSpell.attributeMpKey, PersistentDataType.DOUBLE, rand)
            container.set(InitSpell.attributeMpOperationKey, PersistentDataType.STRING, mpOperation)
            lore.add(generateComponent(rand, mpOperation, "MP", true))
        }

        rand = getRandomDouble(minAtk, maxAtk, 2)
        if (rand != 0.0) {
            map.put(Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier("generic.attack_damage", rand, stringToOperation(atkOperation)))
            lore.add(generateComponent(rand, atkOperation, "공격력"))
        }

        rand = getRandomDouble(minMatk, maxMatk, 2)
        if (rand != 0.0) {
            container.set(InitSpell.attributeMatkKey, PersistentDataType.DOUBLE, rand)
            container.set(InitSpell.attributeMatkOperationKey, PersistentDataType.STRING, matkOperation)
            lore.add(generateComponent(rand, matkOperation, "마법 공격력"))
        }

        rand = getRandomDouble(minAgi, maxAgi, 0)
        if (rand != 0.0) {
            map.put(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier("generic.movement_speed", rand * agiMultiplier, stringToOperation(agiOperation)))
            lore.add(generateComponent(rand, agiOperation, "민첩", true))
        }

        rand = getRandomDouble(minDef, maxDef, 0)
        if (rand != 0.0) {
            container.set(InitSpell.attributeDefKey, PersistentDataType.DOUBLE, rand)
            container.set(InitSpell.attributeDefOperationKey, PersistentDataType.STRING, defOperation)
            lore.add(generateComponent(rand, defOperation, "방어력", true))
        }

        meta.attributeModifiers = map
        meta.lore(lore)
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        item.itemMeta = meta

        caster?.world?.dropItem(caster.location, item)

        return PostCastAction.HANDLE_NORMALLY
    }
}