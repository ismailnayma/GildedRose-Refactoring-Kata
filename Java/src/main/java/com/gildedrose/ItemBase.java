package com.gildedrose;

public class ItemBase {

    private Item item;

    public ItemBase(Item item) {
        this.item = item;
    }

    public static ItemBase create(Item item) {
        return new ItemBase(item);
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
            case "Aged Brie":
                increaseQuality();
                break;
            case "Backstage passes to a TAFKAL80ETC concert":
                increaseQuality();
                if (item.sellIn < 11) {
                    increaseQuality();
                }
                if (item.sellIn < 6) {
                    increaseQuality();
                }
                break;
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
            case "Aged Brie":
                increaseQuality();
                break;
            case "Backstage passes to a TAFKAL80ETC concert":
                item.quality = 0;
                break;
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
