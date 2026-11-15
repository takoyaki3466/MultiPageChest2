package com.takoy3466.multipagechest.client.screen;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Draws a vanilla-styled container background with plain fills.
 * The screen is drawn in code instead of from a texture because the 1.7.10 GUI texture cannot be reused
 * (its licence is unknown) and the layout is a simple grid.
 */
final class ContainerBackgroundPainter {

    private static final int OUTLINE = 0xFF000000;
    private static final int PANEL = 0xFFC6C6C6;
    private static final int PANEL_HIGHLIGHT = 0xFFFFFFFF;
    private static final int PANEL_SHADOW = 0xFF555555;
    private static final int SLOT_FILL = 0xFF8B8B8B;
    private static final int SLOT_SHADOW = 0xFF373737;
    private static final int SLOT_HIGHLIGHT = 0xFFFFFFFF;
    private static final int TRASH_FILL = 0xFFAA7C7C;
    private static final int TRASH_CROSS = 0xFF3F1F1F;
    /** Width of the panel's bevel in pixels. */
    private static final int BEVEL = 2;
    /** Space kept between the cross and the slot edge. */
    private static final int CROSS_MARGIN = 2;

    private ContainerBackgroundPainter() {
    }

    static void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, OUTLINE);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, PANEL_SHADOW);
        graphics.fill(x + 1, y + 1, x + width - 1 - BEVEL, y + height - 1 - BEVEL, PANEL_HIGHLIGHT);
        graphics.fill(x + 1 + BEVEL, y + 1 + BEVEL, x + width - 1 - BEVEL, y + height - 1 - BEVEL, PANEL);
    }

    /**
     * Draws the sunken frame of one slot.
     *
     * @param itemX left edge of the 16x16 item area (the slot's x)
     * @param itemY top edge of the 16x16 item area (the slot's y)
     */
    static void drawSlot(GuiGraphics graphics, int itemX, int itemY, int itemSize) {
        drawSlot(graphics, itemX, itemY, itemSize, SLOT_FILL);
    }

    /** Draws a slot with a reddish fill and a cross, marking that items placed there are destroyed. */
    static void drawTrashSlot(GuiGraphics graphics, int itemX, int itemY, int itemSize) {
        drawSlot(graphics, itemX, itemY, itemSize, TRASH_FILL);
        int crossSize = itemSize - 2 * CROSS_MARGIN;
        int left = itemX + CROSS_MARGIN;
        int top = itemY + CROSS_MARGIN;
        for (int i = 0; i < crossSize; i++) {
            graphics.fill(left + i, top + i, left + i + 1, top + i + 1, TRASH_CROSS);
            graphics.fill(left + crossSize - 1 - i, top + i, left + crossSize - i, top + i + 1, TRASH_CROSS);
        }
    }

    private static void drawSlot(GuiGraphics graphics, int itemX, int itemY, int itemSize, int fill) {
        int left = itemX - 1;
        int top = itemY - 1;
        int right = itemX + itemSize + 1;
        int bottom = itemY + itemSize + 1;
        graphics.fill(left, top, right, bottom, SLOT_HIGHLIGHT);
        graphics.fill(left, top, right - 1, bottom - 1, SLOT_SHADOW);
        graphics.fill(itemX, itemY, itemX + itemSize, itemY + itemSize, fill);
    }
}
