package com.takoy3466.multipagechest.menu;

import com.takoy3466.multipagechest.chest.PageLayout;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import java.util.function.IntSupplier;

/**
 * A chest slot that belongs to one page. It is only active while that page is shown, so the vanilla
 * container screen neither draws it nor lets the player hover or click it on other pages.
 * Its position is the same on every page.
 */
final class PagedChestSlot extends Slot {

    private final int page;
    private final IntSupplier currentPage;

    PagedChestSlot(Container chest, int chestSlot, IntSupplier currentPage) {
        super(chest, chestSlot,
                MenuLayout.CHEST_GRID_X + PageLayout.columnOf(chestSlot) * MenuLayout.SLOT_PITCH,
                MenuLayout.CHEST_GRID_Y + PageLayout.rowOf(chestSlot) * MenuLayout.SLOT_PITCH);
        this.page = PageLayout.pageOf(chestSlot);
        this.currentPage = currentPage;
    }

    @Override
    public boolean isActive() {
        return this.currentPage.getAsInt() == this.page;
    }
}
