package com.takoy3466.multipagechest.forge.client;

import com.takoy3466.multipagechest.client.render.MultiPageChestRenderer;
import com.takoy3466.multipagechest.client.screen.MultiPageChestScreen;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Client-only registration; referenced only when running on the physical client.
 * Forge 52 has no menu screen registration event, so the screen is registered during client setup
 * ({@code MenuScreens#register} is made public by Forge's access transformer).
 */
public final class MultiPageChestForgeClient {

    private MultiPageChestForgeClient() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(MultiPageChestForgeClient::onClientSetup);
        modEventBus.addListener(MultiPageChestForgeClient::registerRenderers);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        // MenuScreens keeps a plain map, so registration is moved to the main thread.
        event.enqueueWork(() -> MenuScreens.register(ModRegistry.chestMenu(), MultiPageChestScreen::new));
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModRegistry.chestBlockEntity(), MultiPageChestRenderer::new);
    }
}
