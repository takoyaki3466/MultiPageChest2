package com.takoy3466.multipagechest.block.entity;

import com.mojang.logging.LogUtils;
import com.takoy3466.multipagechest.chest.PageLayout;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

/**
 * Reads and writes the chest contents.
 * <p>
 * The format matches the 1.7.10 version: an {@code Items} list whose entries carry a {@code Slot} index
 * stored as a short plus the item stack. Vanilla's {@code ContainerHelper} cannot be used because it stores
 * the slot as a byte and therefore only supports 256 slots.
 */
final class ChestItemsNbt {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ITEMS_KEY = "Items";
    private static final String SLOT_KEY = "Slot";

    private ChestItemsNbt() {
    }

    static void save(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        ListTag entries = new ListTag();
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                CompoundTag entry = new CompoundTag();
                entry.putShort(SLOT_KEY, (short) slot);
                entries.add(stack.save(registries, entry));
            }
        }
        tag.put(ITEMS_KEY, entries);
    }

    /**
     * Fills {@code items} (which must have {@link PageLayout#TOTAL_SLOTS} elements) from {@code tag}.
     * Any numeric slot type is accepted; entries with an out-of-range slot are skipped and logged,
     * because there is no slot that could hold them.
     */
    static void load(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        ListTag entries = tag.getList(ITEMS_KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            int slot = entry.getInt(SLOT_KEY);
            if (entry.contains(SLOT_KEY, Tag.TAG_ANY_NUMERIC) && PageLayout.isValidSlot(slot)) {
                items.set(slot, ItemStack.parseOptional(registries, entry));
            } else {
                LOGGER.warn("Discarding multi page chest entry with invalid slot: {}", entry);
            }
        }
    }
}
