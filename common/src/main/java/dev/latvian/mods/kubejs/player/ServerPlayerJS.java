package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.core.PlayerInteractionManagerKJS;
import dev.latvian.mods.kubejs.net.PaintMessage;
import dev.latvian.mods.kubejs.net.SendDataFromServerMessage;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.world.BlockContainerJS;
import dev.latvian.mods.kubejs.world.ServerWorldJS;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * @author LatvianModder
 */
public class ServerPlayerJS extends PlayerJS<ServerPlayer> {
	public final ServerJS server;
	private final boolean hasClientMod;

	public ServerPlayerJS(ServerPlayerDataJS d, ServerWorldJS w, ServerPlayer p) {
		super(d, w, p);
		server = w.getServer();
		hasClientMod = d.hasClientMod();
	}

	@Override
	public PlayerStatsJS getStats() {
		return new PlayerStatsJS(this, minecraftPlayer.getStats());
	}

	@Override
	public void paint(CompoundTag renderer) {
		new PaintMessage(renderer).sendTo(minecraftPlayer);
	}

	@Override
	public boolean isMiningBlock() {
		return ((PlayerInteractionManagerKJS) minecraftPlayer.gameMode).isDestroyingBlockKJS();
	}

	public void setCreativeMode(boolean mode) {
		minecraftPlayer.setGameMode(mode ? GameType.CREATIVE : GameType.SURVIVAL);
	}

	public void setGameMode(String mode) {
		switch (mode) {
			case "survival" -> minecraftPlayer.setGameMode(GameType.SURVIVAL);
			case "creative" -> minecraftPlayer.setGameMode(GameType.CREATIVE);
			case "adventure" -> minecraftPlayer.setGameMode(GameType.ADVENTURE);
			case "spectator" -> minecraftPlayer.setGameMode(GameType.SPECTATOR);
		}
	}

	@Deprecated
	public boolean isOP() {
		return isOp();
	}

	public boolean isOp() {
		return server.getMinecraftServer().getPlayerList().isOp(minecraftPlayer.getGameProfile());
	}

	public void kick(Component reason) {
		minecraftPlayer.connection.disconnect(reason);
	}

	public void kick() {
		kick(new TranslatableComponent("multiplayer.disconnect.kicked"));
	}

	public void ban(String banner, String reason, long expiresInMillis) {
		Date date = new Date();
		UserBanListEntry userlistbansentry = new UserBanListEntry(minecraftPlayer.getGameProfile(), date, banner, new Date(date.getTime() + (expiresInMillis <= 0L ? 315569260000L : expiresInMillis)), reason);
		server.getMinecraftServer().getPlayerList().getBans().add(userlistbansentry);
		kick(new TranslatableComponent("multiplayer.disconnect.banned"));
	}

	public boolean getHasClientMod() {
		return hasClientMod;
	}

	public boolean isAdvancementDone(ResourceLocation id) {
		AdvancementJS a = ServerJS.instance.getAdvancement(id);
		return a != null && minecraftPlayer.getAdvancements().getOrStartProgress(a.advancement).isDone();
	}

	public void unlockAdvancement(ResourceLocation id) {
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null) {
			AdvancementProgress advancementprogress = minecraftPlayer.getAdvancements().getOrStartProgress(a.advancement);

			for (var s : advancementprogress.getRemainingCriteria()) {
				minecraftPlayer.getAdvancements().award(a.advancement, s);
			}
		}
	}

	public void revokeAdvancement(ResourceLocation id) {
		AdvancementJS a = ServerJS.instance.getAdvancement(id);

		if (a != null) {
			AdvancementProgress advancementprogress = minecraftPlayer.getAdvancements().getOrStartProgress(a.advancement);

			if (advancementprogress.hasProgress()) {
				for (var s : advancementprogress.getCompletedCriteria()) {
					minecraftPlayer.getAdvancements().revoke(a.advancement, s);
				}
			}
		}
	}

	@Override
	public void setSelectedSlot(int index) {
		int p = getSelectedSlot();
		super.setSelectedSlot(index);
		int n = getSelectedSlot();

		if (p != n && minecraftPlayer.connection != null) {
			minecraftPlayer.connection.send(new ClientboundSetCarriedItemPacket(n));
		}
	}

	// FIXME: carried
	/*@Override
	public void setMouseItem(ItemStackJS item) {
		super.setMouseItem(item);

		if (minecraftPlayer.connection != null) {
			minecraftPlayer.broadcastCarriedItem();
		}
	}*/

	@Override
	public void sendData(String channel, @Nullable CompoundTag data) {
		if (!channel.isEmpty()) {
			new SendDataFromServerMessage(channel, data).sendTo(minecraftPlayer);
		}
	}

	@Nullable
	public BlockContainerJS getSpawnLocation() {
		BlockPos pos = minecraftPlayer.getRespawnPosition();
		return pos == null ? null : new BlockContainerJS(minecraftPlayer.level, pos);
	}

	public void setSpawnLocation(BlockContainerJS c) {
		minecraftPlayer.setRespawnPosition(c.minecraftLevel.dimension(), c.getPos(), 0F, true, false);
	}
}