package pepjebs.forager_bow_ench.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import pepjebs.forager_bow_ench.ForagerBowEnchantmentMod;

import java.util.List;

public class ForagerDeathCloudEntity extends AreaEffectCloudEntity {

    // 0: OFF, 1: WITH_ITEMS, 2: WITH_XP, 3: WITH_ALL
    private int level = 0;

    public ForagerDeathCloudEntity(EntityType<? extends AreaEffectCloudEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setForagerLevel(int i) {
        level = i;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level == 0 || this.getWorld().isClient()) {
            return;
        }
        var owner = this.getOwner();
        List<ItemEntity> itemsInBox = this.getWorld().getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox());
        List<ExperienceOrbEntity> xpInBox = this.getWorld().getNonSpectatingEntities(
                ExperienceOrbEntity.class, this.getBoundingBox());
        for(var xpOrb: xpInBox){
            if (this.level >= 2 && owner != null) {
                xpOrb.teleport(owner.getX(), owner.getY(), owner.getZ());
                this.getWorld().playSound(null,
                        owner.getX(), owner.getY(), owner.getZ(),
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategory.PLAYERS,
                        0.5f, 1.0f);
                if (ForagerBowEnchantmentMod.shouldLog()) {
                    ForagerBowEnchantmentMod.LOGGER.info("ForagerDeathCloudEntity: Teleporting xp orb...");
                }
            }
        }
        for(var item : itemsInBox) {
            // Skip the ItemEntity if it was already on the ground
            if (item.getItemAge() > 10) {
                if (ForagerBowEnchantmentMod.shouldLog()) {
                    ForagerBowEnchantmentMod.LOGGER.info("ForagerDeathCloudEntity: Skipping existing ItemEntity");
                }
                continue;
            }
            if ((this.level % 2) == 1 && owner != null) {
                // We prefer to transfer directly to player inventory because this avoids the situation where the
                // player is moving too fast and the teleported item doesn't get a chance to be picked up
                if (ForagerBowEnchantmentMod.shouldLog()) {
                    ForagerBowEnchantmentMod.LOGGER.info("ForagerDeathCloudEntity: Transferring item...");
                }
                var didInsert = ((ServerPlayerEntity)owner).getInventory().insertStack(item.getStack());
                if (!didInsert) {
                    owner.dropStack(item.getStack());
                } else {
                    this.getWorld().playSound(null,
                            owner.getX(), owner.getY(), owner.getZ(),
                            SoundEvents.ENTITY_ITEM_PICKUP,
                            SoundCategory.PLAYERS,
                            0.5f, 1.0f);
                }
                item.kill();
            }
        }
    }
}
