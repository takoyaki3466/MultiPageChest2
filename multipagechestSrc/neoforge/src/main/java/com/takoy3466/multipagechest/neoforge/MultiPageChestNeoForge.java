package com.takoy3466.multipagechest.neoforge;

import com.takoy3466.multipagechest.MultiPageChestCommon;
import com.takoy3466.multipagechest.neoforge.client.MultiPageChestNeoForgeClient;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(MultiPageChestCommon.MOD_ID)
public final class MultiPageChestNeoForge {

    public MultiPageChestNeoForge(IEventBus modEventBus, Dist dist) {
        NeoForgeRegistryPlatform registryPlatform = new NeoForgeRegistryPlatform();
        MultiPageChestCommon.init(registryPlatform);
        registryPlatform.registerAll(modEventBus);

        modEventBus.addListener(BuildCreativeModeTabContentsEvent.class, MultiPageChestNeoForge::addToCreativeTab);
        // The client class is only loaded on the physical client.
        if (dist.isClient()) {
            MultiPageChestNeoForgeClient.register(modEventBus);
        }
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModRegistry.chestItem());
        }
    }
}
