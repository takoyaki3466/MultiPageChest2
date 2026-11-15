package com.takoy3466.multipagechest.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * One-slot container that destroys whatever is put into it ("Destroy Item" slot of the 1.7.10 version).
 * It always reports itself as empty, so nothing can be taken back out and the slot never needs syncing.
 */
final class TrashContainer implements Container {

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    /** Discards the stack: this is the purpose of the trash slot. */
    @Override
    public void setItem(int slot, ItemStack stack) {
        // Intentionally not stored.
    }

    /** Nothing is stored, so there is nothing to mark as changed. */
    @Override
    public void setChanged() {
        // Intentionally stateless.
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /** Nothing is stored, so there is nothing to clear. */
    @Override
    public void clearContent() {
        // Intentionally stateless.
    }
}
