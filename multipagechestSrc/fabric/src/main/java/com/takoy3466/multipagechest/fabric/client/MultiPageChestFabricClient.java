package com.takoy3466.multipagechest.fabric.client;

import com.takoy3466.multipagechest.client.render.MultiPageChestRenderer;
import com.takoy3466.multipagechest.client.screen.MultiPageChestScreen;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

/**
 * Client registration. Both vanilla methods are private in 1.21.1 and are opened by
 * {@code multipagechest.accesswidener}; Fabric API's own BlockEntityRendererRegistry is deprecated.
 */
public final class MultiPageChestFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModRegistry.chestMenu(), MultiPageChestScreen::new);
        BlockEntityRenderers.register(ModRegistry.chestBlockEntity(), MultiPageChestRenderer::new);
    }
}
