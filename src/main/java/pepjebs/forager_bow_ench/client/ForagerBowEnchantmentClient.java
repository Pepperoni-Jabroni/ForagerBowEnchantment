package pepjebs.forager_bow_ench.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import pepjebs.forager_bow_ench.ForagerBowEnchantmentMod;
import pepjebs.forager_bow_ench.entity.ForagerDeathCloudEntityRenderer;

public class ForagerBowEnchantmentClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                ForagerBowEnchantmentMod.FORAGER_CLOUD_TYPE,
                ForagerDeathCloudEntityRenderer::new);
    }
}
