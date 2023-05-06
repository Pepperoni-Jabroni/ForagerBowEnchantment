package pepjebs.forager_bow_ench.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import pepjebs.forager_bow_ench.ForagerBowEnchantmentMod;

public class ForagerBowEnchantment extends Enchantment {
    public ForagerBowEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.BOW, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 15 * level;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return ForagerBowEnchantmentMod.CONFIG != null
                && ForagerBowEnchantmentMod.CONFIG.isForagerAvailableForRandomSelection;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return ForagerBowEnchantmentMod.CONFIG != null
                && ForagerBowEnchantmentMod.CONFIG.isForagerAvailableForEnchantedBookOffer;
    }
}
