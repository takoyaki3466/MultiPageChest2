package com.takoy3466.multipagechest.neoforge;

import com.takoy3466.multipagechest.MultiPageChestCommon;
import com.takoy3466.multipagechest.platform.RegistryPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Collects registrations into one {@link DeferredRegister} per registry. {@link #registerAll(IEventBus)}
 * must be called after the shared code has registered everything.
 */
final class NeoForgeRegistryPlatform implements RegistryPlatform {

    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> registers = new LinkedHashMap<>();

    @Override
    public <R, T extends R> Supplier<T> register(Registry<R> registry, String path, Supplier<T> factory) {
        return this.registerFor(registry).register(path, factory);
    }

    void registerAll(IEventBus modEventBus) {
        this.registers.values().forEach(register -> register.register(modEventBus));
    }

    // Safe: entries are only ever stored under the key of the registry they were created for.
    @SuppressWarnings("unchecked")
    private <R> DeferredRegister<R> registerFor(Registry<R> registry) {
        return (DeferredRegister<R>) this.registers.computeIfAbsent(registry.key(),
                key -> DeferredRegister.create(registry, MultiPageChestCommon.MOD_ID));
    }
}
