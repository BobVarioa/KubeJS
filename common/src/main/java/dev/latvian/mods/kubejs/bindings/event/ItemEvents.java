package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.item.FoodEatenEventJS;
import dev.latvian.mods.kubejs.item.ItemCraftedEventJS;
import dev.latvian.mods.kubejs.item.ItemDroppedEventJS;
import dev.latvian.mods.kubejs.item.ItemEntityInteractedEventJS;
import dev.latvian.mods.kubejs.item.ItemLeftClickedEventJS;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesEventJS;
import dev.latvian.mods.kubejs.item.ItemModificationEventJS;
import dev.latvian.mods.kubejs.item.ItemPickedUpEventJS;
import dev.latvian.mods.kubejs.item.ItemRightClickedEmptyEventJS;
import dev.latvian.mods.kubejs.item.ItemRightClickedEventJS;
import dev.latvian.mods.kubejs.item.ItemSmeltedEventJS;
import dev.latvian.mods.kubejs.item.ItemTooltipEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemArmorTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public interface ItemEvents {
	EventGroup GROUP = EventGroup.of("ItemEvents");

	Extra SUPPORTS_ITEM = new Extra().transformer(ItemEvents::transformItem).identity();

	private static Object transformItem(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof ItemLike item) {
			return item.asItem();
		}

		var id = ResourceLocation.tryParse(o.toString());
		var item = id == null ? null : ItemWrapper.getItem(id);
		return item == Items.AIR ? null : item;
	}

	EventHandler MODIFICATION = GROUP.startup("modification", () -> ItemModificationEventJS.class);
	EventHandler TOOL_TIER_REGISTRY = GROUP.startup("toolTierRegistry", () -> ItemToolTierRegistryEventJS.class);
	EventHandler ARMOR_TIER_REGISTRY = GROUP.startup("armorTierRegistry", () -> ItemArmorTierRegistryEventJS.class);
	EventHandler RIGHT_CLICKED = GROUP.server("rightClicked", () -> ItemRightClickedEventJS.class).extra(SUPPORTS_ITEM).cancelable();
	EventHandler CAN_PICK_UP = GROUP.server("canPickUp", () -> ItemPickedUpEventJS.class).extra(SUPPORTS_ITEM).cancelable();
	EventHandler PICKED_UP = GROUP.server("pickedUp", () -> ItemPickedUpEventJS.class).extra(SUPPORTS_ITEM);
	EventHandler DROPPED = GROUP.server("dropped", () -> ItemDroppedEventJS.class).extra(SUPPORTS_ITEM).cancelable();
	EventHandler ENTITY_INTERACTED = GROUP.server("entityInteracted", () -> ItemEntityInteractedEventJS.class).extra(SUPPORTS_ITEM).cancelable();
	EventHandler CRAFTED = GROUP.server("crafted", () -> ItemCraftedEventJS.class).extra(SUPPORTS_ITEM);
	EventHandler SMELTED = GROUP.server("smelted", () -> ItemSmeltedEventJS.class).extra(SUPPORTS_ITEM);
	EventHandler FOOD_EATEN = GROUP.server("foodEaten", () -> FoodEatenEventJS.class).extra(SUPPORTS_ITEM).cancelable();
	EventHandler RIGHT_CLICKED_EMPTY = GROUP.client("rightClickedEmpty", () -> ItemRightClickedEmptyEventJS.class).extra(SUPPORTS_ITEM);
	EventHandler LEFT_CLICKED = GROUP.client("leftClicked", () -> ItemLeftClickedEventJS.class).extra(SUPPORTS_ITEM);
	EventHandler TOOLTIP = GROUP.client("tooltip", () -> ItemTooltipEventJS.class);
	EventHandler MODEL_PROPERTIES = GROUP.startup("modelProperties", () -> ItemModelPropertiesEventJS.class);
}
