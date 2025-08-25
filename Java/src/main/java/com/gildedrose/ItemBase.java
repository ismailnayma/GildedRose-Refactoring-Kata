package com.gildedrose;

public class ItemBase {

    protected Item item;

    public static ItemBase create(Item item) {
        if (item.name.equals("Aged Brie")) {
            return new AgedBrie(item);
        }
        if(item.name.equals("Backstage passes to a TAFKAL80ETC concert")) {
            return new BackstagePasses(item);
        }

        return new ItemBase(item);
    }

    public ItemBase(Item item) {
        this.item = item;
    }

    public void updateItem() {
        refreshQuality();

        refreshExpiration();

        if (isExpired()) {
            updateExpired();
        }
    }

    protected void refreshQuality() {
        switch (item.name) {
            case "Sulfuras, Hand of Ragnaros":
                return;
            default:
                decreaseQuality();
                break;
        }
    }

    protected void refreshExpiration() {
        if (item.name.equals("Sulfuras, Hand of Ragnaros")) {
            return;
        }
        item.sellIn = item.sellIn - 1;
    }

    protected void updateExpired() {
        switch (item.name) {
            case "Sulfuras, Hand of Ragnaros":
                return;
            default:
                decreaseQuality();
                break;
        }
    }

    protected void increaseQuality() {
        if (item.quality < 50) {
            item.quality = item.quality + 1;
        }
    }

    protected void decreaseQuality() {
        if (item.quality > 0) {
            item.quality = item.quality - 1;
        }
    }

    protected boolean isExpired() {
        return item.sellIn < 0;
    }




}
