package com.takoy3466.multipagechest.client.screen;

import com.takoy3466.multipagechest.chest.PageLayout;
import com.takoy3466.multipagechest.chest.PageNavigation;
import com.takoy3466.multipagechest.menu.MenuLayout;
import com.takoy3466.multipagechest.menu.MultiPageChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * Screen of the multi page chest: page buttons on both sides, the "current / total" label and the trash
 * slot tooltip, laid out as in the 1.7.10 version. Like that version it draws no title or inventory label;
 * the title is still used for narration.
 * <p>
 * The screen never changes the page on its own authority: it asks the menu (client prediction) and sends
 * the same button id to the server, whose answer overwrites the local page.
 */
public class MultiPageChestScreen extends AbstractContainerScreen<MultiPageChestMenu> {

    private static final Component PREVIOUS_PAGE_LABEL = Component.literal("-");
    private static final Component NEXT_PAGE_LABEL = Component.literal("+");
    private static final Component TRASH_TOOLTIP = Component.translatable("gui.multipagechest.destroy_item");
    private static final String PAGE_LABEL_KEY = "gui.multipagechest.page";
    private static final int LABEL_COLOR = 0x404040;

    public MultiPageChestScreen(MultiPageChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = MenuLayout.IMAGE_WIDTH;
        this.imageHeight = MenuLayout.IMAGE_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.addPageButton(PageNavigation.PREVIOUS, PREVIOUS_PAGE_LABEL, MenuLayout.PREVIOUS_BUTTON_X);
        this.addPageButton(PageNavigation.NEXT, NEXT_PAGE_LABEL, MenuLayout.NEXT_BUTTON_X);
    }

    private void addPageButton(PageNavigation navigation, Component label, int relativeX) {
        this.addRenderableWidget(Button.builder(label, button -> this.requestPage(navigation))
                .bounds(this.leftPos + relativeX, this.topPos + MenuLayout.PAGE_BUTTON_Y,
                        MenuLayout.PAGE_BUTTON_SIZE, MenuLayout.PAGE_BUTTON_SIZE)
                .build());
    }

    /** Same pattern as the vanilla stonecutter and loom screens. */
    private void requestPage(PageNavigation navigation) {
        if (this.menu.clickMenuButton(this.minecraft.player, navigation.buttonId())) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, navigation.buttonId());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (this.hoveredSlot != null && this.menu.isTrashSlot(this.hoveredSlot)) {
            graphics.renderTooltip(this.font, TRASH_TOOLTIP, mouseX, mouseY);
        } else {
            this.renderTooltip(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ContainerBackgroundPainter.drawPanel(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        for (Slot slot : this.menu.slots) {
            if (!slot.isActive()) {
                continue;
            }
            int x = this.leftPos + slot.x;
            int y = this.topPos + slot.y;
            if (this.menu.isTrashSlot(slot)) {
                ContainerBackgroundPainter.drawTrashSlot(graphics, x, y, MenuLayout.SLOT_ITEM_SIZE);
            } else {
                ContainerBackgroundPainter.drawSlot(graphics, x, y, MenuLayout.SLOT_ITEM_SIZE);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        Component pageLabel = Component.translatable(PAGE_LABEL_KEY, this.menu.getCurrentPage() + 1, PageLayout.PAGE_COUNT);
        int x = MenuLayout.PAGE_LABEL_CENTER_X - this.font.width(pageLabel) / 2;
        graphics.drawString(this.font, pageLabel, x, MenuLayout.PAGE_LABEL_Y, LABEL_COLOR, false);
    }
}
