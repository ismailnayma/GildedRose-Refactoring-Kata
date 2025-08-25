package com.gildedrose;

public class AgedBrie extends ItemBase {
    public AgedBrie(Item item) {
        super(item);
    }

    @Override
    protected void refreshQuality() {
        increaseQuality();
    }
}
