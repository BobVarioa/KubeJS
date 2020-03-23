package dev.latvian.kubejs.mixin;

import dev.latvian.kubejs.KubeJSCore;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author LatvianModder
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Inject(method = "onFoodEaten", at = @At("HEAD"))
	private void foodEaten(World world, ItemStack item, CallbackInfoReturnable<ItemStack> ci)
	{
		KubeJSCore.foodEaten((LivingEntity) (Object) this, item);
	}
}