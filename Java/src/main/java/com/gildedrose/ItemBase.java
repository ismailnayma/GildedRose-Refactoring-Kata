package com.gildedrose;

public class ItemBase {

    private Item item;

    public ItemBase(Item item) {
    }

    public void updateItem(Item item) {
        refreshQuality(item);

        refreshExpiration(item);

        if (isExpired(item)) {
            updateExpired(item);
        }
    }

    protected void refreshQuality(Item item) {
        switch (item.name) {
            case "Aged Brie":
                increaseQuality(item);
                break;
            case "Backstage passes to a TAFKAL80ETC concert":
                increaseQuality(item);
                if (item.sellIn < 11) {
                    increaseQuality(item);
                }
                if (item.sellIn < 6) {
                    increaseQuality(item);
                }
                break;
            case "Sulfuras, Hand of Ragnaros":
                return;
            default:
                decreaseQuality(item);
                break;
        }
    }

    protected void refreshExpiration(Item item) {
        if (item.name.equals("Sulfuras, Hand of Ragnaros")) {
            return;
        }
        item.sellIn = item.sellIn - 1;
    }

    protected void updateExpired(Item item) {
        switch (item.name) {
            case "Aged Brie":
                increaseQuality(item);
                break;
            case "Backstage passes to a TAFKAL80ETC concert":
                item.quality = 0;
                break;
            case "Sulfuras, Hand of Ragnaros":
                return;
            default:
                decreaseQuality(item);
                break;
        }
    }

    protected void increaseQuality(Item item) {
        if (item.quality < 50) {
            item.quality = item.quality + 1;
        }
    }

    protected void decreaseQuality(Item item) {
        if (item.quality > 0) {
            item.quality = item.quality - 1;
        }
    }

    protected boolean isExpired(Item item) {
        return item.sellIn < 0;
    }




}
