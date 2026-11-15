package com.takoy3466.multipagechest.fabric;

import com.takoy3466.multipagechest.MultiPageChestCommon;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public final class MultiPageChestFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MultiPageChestCommon.init(new FabricRegistryPlatform());
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(entries -> entries.accept(ModRegistry.chestItem()));
    }
}
