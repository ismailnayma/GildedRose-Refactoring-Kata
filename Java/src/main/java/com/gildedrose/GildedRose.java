package com.gildedrose;

class GildedRose {
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (Item item : items) {
            updateItem(item);
        }
    }

    private static void updateItem(Item item) {
        refreshQuality(item);

        refreshExpiration(item);

        if (isExpired(item)) {
            updateExpired(item);
        }
    }

    private static void refreshQuality(Item item) {
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

    private static void refreshExpiration(Item item) {
        if (item.name.equals("Sulfuras, Hand of Ragnaros")) {
            return;
        }
        item.sellIn = item.sellIn - 1;
    }

    private static void updateExpired(Item item) {
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

    private static void decreaseQuality(Item item) {
        if (item.quality > 0) {
            item.quality = item.quality - 1;
        }
    }

    private static void increaseQuality(Item item) {
        if (item.quality < 50) {
            item.quality = item.quality + 1;
        }
    }

    private static boolean isExpired(Item item) {
        return item.sellIn < 0;
    }
}
