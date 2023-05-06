package pepjebs.forager_bow_ench.entity;

import net.minecraft.entity.*;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class ForagerDeathCloudEntity extends AreaEffectCloudEntity {

    public int level = 0;

    public ForagerDeathCloudEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        var owner = this.getOwner();
        List<ItemEntity> itemsInBox = this.world.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox());
        List<ExperienceOrbEntity> xpInBox = this.world.getNonSpectatingEntities(
                ExperienceOrbEntity.class, this.getBoundingBox());
        for(var entity: xpInBox){
            if (this.level >= 1 && owner != null) {
                entity.teleport(owner.getX(), owner.getY(), owner.getZ());
            }
        }
        for(var entity : itemsInBox) {
            if (this.level == 2 && owner != null) {
                entity.teleport(owner.getX(), owner.getY(), owner.getZ());
            }
        }
    }
}
