package dev.latvian.mods.kubejs.entity;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.latvian.mods.kubejs.bindings.event.EntityEvents;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * @author LatvianModder
 */
public class KubeJSEntityEventHandler {
	public static void init() {
		EntityEvent.LIVING_CHECK_SPAWN.register(KubeJSEntityEventHandler::checkSpawn);
		EntityEvent.LIVING_DEATH.register(KubeJSEntityEventHandler::livingDeath);
		EntityEvent.LIVING_HURT.register(KubeJSEntityEventHandler::livingHurt);
		EntityEvent.ADD.register(KubeJSEntityEventHandler::entitySpawned);
	}

	private static EventResult checkSpawn(LivingEntity entity, LevelAccessor la, double x, double y, double z, MobSpawnType type, BaseSpawner spawner) {
		if (la instanceof ServerLevel level && UtilsJS.staticServer != null && EntityEvents.CHECK_SPAWN.post(entity.getType(), new CheckLivingEntitySpawnEventJS(entity, level, x, y, z, type))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult livingDeath(LivingEntity entity, DamageSource source) {
		if (entity != null && entity.level instanceof ServerLevel && EntityEvents.DEATH.post(entity.getType(), new LivingEntityDeathEventJS(entity, source))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult livingHurt(LivingEntity entity, DamageSource source, float amount) {
		if (entity != null && entity.level instanceof ServerLevel && amount > 0F && EntityEvents.HURT.post(entity.getType(), new LivingEntityHurtEventJS(entity, source, amount))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult entitySpawned(Entity entity, Level level) {
		if (entity != null && level instanceof ServerLevel && UtilsJS.staticServer != null && EntityEvents.SPAWNED.post(entity.getType(), new EntitySpawnedEventJS(entity, level))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}
}