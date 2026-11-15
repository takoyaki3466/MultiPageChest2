package com.takoy3466.multipagechest.menu;

import com.takoy3466.multipagechest.chest.PageLayout;
import com.takoy3466.multipagechest.chest.PageNavigation;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Menu of the multi page chest.
 * <p>
 * All {@link PageLayout#TOTAL_SLOTS} chest slots are always part of the menu (as in the 1.7.10 version);
 * only the slots of the current page are active. The current page belongs to this menu, i.e. to one player,
 * and is owned by the server: clients request a change through the vanilla container button packet
 * ({@link #clickMenuButton}) with a {@link PageNavigation} button id, and the result is synchronised back
 * through a data slot. No page number or position is ever accepted from the client.
 *
 * <h2>Slot indices</h2>
 * <ul>
 *     <li>{@code [0, 585)} chest, in chest slot order</li>
 *     <li>{@code [585, 612)} player inventory, {@code [612, 621)} hotbar</li>
 *     <li>{@code 621} trash slot</li>
 * </ul>
 */
public class MultiPageChestMenu extends AbstractContainerMenu {

    private static final int CHEST_SLOT_START = 0;
    private static final int CHEST_SLOT_END = CHEST_SLOT_START + PageLayout.TOTAL_SLOTS;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = 36;
    private static final int PLAYER_SLOT_START = CHEST_SLOT_END;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int TRASH_SLOT_INDEX = PLAYER_SLOT_END;
    /** Index of the first main-inventory slot in {@link Inventory} (0-8 is the hotbar). */
    private static final int INVENTORY_MAIN_START = 9;

    private final Container chest;
    private final DataSlot currentPage = new ValidatedPageSlot();

    /** Client-side constructor: the contents arrive through normal slot synchronisation. */
    public MultiPageChestMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, createClientContainer());
    }

    /** Server-side constructor, used by the block entity. */
    public MultiPageChestMenu(int containerId, Inventory playerInventory, Container chest) {
        super(ModRegistry.chestMenu(), containerId);
        checkContainerSize(chest, PageLayout.TOTAL_SLOTS);
        this.chest = chest;
        chest.startOpen(playerInventory.player);

        for (int chestSlot = 0; chestSlot < PageLayout.TOTAL_SLOTS; chestSlot++) {
            this.addSlot(new PagedChestSlot(chest, chestSlot, this::getCurrentPage));
        }
        this.addPlayerInventorySlots(playerInventory);
        this.addSlot(new Slot(new TrashContainer(), 0, MenuLayout.TRASH_SLOT_X, MenuLayout.TRASH_SLOT_Y));
        this.addDataSlot(this.currentPage);
    }

    /** Mirrors the server's 64 item limit so client-side click prediction matches the server. */
    private static Container createClientContainer() {
        return new SimpleContainer(PageLayout.TOTAL_SLOTS) {
            @Override
            public int getMaxStackSize() {
                return PageLayout.MAX_STACK_SIZE;
            }
        };
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < MenuLayout.PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < MenuLayout.PLAYER_INVENTORY_COLUMNS; column++) {
                this.addSlot(new Slot(playerInventory,
                        INVENTORY_MAIN_START + row * MenuLayout.PLAYER_INVENTORY_COLUMNS + column,
                        MenuLayout.PLAYER_INVENTORY_X + column * MenuLayout.SLOT_PITCH,
                        MenuLayout.PLAYER_INVENTORY_Y + row * MenuLayout.SLOT_PITCH));
            }
        }
        for (int column = 0; column < MenuLayout.PLAYER_INVENTORY_COLUMNS; column++) {
            this.addSlot(new Slot(playerInventory, column,
                    MenuLayout.PLAYER_INVENTORY_X + column * MenuLayout.SLOT_PITCH,
                    MenuLayout.HOTBAR_Y));
        }
    }

    public Container getContainer() {
        return this.chest;
    }

    public int getCurrentPage() {
        return this.currentPage.get();
    }

    public boolean isTrashSlot(Slot slot) {
        return slot.index == TRASH_SLOT_INDEX;
    }

    /**
     * Switches the page. Runs on the server for every button packet (after vanilla has checked the
     * container id and {@link #stillValid}) and on the client as a prediction.
     *
     * @return {@code false} for unknown button ids, which leaves the menu unchanged
     */
    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        Optional<PageNavigation> navigation = PageNavigation.fromButtonId(buttonId);
        if (navigation.isEmpty()) {
            return false;
        }
        this.currentPage.set(navigation.get().apply(this.getCurrentPage()));
        return true;
    }

    /**
     * Shift-click, as in the 1.7.10 version: chest stacks go to the player inventory (hotbar first);
     * player stacks go to the current page, and only if nothing fits there, to the following pages.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (index == TRASH_SLOT_INDEX || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        boolean moved = index < CHEST_SLOT_END
                ? this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)
                : this.moveIntoChest(stack);
        if (!moved) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    private boolean moveIntoChest(ItemStack stack) {
        int page = this.getCurrentPage();
        int pageEnd = CHEST_SLOT_START + PageLayout.endSlotOf(page);
        return this.moveItemStackTo(stack, CHEST_SLOT_START + PageLayout.firstSlotOf(page), pageEnd, false)
                || this.moveItemStackTo(stack, pageEnd, CHEST_SLOT_END, false);
    }

    /**
     * Double-click collection only takes from visible slots. This intentionally differs from the
     * 1.7.10 version, which also collected from hidden pages (see docs/legacy-analysis.md).
     */
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.isActive() && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.chest.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.chest.stopOpen(player);
    }

    /** Holds the current page and rejects any value outside the page range, including synchronised ones. */
    private static final class ValidatedPageSlot extends DataSlot {
        private int page = PageLayout.FIRST_PAGE;

        @Override
        public int get() {
            return this.page;
        }

        @Override
        public void set(int value) {
            if (!PageLayout.isValidPage(value)) {
                throw new IllegalArgumentException("Page out of range: " + value);
            }
            this.page = value;
        }
    }
}
