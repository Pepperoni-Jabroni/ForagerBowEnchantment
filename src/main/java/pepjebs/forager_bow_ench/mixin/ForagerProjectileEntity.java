package pepjebs.forager_bow_ench.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pepjebs.forager_bow_ench.ForagerBowEnchantmentMod;
import pepjebs.forager_bow_ench.util.ForagerArrowEntityInterface;

@Mixin(ProjectileEntity.class)
public class ForagerProjectileEntity {

    private ProjectileEntity projectileEntity = ((ProjectileEntity) (Object) this);

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
        if (projectileEntity instanceof ArrowEntity && shooter instanceof LivingEntity) {
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
            // 2: WITH_XP
            return 2;
        }
        // 3: WITH_ALL
        return  3;
    }
}
