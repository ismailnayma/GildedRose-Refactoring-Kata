package com.gildedrose;

public class BackstagePasses extends ItemBase {
    public BackstagePasses(Item item) {
        super(item);
    }

    @Override
    protected void refreshQuality() {
        increaseQuality();
        if (item.sellIn < 11) {
            increaseQuality();
        }
        if (item.sellIn < 6) {
            increaseQuality();
        }
    }

    @Override
    protected void updateExpired() {
        item.quality = 0;
    }


}
