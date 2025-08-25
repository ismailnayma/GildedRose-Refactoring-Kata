package com.gildedrose;

public class ItemBase {

    protected Item item;

    public static ItemBase create(Item item) {
        switch (item.name) {
            case "Aged Brie":
                return new AgedBrie(item);
            case "Backstage passes to a TAFKAL80ETC concert":
                return new BackstagePasses(item);
            case "Sulfuras, Hand of Ragnaros":
                return new Sulfuras(item);
            case "Conjured Mana Cake":
                return new Conjured(item);
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
        decreaseQuality();
    }

    protected void refreshExpiration() {
        item.sellIn--;
    }

    protected void updateExpired() {
        decreaseQuality();
    }

    protected void increaseQuality() {
        if (item.quality < 50) {
            item.quality++;
        }
    }

    protected void decreaseQuality() {
        if (item.quality > 0) {
            item.quality--;
        }
    }

    protected boolean isExpired() {
        return item.sellIn < 0;
    }




}
