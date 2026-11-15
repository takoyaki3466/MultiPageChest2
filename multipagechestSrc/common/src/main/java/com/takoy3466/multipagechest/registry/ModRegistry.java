package com.takoy3466.multipagechest.registry;

import com.takoy3466.multipagechest.block.MultiPageChestBlock;
import com.takoy3466.multipagechest.block.entity.MultiPageChestBlockEntity;
import com.takoy3466.multipagechest.menu.MultiPageChestMenu;
import com.takoy3466.multipagechest.platform.RegistryPlatform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * Registers and exposes every game object of the mod.
 * Registration is explicit ({@link #register(RegistryPlatform)}) instead of relying on static initialisation
 * order, and each accessor fails loudly if it is used before registration.
 */
public final class ModRegistry {

    /** Registry path shared by the block, its item, its block entity type and its menu type. */
    public static final String CHEST_PATH = "multipagechest";

    private static Supplier<MultiPageChestBlock> chestBlock;
    private static Supplier<BlockItem> chestItem;
    private static Supplier<BlockEntityType<MultiPageChestBlockEntity>> chestBlockEntity;
    private static Supplier<MenuType<MultiPageChestMenu>> chestMenu;

    private ModRegistry() {
    }

    public static void register(RegistryPlatform platform) {
        if (chestBlock != null) {
            throw new IllegalStateException("MultiPageChest objects are already registered");
        }
        chestBlock = platform.register(BuiltInRegistries.BLOCK, CHEST_PATH,
                () -> new MultiPageChestBlock(MultiPageChestBlock.defaultProperties()));
        chestItem = platform.register(BuiltInRegistries.ITEM, CHEST_PATH,
                () -> new BlockItem(chestBlock.get(), new Item.Properties()));
        chestBlockEntity = platform.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, CHEST_PATH,
                () -> BlockEntityType.Builder.of(MultiPageChestBlockEntity::new, chestBlock.get()).build(null));
        chestMenu = platform.register(BuiltInRegistries.MENU, CHEST_PATH,
                () -> new MenuType<>(MultiPageChestMenu::new, FeatureFlags.VANILLA_SET));
    }

    public static MultiPageChestBlock chestBlock() {
        return registered(chestBlock, "block");
    }

    public static BlockItem chestItem() {
        return registered(chestItem, "item");
    }

    public static BlockEntityType<MultiPageChestBlockEntity> chestBlockEntity() {
        return registered(chestBlockEntity, "block entity type");
    }

    public static MenuType<MultiPageChestMenu> chestMenu() {
        return registered(chestMenu, "menu type");
    }

    private static <T> T registered(Supplier<T> supplier, String description) {
        if (supplier == null) {
            throw new IllegalStateException("MultiPageChest " + description + " accessed before registration");
        }
        return supplier.get();
    }
}
