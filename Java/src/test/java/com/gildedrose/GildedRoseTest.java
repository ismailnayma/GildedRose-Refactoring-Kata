package com.gildedrose;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class GildedRoseTest {

    private static void runDays(GildedRose app, int days) {
        for (int i = 0; i < days; i++) app.updateQuality();
    }
    private static Item item(String name, int sellIn, int quality) {
        return new Item(name, sellIn, quality);
    }

    @Nested
    class BasicsAndRules {
        @Test
        void item_quality_degrades_by_one_with_1_day_left() {
            Item it = item("Standard Item", 1, 4);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(3, it.quality);
        }

        @Test
        void item_quality_degrades_down_to_0() {
            Item it = item("Standard Item", 4, 1);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(0, it.quality);
        }

        @Test
        void item_quality_is_never_negative() {
            Item it = item("Standard Item", 4, 0);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(0, it.quality);
        }

        @Test
        void sellin_decreases_by_one_each_day_for_non_legendary() {
            Item normal = item("Standard Item", 10, 20);
            Item brie = item("Aged Brie", 5, 10);
            Item backstage = item("Backstage passes to a TAFKAL80ETC concert", 12, 20);
            Item sulfuras = item("Sulfuras, Hand of Ragnaros", 0, 80);
            GildedRose subject = new GildedRose(new Item[]{normal, brie, backstage, sulfuras});
            runDays(subject, 3);
            assertEquals(7, normal.sellIn);
            assertEquals(2, brie.sellIn);
            assertEquals(9, backstage.sellIn);
            assertEquals(0, sulfuras.sellIn);
        }

        @Test
        void long_run_boundaries_hold_for_all_non_legendary() {
            Item[] items = new Item[]{
                item("Standard Item", 3, 6),
                item("Aged Brie", 2, 48),
                item("Backstage passes to a TAFKAL80ETC concert", 12, 49),
                item("Sulfuras, Hand of Ragnaros", 0, 80),
                item("Standard Item", 0, 1),
                item("Aged Brie", 0, 49),
                item("Backstage passes to a TAFKAL80ETC concert", 1, 49)
            };
            GildedRose subject = new GildedRose(items);
            runDays(subject, 40);
            for (Item it : items) {
                if (it.name.startsWith("Sulfuras")) {
                    assertEquals(80, it.quality);
                } else {
                    assertTrue(it.quality >= 0 && it.quality <= 50);
                }
            }
        }
    }

    @Nested
    class NormalItem {
        @Test
        void normal_item_degrades_twice_as_fast_after_sell_date() {
            Item it = item("Standard Item", 0, 7);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(-1, it.sellIn);
            assertEquals(5, it.quality);
        }

        @ParameterizedTest
        @CsvSource({
            "2, 2, 1, 1",
            "2, 2, 2, 0",
            "0, 6, 1, 4",
            "-1, 6, 2, 2"
        })
        void normal_item_multi_day_progression(int sellIn, int quality, int days, int expectedQuality) {
            Item it = item("Standard Item", sellIn, quality);
            GildedRose subject = new GildedRose(new Item[]{it});
            runDays(subject, days);
            assertEquals(expectedQuality, it.quality);
            assertTrue(it.quality >= 0 && it.quality <= 50);
        }

        @Test
        void normal_item_never_goes_below_zero_in_long_run() {
            Item it = item("Standard Item", 1, 1);
            GildedRose subject = new GildedRose(new Item[]{it});
            runDays(subject, 10);
            assertEquals(-9, it.sellIn);
            assertEquals(0, it.quality);
        }
    }

    @Nested
    class AgedBrie {
        @Test
        void aged_items_increase_in_quality_over_time() {
            Item it = item("Aged Brie", 5, 6);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(7, it.quality);
        }

        @Test
        void aged_item_quality_49_increases_up_to_50() {
            Item it = item("Aged Brie", 5, 49);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(50, it.quality);
        }

        @Test
        void quality_of_an_item_is_never_greater_than_50() {
            Item it = item("Aged Brie", 5, 50);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(50, it.quality);
        }

        @Test
        void aged_item_quality_increases_twice_as_fast_past_sellin_date() {
            Item it = item("Aged Brie", 0, 6);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(8, it.quality);
        }

        @Test
        void aged_item_quality_50_past_sellin_date_does_not_increase() {
            Item it = item("Aged Brie", 0, 50);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(50, it.quality);
        }

        @ParameterizedTest
        @CsvSource({
            "1, 48, 1, 49",
            "1, 49, 1, 50",
            "0, 49, 1, 50",
            "0, 48, 2, 50"
        })
        void aged_brie_parameterized_caps_and_acceleration(int sellIn, int quality, int days, int expectedQuality) {
            Item it = item("Aged Brie", sellIn, quality);
            GildedRose subject = new GildedRose(new Item[]{it});
            runDays(subject, days);
            assertEquals(expectedQuality, it.quality);
            assertTrue(it.quality <= 50);
        }

        @Test
        void aged_brie_capped_in_long_run() {
            Item it = item("Aged Brie", 3, 49);
            GildedRose subject = new GildedRose(new Item[]{it});
            runDays(subject, 10);
            assertEquals(50, it.quality);
            assertTrue(it.sellIn < 0);
        }
    }

    @Nested
    class BackstagePasses {
        @Test
        void backstage_passes_increase_in_quality_as_sellIn_date_approaches() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 15, 20);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(21, it.quality);
        }

        @Test
        void backstage_passes_increase_in_quality_by_1_when_there_are_10_days_or_less() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 11, 48);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(49, it.quality);
        }

        @Test
        void backstage_passes_increase_in_quality_by_2_when_there_are_10_days_or_less() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 10, 20);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(22, it.quality);
        }

        @Test
        void backstage_passes_quality_49_increase_up_to_50_when_there_are_10_days_or_less() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 10, 49);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(50, it.quality);
        }

        @Test
        void backstage_passes_increase_in_quality_by_2_when_there_are_6_days_or_less() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 6, 46);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(48, it.quality);
        }

        @Test
        void backstage_passes_increase_in_quality_by_3_when_there_are_5_days_or_less() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 5, 20);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(23, it.quality);
        }

        @Test
        void backstage_passes_quality_47_increase_up_to_50_when_there_are_5_days_or_less() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 5, 47);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(50, it.quality);
        }

        @Test
        void backstage_passes_quality_49_increase_up_to_50_when_there_are_5_days_or_less() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 5, 49);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(50, it.quality);
        }

        @Test
        void backstage_passes_quality_is_0_after_concert() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 0, 20);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(0, it.quality);
        }

        @ParameterizedTest
        @CsvSource({
            "15, 20, 1",
            "11, 20, 1",
            "10, 20, 2",
            "7, 20, 2",
            "6, 20, 2",
            "5, 20, 3",
            "1, 20, 3"
        })
        void backstage_stepwise_increase(int sellIn, int startQ, int expectedDelta) {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", sellIn, startQ);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            int expected = Math.min(50, startQ + expectedDelta);
            assertEquals(sellIn - 1, it.sellIn);
            assertEquals(expected, it.quality);
        }

        @Test
        void backstage_boundary_change_11_to_10() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 11, 30);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(10, it.sellIn);
            assertEquals(31, it.quality);
            subject.updateQuality();
            assertEquals(9, it.sellIn);
            assertEquals(33, it.quality);
        }

        @Test
        void backstage_boundary_change_6_to_5() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 6, 40);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(5, it.sellIn);
            assertEquals(42, it.quality);
            subject.updateQuality();
            assertEquals(4, it.sellIn);
            assertEquals(45, it.quality);
        }

        @Test
        void backstage_multi_day_drop_to_zero() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 2, 44);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(1, it.sellIn);
            assertEquals(47, it.quality);
            subject.updateQuality();
            assertEquals(0, it.sellIn);
            assertEquals(50, it.quality);
            subject.updateQuality();
            assertEquals(-1, it.sellIn);
            assertEquals(0, it.quality);
        }

        @Test
        void backstage_quality_50_stays_50_even_if_increase_should_apply() {
            Item it = item("Backstage passes to a TAFKAL80ETC concert", 7, 50);
            new GildedRose(new Item[]{it}).updateQuality();
            assertEquals(6, it.sellIn);
            assertEquals(50, it.quality);
        }

    }

    @Nested
    class Sulfuras {
        @Test
        void sulfuras_never_changes() {
            Item it = item("Sulfuras, Hand of Ragnaros", 0, 80);
            GildedRose subject = new GildedRose(new Item[]{it});
            runDays(subject, 30);
            assertEquals(0, it.sellIn);
            assertEquals(80, it.quality);
        }

        @Test
        void sulfuras_negative_sellin_constant() {
            Item it = item("Sulfuras, Hand of Ragnaros", -5, 80);
            GildedRose subject = new GildedRose(new Item[]{it});
            subject.updateQuality();
            assertEquals(-5, it.sellIn);
            assertEquals(80, it.quality);
        }
    }

    @Nested
    class MixedInventoryAndOddInputs {
        @Test
        void mixed_inventory_independent_evolution() {
            Item normal = item("Standard Item", 10, 20);
            Item brie   = item("Aged Brie", 2, 0);
            Item pass   = item("Backstage passes to a TAFKAL80ETC concert", 15, 20);
            Item sulf   = item("Sulfuras, Hand of Ragnaros", 0, 80);
            GildedRose subject = new GildedRose(new Item[]{normal, brie, pass, sulf});
            runDays(subject, 5);
            assertEquals(5, normal.sellIn);
            assertEquals(15, normal.quality);
            assertEquals(-3, brie.sellIn);
            assertTrue(brie.quality >= 0 && brie.quality <= 50);
            assertEquals(10, pass.sellIn);
            assertEquals(25, pass.quality);
            assertEquals(0, sulf.sellIn);
            assertEquals(80, sulf.quality);
        }

        @Test
        void normal_item_starting_above_50_decreases() {
            Item it = item("Standard Item", 5, 55);
            GildedRose subject = new GildedRose(new Item[]{it});
            runDays(subject, 2);
            assertEquals(53, it.quality);
        }

        @Test
        void brie_starting_above_50_does_not_increase() {
            Item it = item("Aged Brie", 5, 55);
            GildedRose subject = new GildedRose(new Item[]{it});
            runDays(subject, 3);
            assertTrue(it.quality <= 55);
        }
    }

    @Nested
    class Conjured {
        @Test
        void conjured_degrades_twice_as_fast_before_sell_date() {
            Item it = item("Conjured Mana Cake", 3, 10);
            new GildedRose(new Item[]{it}).updateQuality();
            assertEquals(2, it.sellIn);
            assertEquals(8, it.quality);
        }

        @Test
        void conjured_degrades_four_after_sell_date() {
            Item it = item("Conjured Mana Cake", 0, 10);
            new GildedRose(new Item[]{it}).updateQuality();
            assertEquals(-1, it.sellIn);
            assertEquals(6, it.quality);
        }

        @Test
        void conjured_never_goes_negative() {
            Item it = item("Conjured Mana Cake", 0, 3);
            GildedRose app = new GildedRose(new Item[]{it});
            runDays(app, 2);
            assertEquals(-2, it.sellIn);
            assertEquals(0, it.quality);
        }
    }


}
