package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJSEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class SendDataFromClientMessage extends BaseC2SMessage {
	private final String channel;
	private final CompoundTag data;

	public SendDataFromClientMessage(String c, @Nullable CompoundTag d) {
		channel = c;
		data = d;
	}

	SendDataFromClientMessage(FriendlyByteBuf buf) {
		channel = buf.readUtf(120);
		data = buf.readNbt();
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.SEND_DATA_FROM_CLIENT;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(channel, 120);
		buf.writeNbt(data);
	}

	@Override
	public void handle(PacketContext context) {
		if (!channel.isEmpty()) {
			final var player = context.getPlayer();

			if (player != null) {
				new NetworkEventJS(player, channel, data).post(KubeJSEvents.PLAYER_DATA_FROM_CLIENT, channel);
			}
		}
	}
}