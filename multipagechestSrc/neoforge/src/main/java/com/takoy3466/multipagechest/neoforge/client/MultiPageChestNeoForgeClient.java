package com.takoy3466.multipagechest.neoforge.client;

import com.takoy3466.multipagechest.client.render.MultiPageChestRenderer;
import com.takoy3466.multipagechest.client.screen.MultiPageChestScreen;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only registration; referenced only when running on the physical client. */
public final class MultiPageChestNeoForgeClient {

    private MultiPageChestNeoForgeClient() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegisterMenuScreensEvent.class, MultiPageChestNeoForgeClient::registerScreens);
        modEventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, MultiPageChestNeoForgeClient::registerRenderers);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModRegistry.chestMenu(), MultiPageChestScreen::new);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModRegistry.chestBlockEntity(), MultiPageChestRenderer::new);
    }
}
