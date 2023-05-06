package pepjebs.forager_bow_ench.mixin;

import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import pepjebs.forager_bow_ench.util.ForagerArrowEntityInterface;

@Mixin(ArrowEntity.class)
public class ArrowEntityIsForagerMixin implements ForagerArrowEntityInterface {

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
}
