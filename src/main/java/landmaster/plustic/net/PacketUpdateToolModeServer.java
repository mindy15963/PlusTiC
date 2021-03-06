package landmaster.plustic.net;

import io.netty.buffer.*;
import landmaster.plustic.tools.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateToolModeServer implements IMessage {
	
	public static IMessage onMessage(PacketUpdateToolModeServer packet, MessageContext ctx) {
		IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.getEntityWorld();
		mainThread.addScheduledTask(() -> {
			final Enum<?> enm = IToggleTool.rotateHeldItemMode(ctx.getServerHandler().player);
			if (enm instanceof IEnumL10n) {
				ctx.getServerHandler().player.sendMessage(new TextComponentTranslation("msg.plustic.tool_mode", new TextComponentTranslation(((IEnumL10n)enm).getUnlocName())));
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}
	
}
