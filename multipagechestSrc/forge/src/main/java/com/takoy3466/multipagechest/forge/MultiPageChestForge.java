package com.takoy3466.multipagechest.forge;

import com.takoy3466.multipagechest.MultiPageChestCommon;
import com.takoy3466.multipagechest.forge.client.MultiPageChestForgeClient;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(MultiPageChestCommon.MOD_ID)
public final class MultiPageChestForge {

    public MultiPageChestForge(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ForgeRegistryPlatform registryPlatform = new ForgeRegistryPlatform();
        MultiPageChestCommon.init(registryPlatform);
        registryPlatform.registerAll(modEventBus);

        modEventBus.addListener(MultiPageChestForge::addToCreativeTab);
        // The client class is only loaded on the physical client.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MultiPageChestForgeClient.register(modEventBus);
        }
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModRegistry.chestItem());
        }
    }
}
