package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.ComposterBlock;

/**
 * @author LatvianModder
 */
public class CompostablesRecipeEventJS extends EventJS {
	public void remove(IngredientJS o) {
		for (var item : o.getVanillaItems()) {
			ComposterBlock.COMPOSTABLES.removeFloat(item);
		}
	}

	public void removeAll() {
		ComposterBlock.COMPOSTABLES.clear();
	}

	public void add(IngredientJS o, float f) {
		for (var item : o.getVanillaItems()) {
			ComposterBlock.COMPOSTABLES.put(item, Mth.clamp(f, 0F, 1F));
		}
	}
}