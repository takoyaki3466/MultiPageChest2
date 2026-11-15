package com.takoy3466.multipagechest.menu;

/**
 * Pixel layout of the chest screen, relative to the screen's top-left corner.
 * Values are the ones used by the 1.7.10 version (see docs/legacy-analysis.md) and are shared by the menu
 * (slot positions) and the screen (background, buttons, labels).
 */
public final class MenuLayout {

    public static final int IMAGE_WIDTH = 256;
    public static final int IMAGE_HEIGHT = 256;

    /** Distance between two neighbouring slots. */
    public static final int SLOT_PITCH = 18;
    /** Size of the item area inside a slot. */
    public static final int SLOT_ITEM_SIZE = 16;

    public static final int CHEST_GRID_X = 12;
    public static final int CHEST_GRID_Y = 8;

    public static final int PLAYER_INVENTORY_X = 48;
    public static final int PLAYER_INVENTORY_Y = 174;
    public static final int PLAYER_INVENTORY_ROWS = 3;
    public static final int PLAYER_INVENTORY_COLUMNS = 9;
    public static final int HOTBAR_Y = 232;

    public static final int TRASH_SLOT_X = 228;
    public static final int TRASH_SLOT_Y = 232;

    /** Page buttons sit outside the image on both sides, vertically aligned. */
    public static final int PAGE_BUTTON_SIZE = 20;
    public static final int PREVIOUS_BUTTON_X = -23;
    public static final int NEXT_BUTTON_X = 259;
    public static final int PAGE_BUTTON_Y = 78;

    /** Centre of the "current / total" page label. */
    public static final int PAGE_LABEL_CENTER_X = 229;
    public static final int PAGE_LABEL_Y = 179;

    private MenuLayout() {
    }
}
