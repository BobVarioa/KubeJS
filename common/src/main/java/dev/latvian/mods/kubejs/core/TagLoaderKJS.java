package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.mods.kubejs.server.TagEventJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public interface TagLoaderKJS<T> {
	default void customTagsKJS(Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
		TagIngredientJS.resetContext();
		var c = getDirectory().substring(5);
		var reg = getRegistryKJS();

		if (reg != null) {
			var event = new TagEventJS<>(c, map, reg);
			var id = UtilsJS.stripEventName(c);
			event.post("tags.%s".formatted(id));

			switch (id) {
				case "items" -> event.post("item.tags");
				case "blocks" -> event.post("block.tags");
				case "fluids" -> event.post("fluid.tags");
			}
		}
	}

	void setRegistryKJS(Registry<T> registry);

	@Nullable
	Registry<T> getRegistryKJS();

	String getDirectory();
}