package pepjebs.forager_bow_ench;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pepjebs.forager_bow_ench.config.ForagerBowEnchantConfig;
import pepjebs.forager_bow_ench.enchantment.ForagerBowEnchantment;
import pepjebs.forager_bow_ench.entity.ForagerDeathCloudEntity;
import pepjebs.forager_bow_ench.util.ForagerArrowEntityInterface;

public class ForagerBowEnchantmentMod implements ModInitializer {

    public static final String MOD_ID = "forager_bow_ench";
    public static Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static ForagerBowEnchantConfig CONFIG = null;

    public static final Enchantment FORAGER_ENCHANT = new ForagerBowEnchantment();
    public static final EntityType<ForagerDeathCloudEntity> FORAGER_CLOUD_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "forager_cloud"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ForagerDeathCloudEntity::new).build()
    );

    @Override
    public void onInitialize() {
        Registry.register(Registries.ENCHANTMENT, new Identifier(MOD_ID, "forager"), FORAGER_ENCHANT);

        if(FabricLoader.getInstance().isModLoaded("cloth-config")) {
            AutoConfig.register(ForagerBowEnchantConfig.class, JanksonConfigSerializer::new);
            CONFIG = AutoConfig.getConfigHolder(ForagerBowEnchantConfig.class).getConfig();
        }

        if (CONFIG == null || CONFIG.isForagerAvailableInEndCities) {
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
                if (id.compareTo(LootTables.END_CITY_TREASURE_CHEST) == 0) {
                    tableBuilder.pool(
                            LootPool.builder()
                                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(7))
                                    .apply(new SetEnchantmentsLootFunction.Builder().enchantment(
                                            FORAGER_ENCHANT,
                                            UniformLootNumberProvider.create(1, 2)).build()
                                    )
                                    .build()
                    );
                }
            });
        }

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            var sourceEntity = source.getSource();
            if (!(sourceEntity instanceof ArrowEntity)) {
                return;
            }
            // If the blowing kill was done with a forager enchanted bow
            int foragerLevel = ((ForagerArrowEntityInterface) sourceEntity).getForagerLevel();
            if (source.getAttacker() instanceof LivingEntity && foragerLevel > 0) {
                // Play world sound
                entity.world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z,
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.AMBIENT, 0.75f, 1.0f);
                // Spawn the forager death cloud entity
                var deathCloud = new ForagerDeathCloudEntity(FORAGER_CLOUD_TYPE, entity.world);
                deathCloud.setOwner((LivingEntity)source.getAttacker());
                deathCloud.setForagerLevel(foragerLevel);
                deathCloud.setPosition(entity.getPos());
                deathCloud.setRadius(1.5F);
                deathCloud.setRadiusOnUse(-0.5F);
                deathCloud.setWaitTime(0);
                deathCloud.setDuration(30);;
                deathCloud.setColor(12321023);
                deathCloud.setRadiusGrowth(-deathCloud.getRadius() / (float)deathCloud.getDuration());
                entity.world.spawnEntity(deathCloud);
            }
        });
    }
}
