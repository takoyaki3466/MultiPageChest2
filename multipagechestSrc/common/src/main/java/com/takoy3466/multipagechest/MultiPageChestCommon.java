package com.takoy3466.multipagechest;

import com.takoy3466.multipagechest.platform.RegistryPlatform;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * Loader-independent entry point.
 * Each loader calls {@link #init(RegistryPlatform)} exactly once while the mod is being constructed.
 */
public final class MultiPageChestCommon {

    public static final String MOD_ID = "multipagechest";

    private MultiPageChestCommon() {
    }

    public static void init(RegistryPlatform registryPlatform) {
        ModRegistry.register(registryPlatform);
    }

    /** Creates an identifier in this mod's namespace. */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
