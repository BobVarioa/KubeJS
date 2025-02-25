package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.EntityJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
public class PlayerChatEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	public Component component;

	public PlayerChatEventJS(ServerPlayer player, Component component) {
		this.player = player;
		this.component = component;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public String getUsername() {
		return player.getGameProfile().getName();
	}

	public String getMessage() {
		return component.getString();
	}

	public MutableComponent getComponent() {
		return component.copy();
	}

	public void setMessage(Component text) {
		component = text;
	}
}