package com.takoy3466.multipagechest.fabric;

import com.takoy3466.multipagechest.MultiPageChestCommon;
import com.takoy3466.multipagechest.platform.RegistryPlatform;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

/** Fabric registers directly into the vanilla registries during mod initialisation. */
final class FabricRegistryPlatform implements RegistryPlatform {

    @Override
    public <R, T extends R> Supplier<T> register(Registry<R> registry, String path, Supplier<T> factory) {
        T registered = Registry.register(registry, MultiPageChestCommon.id(path), factory.get());
        return () -> registered;
    }
}
