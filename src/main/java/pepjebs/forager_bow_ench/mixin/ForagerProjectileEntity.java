package pepjebs.forager_bow_ench.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pepjebs.forager_bow_ench.ForagerBowEnchantmentMod;
import pepjebs.forager_bow_ench.util.ForagerArrowEntityInterface;

@Mixin(ProjectileEntity.class)
public class ForagerProjectileEntity implements ForagerArrowEntityInterface {

    private ProjectileEntity projectileEntity = ((ProjectileEntity) (Object) this);

    // 0: OFF, 1: WITH_ITEMS, 2: WITH_XP, 3: WITH_ALL
    public int isForagerSourcedLevel = 0;

    @Override
    public int getForagerLevel() {
        return isForagerSourcedLevel;
    }

    @Override
    public void setForagerLevel(int i) {
        isForagerSourcedLevel = i;
    }

    @Inject(
            method = "setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V",
            at = @At("HEAD")
    )
    public void setProjEntityForagerAttr(
            Entity shooter,
            float pitch,
            float yaw,
            float roll,
            float speed,
            float divergence,
            CallbackInfo ci
    ) {
        if (shooter instanceof LivingEntity) {
            var stack = ((LivingEntity) shooter).getMainHandStack();
            if (stack.hasEnchantments()
                    && EnchantmentHelper.get(stack).containsKey(ForagerBowEnchantmentMod.FORAGER_ENCHANT)) {
                int enchantLevel = EnchantmentHelper.get(stack).get(ForagerBowEnchantmentMod.FORAGER_ENCHANT);
                int foragerLevel = getForagerLevelForEnchLevel(enchantLevel);
                ((ForagerArrowEntityInterface) projectileEntity).setForagerLevel(foragerLevel);
            }
        }
    }

    private int getForagerLevelForEnchLevel(int enchantLevel) {
        if (enchantLevel == 1) {
            if (ForagerBowEnchantmentMod.CONFIG == null || ForagerBowEnchantmentMod.CONFIG.doExperienceAsLevelOne) {
                // 2: WITH_XP
                return 2;
            } else {
                // 1: WITH_ITEMS
                return 1;
            }
        }
        // 3: WITH_ALL
        return  3;
    }
}
