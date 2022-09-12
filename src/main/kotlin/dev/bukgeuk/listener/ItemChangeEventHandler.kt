package dev.bukgeuk.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.nisovin.magicspells.MagicSpells
import dev.bukgeuk.InitSpell
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerChangedMainHandEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemChangeEventHandler(private val mpVariable: String, private val mpBaseVariable: String,
                             private val defVariable: String, private val defBaseVariable: String,
                             private val matkVariable: String, private val matkBaseVariable: String
                       ): Listener {
    private fun calculate(player: Player, main: ItemStack?, off: ItemStack?, helmet: ItemStack?, chestplate: ItemStack?, leggings: ItemStack?, boots: ItemStack?) {
        val vm = MagicSpells.getVariableManager()
        val mp = vm.getVariable(mpVariable)
        var mpa = 0.0; var mpm = 0.0
        val def = vm.getVariable(defVariable)
        var defa = 0.0; var defm = 0.0
        val matk = vm.getVariable(matkVariable)
        var matka = 0.0; var matkm = 0.0

        val list = listOf(main, off, helmet, chestplate, leggings, boots)

        for (item in list) {
            if (item == null) continue
            if (item.itemMeta == null) continue
            val container = item.itemMeta.persistentDataContainer

            var value = container.get(InitSpell.attributeMpKey, PersistentDataType.DOUBLE)
            if (value != null) {
                if (container.get(InitSpell.attributeMpOperationKey, PersistentDataType.STRING) == "*") mpm += value
                else mpa += value
            }

            value = container.get(InitSpell.attributeMatkKey, PersistentDataType.DOUBLE)
            if (value != null) {
                if (container.get(InitSpell.attributeMatkOperationKey, PersistentDataType.STRING) == "*") matkm += value
                else matka += value
            }

            value = container.get(InitSpell.attributeDefKey, PersistentDataType.DOUBLE)
            if (value != null) {
                if (container.get(InitSpell.attributeDefOperationKey, PersistentDataType.STRING) == "*") defm += value
                else defa += value
            }
        }

        var base = vm.getValue(mpBaseVariable, player)
        mp.set(player, (base + mpa) * (1 + mpm))
        base = vm.getValue(matkBaseVariable, player)
        matk.set(player, (base + matka) * (1 + matkm))
        base = vm.getValue(defBaseVariable, player)
        def.set(player, (base + defa) * (1 + defm))
    }

    @EventHandler
    fun onArmorChange(event: PlayerArmorChangeEvent) {
        val i = event.player.inventory
        when(event.slotType) {
            PlayerArmorChangeEvent.SlotType.HEAD -> calculate(event.player, i.itemInMainHand, i.itemInOffHand, event.newItem, i.chestplate, i.leggings, i.boots)
            PlayerArmorChangeEvent.SlotType.CHEST -> calculate(event.player, i.itemInMainHand, i.itemInOffHand, i.helmet, event.newItem, i.leggings, i.boots)
            PlayerArmorChangeEvent.SlotType.LEGS -> calculate(event.player, i.itemInMainHand, i.itemInOffHand, i.helmet, i.chestplate, event.newItem, i.boots)
            PlayerArmorChangeEvent.SlotType.FEET -> calculate(event.player, i.itemInMainHand, i.itemInOffHand, i.helmet, i.chestplate, i.leggings, event.newItem)
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val i = event.player.inventory
        calculate(event.player as Player, i.itemInMainHand, i.itemInOffHand, i.helmet, i.chestplate, i.leggings, i.boots)
    }

    @EventHandler
    fun onItemHeld(event: PlayerItemHeldEvent) {
        val i = event.player.inventory
        calculate(event.player, i.getItem(event.newSlot), i.itemInOffHand, i.helmet, i.chestplate, i.leggings, i.boots)
    }
}