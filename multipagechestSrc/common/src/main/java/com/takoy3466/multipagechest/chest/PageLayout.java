package com.takoy3466.multipagechest.chest;

/**
 * Storage layout of a multi page chest, taken from the 1.7.10 version (see docs/legacy-analysis.md).
 * <p>
 * The chest stores one flat list of {@link #TOTAL_SLOTS} slots. A page is not separate storage but a
 * fixed window of {@link #SLOTS_PER_PAGE} consecutive slots, so switching pages never moves or copies items.
 * All page/slot conversions live here so that the menu, the screen and the storage agree on them.
 */
public final class PageLayout {

    public static final int PAGE_COUNT = 5;
    public static final int COLUMNS = 13;
    public static final int ROWS = 9;
    public static final int SLOTS_PER_PAGE = COLUMNS * ROWS;
    public static final int TOTAL_SLOTS = SLOTS_PER_PAGE * PAGE_COUNT;
    public static final int MAX_STACK_SIZE = 64;
    public static final int FIRST_PAGE = 0;
    public static final int LAST_PAGE = PAGE_COUNT - 1;

    private PageLayout() {
    }

    public static boolean isValidPage(int page) {
        return page >= FIRST_PAGE && page <= LAST_PAGE;
    }

    public static boolean isValidSlot(int slot) {
        return slot >= 0 && slot < TOTAL_SLOTS;
    }

    public static int pageOf(int slot) {
        requireValidSlot(slot);
        return slot / SLOTS_PER_PAGE;
    }

    /** Column of the slot inside its page, counted from the left. */
    public static int columnOf(int slot) {
        requireValidSlot(slot);
        return (slot % SLOTS_PER_PAGE) % COLUMNS;
    }

    /** Row of the slot inside its page, counted from the top. */
    public static int rowOf(int slot) {
        requireValidSlot(slot);
        return (slot % SLOTS_PER_PAGE) / COLUMNS;
    }

    /** First slot of the page (inclusive). */
    public static int firstSlotOf(int page) {
        requireValidPage(page);
        return page * SLOTS_PER_PAGE;
    }

    /** End of the page (exclusive). */
    public static int endSlotOf(int page) {
        return firstSlotOf(page) + SLOTS_PER_PAGE;
    }

    private static void requireValidSlot(int slot) {
        if (!isValidSlot(slot)) {
            throw new IllegalArgumentException("Slot out of range: " + slot);
        }
    }

    private static void requireValidPage(int page) {
        if (!isValidPage(page)) {
            throw new IllegalArgumentException("Page out of range: " + page);
        }
    }
}
