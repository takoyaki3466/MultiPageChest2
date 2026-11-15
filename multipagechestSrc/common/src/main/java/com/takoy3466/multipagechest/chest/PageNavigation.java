package com.takoy3466.multipagechest.chest;

import java.util.Optional;

/**
 * Page switching actions. The ordinal-independent {@link #buttonId()} is what the client sends to the
 * server (vanilla container button click), so it must stay stable.
 * Both directions wrap around, as in the 1.7.10 version (first page "-" goes to the last page and vice versa).
 */
public enum PageNavigation {
    PREVIOUS(0, -1),
    NEXT(1, 1);

    private final int buttonId;
    private final int step;

    PageNavigation(int buttonId, int step) {
        this.buttonId = buttonId;
        this.step = step;
    }

    public int buttonId() {
        return this.buttonId;
    }

    /**
     * @param currentPage a valid page
     * @return the page reached by this action
     */
    public int apply(int currentPage) {
        if (!PageLayout.isValidPage(currentPage)) {
            throw new IllegalArgumentException("Page out of range: " + currentPage);
        }
        return Math.floorMod(currentPage + this.step, PageLayout.PAGE_COUNT);
    }

    /** Resolves a button id received from a client; unknown ids are rejected. */
    public static Optional<PageNavigation> fromButtonId(int buttonId) {
        for (PageNavigation navigation : values()) {
            if (navigation.buttonId == buttonId) {
                return Optional.of(navigation);
            }
        }
        return Optional.empty();
    }
}
