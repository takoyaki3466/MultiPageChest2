package com.takoy3466.multipagechest.block.entity;

import com.takoy3466.multipagechest.chest.PageLayout;
import com.takoy3466.multipagechest.menu.MultiPageChestMenu;
import com.takoy3466.multipagechest.registry.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Owns the 585 stacks of one chest and is the single source of truth for them.
 * <p>
 * This class deliberately does not extend {@code BaseContainerBlockEntity}: in 1.21.1 that class exports the
 * contents as an {@code ItemContainerContents} component whenever its components are collected (for example by
 * a {@code copy_components} loot function), and that component throws if any stack sits at slot 256 or later.
 * The 1.7.10 chest had no custom names or locks, so nothing else from that class is needed.
 * The contents are also not sent to clients ({@code getUpdateTag} is not overridden); open menus sync their slots.
 * <p>
 * Opening/closing follows the vanilla chest: an openers counter plays the sounds and sends block event
 * {@link #EVENT_SET_OPEN_COUNT} so clients can animate the lid.
 */
public class MultiPageChestBlockEntity extends BlockEntity implements Container, MenuProvider, LidBlockEntity {

    private static final int EVENT_SET_OPEN_COUNT = 1;
    /** Volume and pitch range used by the 1.7.10 version (and the vanilla chest). */
    private static final float SOUND_VOLUME = 0.5F;
    private static final float SOUND_PITCH_BASE = 0.9F;
    private static final float SOUND_PITCH_VARIATION = 0.1F;
    private static final Component TITLE = Component.translatable("container.multipagechest");

    private final NonNullList<ItemStack> items = NonNullList.withSize(PageLayout.TOTAL_SLOTS, ItemStack.EMPTY);
    private final ChestLidController lidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, SoundEvents.CHEST_OPEN);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, SoundEvents.CHEST_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int previousCount, int newCount) {
            level.blockEvent(pos, state.getBlock(), EVENT_SET_OPEN_COUNT, newCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof MultiPageChestMenu menu
                    && menu.getContainer() == MultiPageChestBlockEntity.this;
        }
    };

    public MultiPageChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.chestBlockEntity(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MultiPageChestBlockEntity chest) {
        chest.lidController.tickLid();
    }

    private static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        float pitch = level.random.nextFloat() * SOUND_PITCH_VARIATION + SOUND_PITCH_BASE;
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                sound, SoundSource.BLOCKS, SOUND_VOLUME, pitch);
    }

    // ---- persistence -------------------------------------------------------------------------------------

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        ChestItemsNbt.load(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ChestItemsNbt.save(tag, this.items, registries);
    }

    // ---- Container ---------------------------------------------------------------------------------------

    @Override
    public int getContainerSize() {
        return PageLayout.TOTAL_SLOTS;
    }

    @Override
    public int getMaxStackSize() {
        return PageLayout.MAX_STACK_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);
        if (!removed.isEmpty()) {
            this.setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    /** Called from the block's scheduled tick. */
    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    // ---- menu and lid ------------------------------------------------------------------------------------

    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MultiPageChestMenu(containerId, playerInventory, this);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == EVENT_SET_OPEN_COUNT) {
            this.lidController.shouldBeOpen(type > 0);
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return this.lidController.getOpenness(partialTicks);
    }
}
