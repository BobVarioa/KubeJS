
package dev.latvian.mods.kubejs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.bindings.ComponentWrapper;
import dev.latvian.mods.kubejs.bindings.DamageSourceWrapper;
import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.bindings.UtilsWrapper;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.bindings.event.EntityEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.bindings.event.NetworkEvents;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.bindings.event.WorldgenEvents;
import dev.latvian.mods.kubejs.block.DetectorBlock;
import dev.latvian.mods.kubejs.block.MaterialJS;
import dev.latvian.mods.kubejs.block.MaterialListJS;
import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import dev.latvian.mods.kubejs.block.custom.CropBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FallingBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FenceBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.FenceGateBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.HorizontalDirectionalBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.SlabBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.StairBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.StoneButtonBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.StonePressurePlateBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.WallBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.WoodenButtonBlockBuilder;
import dev.latvian.mods.kubejs.block.custom.WoodenPressurePlateBlockBuilder;
import dev.latvian.mods.kubejs.block.entity.BlockEntityBuilder;
import dev.latvian.mods.kubejs.block.entity.ablities.BlockAbility;
import dev.latvian.mods.kubejs.block.entity.ablities.EnergyBlockAbility;
import dev.latvian.mods.kubejs.block.entity.ablities.FluidBlockAbility;
import dev.latvian.mods.kubejs.block.entity.ablities.ItemBlockAbility;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.screen.AtlasTextureObject;
import dev.latvian.mods.kubejs.client.painter.screen.GradientObject;
import dev.latvian.mods.kubejs.client.painter.screen.ItemObject;
import dev.latvian.mods.kubejs.client.painter.screen.RectangleObject;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenGroup;
import dev.latvian.mods.kubejs.client.painter.screen.TextObject;
import dev.latvian.mods.kubejs.core.PlayerSelector;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupWrapper;
import dev.latvian.mods.kubejs.event.IEventHandler;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.integration.rei.REIKubeJSEvents;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.custom.ArmorItemBuilder;
import dev.latvian.mods.kubejs.item.custom.AxeItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import dev.latvian.mods.kubejs.item.custom.HoeItemBuilder;
import dev.latvian.mods.kubejs.item.custom.ItemArmorTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.ItemToolTierRegistryEventJS;
import dev.latvian.mods.kubejs.item.custom.PickaxeItemBuilder;
import dev.latvian.mods.kubejs.item.custom.RecordItemJS;
import dev.latvian.mods.kubejs.item.custom.ShovelItemBuilder;
import dev.latvian.mods.kubejs.item.custom.SwordItemBuilder;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStack;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.level.gen.filter.mob.MobFilter;
import dev.latvian.mods.kubejs.misc.BasicMobEffect;
import dev.latvian.mods.kubejs.misc.CustomStatBuilder;
import dev.latvian.mods.kubejs.misc.EnchantmentBuilder;
import dev.latvian.mods.kubejs.misc.PaintingVariantBuilder;
import dev.latvian.mods.kubejs.misc.ParticleTypeBuilder;
import dev.latvian.mods.kubejs.misc.PoiTypeBuilder;
import dev.latvian.mods.kubejs.misc.PotionBuilder;
import dev.latvian.mods.kubejs.misc.SoundEventBuilder;
import dev.latvian.mods.kubejs.misc.VillagerProfessionBuilder;
import dev.latvian.mods.kubejs.misc.VillagerTypeBuilder;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.mods.kubejs.recipe.ingredientaction.IngredientActionFilter;
import dev.latvian.mods.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.mods.kubejs.recipe.minecraft.SmithingRecipeJS;
import dev.latvian.mods.kubejs.recipe.minecraft.StonecuttingRecipeJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.CustomJavaToJsWrappersEvent;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerSettings;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.NBTIOWrapper;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.CollectionTagWrapper;
import dev.latvian.mods.rhino.mod.util.CompoundTagWrapper;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.AABBWrapper;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import dev.latvian.mods.rhino.mod.wrapper.DirectionWrapper;
import dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import dev.latvian.mods.unit.Unit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class BuiltinKubeJSPlugin extends KubeJSPlugin {
	public static final HashMap<String, Object> GLOBAL = new HashMap<>();

	@Override
	public void init() {
		RegistryObjectBuilderTypes.SOUND_EVENT.addType("basic", SoundEventBuilder.class, SoundEventBuilder::new);

		RegistryObjectBuilderTypes.BLOCK.addType("basic", BasicBlockJS.Builder.class, BasicBlockJS.Builder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("detector", DetectorBlock.Builder.class, DetectorBlock.Builder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("slab", SlabBlockBuilder.class, SlabBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("stairs", StairBlockBuilder.class, StairBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("fence", FenceBlockBuilder.class, FenceBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("fence_gate", FenceGateBlockBuilder.class, FenceGateBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("wall", WallBlockBuilder.class, WallBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("wooden_pressure_plate", WoodenPressurePlateBlockBuilder.class, WoodenPressurePlateBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("stone_pressure_plate", StonePressurePlateBlockBuilder.class, StonePressurePlateBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("wooden_button", WoodenButtonBlockBuilder.class, WoodenButtonBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("stone_button", StoneButtonBlockBuilder.class, StoneButtonBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("falling", FallingBlockBuilder.class, FallingBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("crop", CropBlockBuilder.class, CropBlockBuilder::new);
		RegistryObjectBuilderTypes.BLOCK.addType("cardinal", HorizontalDirectionalBlockBuilder.class, HorizontalDirectionalBlockBuilder::new);

		RegistryObjectBuilderTypes.ITEM.addType("basic", BasicItemJS.Builder.class, BasicItemJS.Builder::new);
		RegistryObjectBuilderTypes.ITEM.addType("sword", SwordItemBuilder.class, SwordItemBuilder::new);
		RegistryObjectBuilderTypes.ITEM.addType("pickaxe", PickaxeItemBuilder.class, PickaxeItemBuilder::new);
		RegistryObjectBuilderTypes.ITEM.addType("axe", AxeItemBuilder.class, AxeItemBuilder::new);
		RegistryObjectBuilderTypes.ITEM.addType("shovel", ShovelItemBuilder.class, ShovelItemBuilder::new);
		RegistryObjectBuilderTypes.ITEM.addType("hoe", HoeItemBuilder.class, HoeItemBuilder::new);
		RegistryObjectBuilderTypes.ITEM.addType("helmet", ArmorItemBuilder.Helmet.class, ArmorItemBuilder.Helmet::new);
		RegistryObjectBuilderTypes.ITEM.addType("chestplate", ArmorItemBuilder.Chestplate.class, ArmorItemBuilder.Chestplate::new);
		RegistryObjectBuilderTypes.ITEM.addType("leggings", ArmorItemBuilder.Leggings.class, ArmorItemBuilder.Leggings::new);
		RegistryObjectBuilderTypes.ITEM.addType("boots", ArmorItemBuilder.Boots.class, ArmorItemBuilder.Boots::new);
		RegistryObjectBuilderTypes.ITEM.addType("music_disc", RecordItemJS.Builder.class, RecordItemJS.Builder::new);

		RegistryObjectBuilderTypes.FLUID.addType("basic", FluidBuilder.class, FluidBuilder::new);
		RegistryObjectBuilderTypes.ENCHANTMENT.addType("basic", EnchantmentBuilder.class, EnchantmentBuilder::new);
		RegistryObjectBuilderTypes.MOB_EFFECT.addType("basic", BasicMobEffect.Builder.class, BasicMobEffect.Builder::new);
		// ENTITY_TYPE
		RegistryObjectBuilderTypes.BLOCK_ENTITY_TYPE.addType("basic", BlockEntityBuilder.class, BlockEntityBuilder::new);
		RegistryObjectBuilderTypes.POTION.addType("basic", PotionBuilder.class, PotionBuilder::new);
		RegistryObjectBuilderTypes.PARTICLE_TYPE.addType("basic", ParticleTypeBuilder.class, ParticleTypeBuilder::new);
		RegistryObjectBuilderTypes.PAINTING_VARIANT.addType("basic", PaintingVariantBuilder.class, PaintingVariantBuilder::new);
		RegistryObjectBuilderTypes.CUSTOM_STAT.addType("basic", CustomStatBuilder.class, CustomStatBuilder::new);
		RegistryObjectBuilderTypes.POINT_OF_INTEREST_TYPE.addType("basic", PoiTypeBuilder.class, PoiTypeBuilder::new);
		RegistryObjectBuilderTypes.VILLAGER_TYPE.addType("basic", VillagerTypeBuilder.class, VillagerTypeBuilder::new);
		RegistryObjectBuilderTypes.VILLAGER_PROFESSION.addType("basic", VillagerProfessionBuilder.class, VillagerProfessionBuilder::new);
	}

	@Override
	public void initStartup() {
		ItemEvents.TOOL_TIER_REGISTRY.post(new ItemToolTierRegistryEventJS());
		ItemEvents.ARMOR_TIER_REGISTRY.post(new ItemArmorTierRegistryEventJS());

		for (var types : RegistryObjectBuilderTypes.MAP.values()) {
			// types.postEvent();
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void clientInit() {
		Painter.INSTANCE.registerObject("screen_group", ScreenGroup::new);
		Painter.INSTANCE.registerObject("rectangle", RectangleObject::new);
		Painter.INSTANCE.registerObject("text", TextObject::new);
		Painter.INSTANCE.registerObject("atlas_texture", AtlasTextureObject::new);
		Painter.INSTANCE.registerObject("gradient", GradientObject::new);
		Painter.INSTANCE.registerObject("item", ItemObject::new);
	}

	@Override
	public void registerEvents() {
		StartupEvents.GROUP.register();
		ClientEvents.GROUP.register();
		ServerEvents.GROUP.register();
		LevelEvents.GROUP.register();
		WorldgenEvents.GROUP.register();
		NetworkEvents.GROUP.register();
		ItemEvents.GROUP.register();
		BlockEvents.GROUP.register();
		EntityEvents.GROUP.register();
		PlayerEvents.GROUP.register();

		if (Platform.isModLoaded("roughlyenoughitems")) {
			REIKubeJSEvents.register();
		}
	}

	@Override
	public void registerClasses(ScriptType type, ClassFilter filter) {
		filter.allow("java.lang.Number"); // java.lang
		filter.allow("java.lang.String");
		filter.allow("java.lang.Character");
		filter.allow("java.lang.Byte");
		filter.allow("java.lang.Short");
		filter.allow("java.lang.Integer");
		filter.allow("java.lang.Long");
		filter.allow("java.lang.Float");
		filter.allow("java.lang.Double");
		filter.allow("java.lang.Boolean");
		filter.allow("java.lang.Runnable");
		filter.allow("java.lang.Iterable");
		filter.allow("java.lang.Comparable");
		filter.allow("java.lang.CharSequence");

		filter.allow("java.math.BigInteger"); // java.math
		filter.allow("java.math.BigDecimal");

		filter.deny("java.io"); // IO
		filter.allow("java.io.Closeable");
		filter.allow("java.io.Serializable");

		filter.deny("java.nio"); // NIO
		filter.allow("java.nio.ByteOrder");

		filter.allow("java.util"); // Utils
		filter.deny("java.util.jar");
		filter.deny("java.util.zip");

		filter.allow("it.unimi.dsi.fastutil"); // FastUtil

		filter.allow("dev.latvian.mods.kubejs"); // KubeJS
		filter.deny("dev.latvian.mods.kubejs.script");
		filter.deny("dev.latvian.mods.kubejs.mixin");
		filter.deny(KubeJSPlugin.class);
		filter.deny(KubeJSPlugins.class);

		filter.allow("net.minecraft"); // Minecraft
		filter.allow("com.mojang.authlib.GameProfile");
		filter.allow("com.mojang.util.UUIDTypeAdapter");
		filter.allow("com.mojang.brigadier");
		filter.allow("com.mojang.blaze3d");

		filter.allow("dev.architectury"); // Architectury

		// Misc
		filter.deny("java.net"); // Networks
		filter.deny("sun"); // Sun
		filter.deny("com.sun"); // Sun
		filter.deny("io.netty"); // Netty
		filter.deny("org.objectweb.asm"); // ASM
		filter.deny("org.spongepowered.asm"); // Sponge ASM
		filter.deny("org.openjdk.nashorn"); // Nashorn
		filter.deny("jdk.nashorn"); // Nashorn

		// Mods
		filter.allow("mezz.jei"); // JEI
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("global", GLOBAL);

		if (event.type == ScriptType.SERVER) {
			ServerSettings.instance = new ServerSettings();
			event.add("settings", ServerSettings.instance);
		}

		event.add("Platform", PlatformWrapper.class);
		event.add("console", event.type.console);

		for (var group : EventGroup.getGroups().values()) {
			event.add(group.name, new EventGroupWrapper(event.type, group));
		}

		event.addFunction("onEvent", args -> onLegacyEvent(event, args[0], (IEventHandler) args[1]), null, IEventHandler.class);
		event.addFunction("java", args -> event.manager.loadJavaClass(event, args), new Class[]{null});

		event.add("JavaMath", Math.class);
		event.add("ResourceLocation", ResourceLocation.class);

		event.add("Utils", UtilsWrapper.class);
		event.add("Component", ComponentWrapper.class);
		event.add("Text", ComponentWrapper.class);
		event.add("UUID", UUIDWrapper.class);
		event.add("JsonIO", JsonIO.class);
		event.add("Block", BlockWrapper.class);
		event.add("Item", ItemWrapper.class);
		event.add("Ingredient", IngredientWrapper.class);
		event.add("IngredientHelper", IngredientPlatformHelper.get());
		event.add("NBT", NBTUtils.class);
		event.add("NBTIO", NBTIOWrapper.class);
		event.add("Direction", DirectionWrapper.class);
		event.add("Facing", DirectionWrapper.class);
		event.add("AABB", AABBWrapper.class);

		event.add("Fluid", FluidWrapper.class);

		event.add("SECOND", 1000L);
		event.add("MINUTE", 60000L);
		event.add("HOUR", 3600000L);

		event.add("Color", ColorWrapper.class);
		event.add("BlockStatePredicate", BlockStatePredicate.class);

		event.add("AIR_ITEM", Items.AIR);
		event.add("AIR_BLOCK", Blocks.AIR);

		event.add("DecorationGenerationStep", GenerationStep.Decoration.class);
		event.add("CarvingGenerationStep", GenerationStep.Carving.class);
		event.add("Vec3", Vec3.class);
		event.add("Vec3d", Vec3.class);
		event.add("Vec3i", Vec3i.class);
		event.add("BlockPos", BlockPos.class);
		event.add("DamageSource", DamageSource.class);

		event.add("BlockProperties", BlockStateProperties.class);

		KubeJS.PROXY.clientBindings(event);
	}

	private static Object onLegacyEvent(BindingsEvent event, Object ids, IEventHandler handler) {
		for (var o : ListJS.orSelf(ids)) {
			String str = String.valueOf(o);

			event.type.console.pushLineNumber();

			var h = EventGroup.getLegacyMap().get(str);

			if (h != null) {
				event.type.console.warn("Legacy '" + str + "' event handler found! Change it to `" + h + "(event => { })`");
				h.listen(event.type, null, handler);
			} else {
				int index = str.lastIndexOf('.');

				if (index == -1) {
					event.type.console.warn("Unknown legacy event handler '" + str + "'!");
				} else {
					var h1 = EventGroup.getLegacyMap().get(str.substring(0, index));

					if (h1 != null) {
						String extra = str.substring(index + 1);
						event.type.console.warn("Legacy '" + str + "' event handler found! Change it to `" + h1 + "('" + extra + "', event => { })`");
						h1.listen(event.type, extra, handler);
					} else {
						event.type.console.warn("Unknown legacy event handler '" + str + "'!");
					}
				}
			}

			event.type.console.popLineNumber();
		}

		return null;
	}

	@Override
	public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
		// Java / Minecraft //
		typeWrappers.register(String.class, String::valueOf);
		typeWrappers.register(CharSequence.class, String::valueOf);
		typeWrappers.register(UUID.class, UUIDWrapper::fromString);
		typeWrappers.register(Pattern.class, UtilsJS::parseRegex);
		typeWrappers.register(JsonObject.class, MapJS::json);
		typeWrappers.register(JsonArray.class, ListJS::json);
		typeWrappers.register(JsonElement.class, JsonIO::of);
		typeWrappers.register(JsonPrimitive.class, JsonIO::primitiveOf);
		typeWrappers.register(Path.class, UtilsJS::getPath);
		typeWrappers.register(File.class, UtilsJS::getFileFromPath);
		typeWrappers.register(Unit.class, Painter.INSTANCE::unitOf);

		typeWrappers.register(ResourceLocation.class, UtilsJS::getMCID);
		typeWrappers.register(CompoundTag.class, NBTUtils::isTagCompound, NBTUtils::toTagCompound);
		typeWrappers.register(CollectionTag.class, NBTUtils::isTagCollection, NBTUtils::toTagCollection);
		typeWrappers.register(ListTag.class, NBTUtils::isTagCollection, NBTUtils::toTagList);
		typeWrappers.register(Tag.class, NBTUtils::toTag);

		typeWrappers.register(BlockPos.class, UtilsJS::blockPosOf);
		typeWrappers.register(Vec3.class, UtilsJS::vec3Of);

		typeWrappers.register(Item.class, ItemStackJS::getRawItem);
		typeWrappers.register(MobCategory.class, o -> o == null ? null : UtilsJS.mobCategoryByName(o.toString()));

		typeWrappers.register(AABB.class, AABBWrapper::wrap);
		typeWrappers.register(IntProvider.class, UtilsJS::intProviderOf);
		typeWrappers.register(NumberProvider.class, UtilsJS::numberProviderOf);
		typeWrappers.register(LootContext.EntityTarget.class, o -> o == null ? null : LootContext.EntityTarget.getByName(o.toString().toLowerCase()));
		typeWrappers.register(CopyNameFunction.NameSource.class, o -> o == null ? null : CopyNameFunction.NameSource.getByName(o.toString().toLowerCase()));

		// KubeJS //
		typeWrappers.register(Map.class, MapJS::of);
		typeWrappers.register(List.class, ListJS::of);
		typeWrappers.register(Iterable.class, ListJS::of);
		typeWrappers.register(Collection.class, ListJS::of);
		typeWrappers.register(Set.class, ListJS::ofSet);
		typeWrappers.register(ItemStack.class, ItemStackJS::of);
		typeWrappers.register(Ingredient.class, IngredientJS::of);
		typeWrappers.register(IngredientStack.class, o -> IngredientJS.of(o).kjs$asStack());
		typeWrappers.register(BlockStatePredicate.class, BlockStatePredicate::of);
		typeWrappers.register(RuleTest.class, BlockStatePredicate::ruleTestOf);
		typeWrappers.register(BiomeFilter.class, BiomeFilter::of);
		typeWrappers.register(MobFilter.class, MobFilter::of);
		typeWrappers.register(FluidStackJS.class, FluidStackJS::of);
		typeWrappers.register(RecipeFilter.class, RecipeFilter::of);
		typeWrappers.register(MaterialJS.class, MaterialListJS.INSTANCE::of);
		typeWrappers.register(IngredientActionFilter.class, IngredientActionFilter::filterOf);
		typeWrappers.register(Tier.class, o -> ItemBuilder.TOOL_TIERS.getOrDefault(String.valueOf(o), Tiers.IRON));
		typeWrappers.register(ArmorMaterial.class, ItemBuilder::ofArmorMaterial);
		typeWrappers.register(PlayerSelector.class, PlayerSelector::of);
		typeWrappers.register(DamageSource.class, DamageSourceWrapper::of);
		typeWrappers.register(EntitySelector.class, UtilsJS::entitySelector);
		typeWrappers.register(IngredientMatch.class, IngredientMatch::of);
		typeWrappers.register(BlockAbility.AbilityJS.class, BlockAbility.AbilityJS::of);
		typeWrappers.register(BlockAbility.SlotDefinition.class, BlockAbility.SlotDefinition::of);

		// components //
		typeWrappers.register(Component.class, ComponentWrapper::of);
		typeWrappers.register(MutableComponent.class, ComponentWrapper::ofMutable);
		typeWrappers.register(Color.class, ColorWrapper::of);
		typeWrappers.register(TextColor.class, o -> ColorWrapper.of(o).createTextColorJS());
		typeWrappers.register(ClickEvent.class, ComponentWrapper::clickEventOf);

		KubeJS.PROXY.clientTypeWrappers(typeWrappers);
	}

	@Override
	public void registerCustomJavaToJsWrappers(CustomJavaToJsWrappersEvent event) {
		event.add(CompoundTag.class, CompoundTagWrapper::new);
		event.add(CollectionTag.class, CollectionTagWrapper::new);
	}

	@Override
	public void registerRecipeTypes(RegisterRecipeTypesEvent event) {
		event.registerShaped(new ResourceLocation("kubejs:shaped"));
		event.registerShapeless(new ResourceLocation("kubejs:shapeless"));
		event.registerShaped(new ResourceLocation("minecraft:crafting_shaped"));
		event.registerShapeless(new ResourceLocation("minecraft:crafting_shapeless"));
		event.register(new ResourceLocation("minecraft:stonecutting"), StonecuttingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smelting"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:blasting"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smoking"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:campfire_cooking"), CookingRecipeJS::new);
		event.register(new ResourceLocation("minecraft:smithing"), SmithingRecipeJS::new);

		// Mod recipe types that use vanilla syntax

		if (Platform.isModLoaded("cucumber")) {
			event.registerShaped(new ResourceLocation("cucumber:shaped_no_mirror"));
		}

		if (Platform.isModLoaded("extendedcrafting")) {
			event.registerShaped(new ResourceLocation("extendedcrafting:shaped_table"));
			event.registerShapeless(new ResourceLocation("extendedcrafting:shapeless_table"));
		}

		if (Platform.isModLoaded("dankstorage")) {
			event.registerShaped(new ResourceLocation("dankstorage:upgrade"));
		}
	}

	@Override
	public void generateDataJsons(DataJsonGenerator generator) {
		for (var builder : RegistryObjectBuilderTypes.ALL_BUILDERS) {
			builder.generateDataJsons(generator);
		}
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		for (var builder : RegistryObjectBuilderTypes.ALL_BUILDERS) {
			builder.generateAssetJsons(generator);
		}
	}

	@Override
	public void generateLang(Map<String, String> lang) {
		lang.put("itemGroup.kubejs.kubejs", "KubeJS");
		lang.put("item.kubejs.dummy_fluid_item", "Dummy Fluid Item");

		for (var builder : RegistryObjectBuilderTypes.ALL_BUILDERS) {
			builder.generateLang(lang);
		}
	}
}
