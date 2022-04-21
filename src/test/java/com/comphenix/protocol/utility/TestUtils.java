package com.comphenix.protocol.utility;

import com.comphenix.protocol.reflect.accessors.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtils {

	public static void assertItemCollectionsEqual(List<ItemStack> first, List<ItemStack> second) {
		assertEquals(first.size(), second.size());
		for (int i = 0; i < first.size(); i++) {
			assertItemsEqual(first.get(i), second.get(i));
		}
	}

	public static void assertItemsEqual(ItemStack first, ItemStack second) {
		if (first == null) {
			assertNull(second);
		} else {
			assertNotNull(first);

			// The legacy check in ItemStack#isSimilar causes a null pointer
			assertEquals(first.getType(), second.getType());
			assertEquals(first.getDurability(), second.getDurability());
			assertEquals(first.hasItemMeta(), second.hasItemMeta());
			if (first.hasItemMeta()) {
				assertTrue(Bukkit.getItemFactory().equals(first.getItemMeta(), second.getItemMeta()));
			}
		}
	}

	public static boolean equivalentItem(ItemStack first, ItemStack second) {
		if (first == null) {
			return second == null;
		} else if (second == null) {
			return false;
		} else {
			return first.getType().equals(second.getType());
		}
	}

	public static void setFinalField(Object obj, Field field, Object newValue) {
		Accessors.getFieldAccessor(field, true).set(obj, newValue);
	}
}
