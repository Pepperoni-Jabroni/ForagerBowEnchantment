package pepjebs.forager_bow_ench.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import pepjebs.forager_bow_ench.ForagerBowEnchantmentMod;

import java.util.Collections;
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
        if (this.level == 0 || this.world.isClient()) {
            return;
        }
        var owner = this.getOwner();
        List<ItemEntity> itemsInBox = this.world.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox());
        List<ExperienceOrbEntity> xpInBox = this.world.getNonSpectatingEntities(
                ExperienceOrbEntity.class, this.getBoundingBox());
        for(var xpOrb: xpInBox){
            if (this.level >= 2 && owner != null) {
                xpOrb.teleport(owner.getX(), owner.getY(), owner.getZ());
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
                var didCompress = tryInvStack(owner, item);
                if (!didCompress) {
                    owner.dropStack(item.getStack());
                    item.kill();
                }
            }
        }
    }

    private boolean tryInvStack(LivingEntity livingEntity, ItemEntity itemEntity) {
        if (livingEntity instanceof PlayerEntity playerEntity) {
            var itemOfEntity = itemEntity.getStack().getItem();
            int itemEntityStackCount = itemEntity.getStack().getCount();
            // First pass will try to compress on existing stacks
            for(int i = 0; i < playerEntity.getInventory().main.size(); i++) {
                var invStack = playerEntity.getInventory().main.get(i);
                if (invStack.isOf(itemOfEntity) && (invStack.getMaxCount() > invStack.getCount())) {
                    int countDiff = invStack.getMaxCount() - invStack.getCount();
                    int amountToAdd = Math.min(countDiff, itemEntityStackCount);
                    itemEntityStackCount -= amountToAdd;
                    playerEntity.getInventory().main.get(i).increment(amountToAdd);
                    if (ForagerBowEnchantmentMod.shouldLog()) {
                        ForagerBowEnchantmentMod.LOGGER.info(
                                "ForagerDeathCloudEntity: Adding "+amountToAdd
                                +" to existing "+itemOfEntity.toString());
                    }
                    if (itemEntityStackCount == 0) {
                        itemEntity.kill();
                        return true;
                    }
                }
            }
            // Second pass will try to insert into empty stack
            for(int i = 0; i < playerEntity.getInventory().main.size(); i++) {
                var invStack = playerEntity.getInventory().main.get(i);
                if (invStack.isEmpty()) {
                    var stackToSet = itemEntity.getStack();
                    if (ForagerBowEnchantmentMod.shouldLog()) {
                        ForagerBowEnchantmentMod.LOGGER.info(
                                "ForagerDeathCloudEntity: Setting blank spot with "
                                +itemEntityStackCount+" of "+itemOfEntity.toString());
                    }
                    stackToSet.setCount(itemEntityStackCount);
                    playerEntity.getInventory().main.set(i, stackToSet);
                    itemEntity.kill();
                    return true;
                }
            }
        }
        return false;
    }
}
