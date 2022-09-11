package dev.bukgeuk.instant

import com.nisovin.magicspells.spells.InstantSpell
import com.nisovin.magicspells.util.MagicConfig
import org.bukkit.entity.LivingEntity

class DropMagicItemSpell(config: MagicConfig?, spellName: String?): InstantSpell(config, spellName) {
    override fun castSpell(p0: LivingEntity?, p1: SpellCastState?, p2: Float, p3: Array<out String>?): PostCastAction {


        return PostCastAction.HANDLE_NORMALLY
    }
}