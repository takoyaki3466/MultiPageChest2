package com.takoy3466.multipagechest.platform;

import net.minecraft.core.Registry;

import java.util.function.Supplier;

/**
 * The only registration operation the shared code needs from a mod loader.
 * <p>
 * Fabric registers immediately, while Forge and NeoForge defer registration until their registry events.
 * Callers must therefore treat the returned supplier as unavailable until registration has happened
 * and must not call {@code factory.get()} themselves.
 */
public interface RegistryPlatform {

    /**
     * Registers an object under this mod's namespace.
     *
     * @param registry the vanilla registry that will hold the object
     * @param path     the path part of the identifier
     * @param factory  creates the object; called exactly once by the loader
     * @return a supplier of the registered object
     */
    <R, T extends R> Supplier<T> register(Registry<R> registry, String path, Supplier<T> factory);
}
