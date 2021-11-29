package dev.latvian.mods.kubejs.fluid;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJSObjects;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * @author LatvianModder
 */
public class KubeJSFluidEventHandler {
	public static void init() {
		if (!CommonProperties.get().serverOnly) {
			registry();
		}
	}

	@ExpectPlatform
	private static FlowingFluid buildFluid(boolean source, FluidBuilder builder) {
		throw new AssertionError();
	}

	private static void registry() {
		for (var builder : KubeJSObjects.FLUIDS.values()) {
			KubeJSRegistries.fluids().register(builder.id, () -> builder.stillFluid = buildFluid(true, builder));
			KubeJSRegistries.fluids().register(new ResourceLocation(builder.id.getNamespace(), "flowing_" + builder.id.getPath()), () -> builder.flowingFluid = buildFluid(false, builder));
		}
	}
}