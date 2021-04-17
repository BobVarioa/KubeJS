package dev.latvian.kubejs.forge;

import dev.latvian.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class BuiltinKubeJSForgePlugin extends BuiltinKubeJSPlugin {
	@Override
	public void addClasses(ScriptType type, ClassList list) {
		super.addClasses(type, list);

		list.allow("net.minecraftforge");
		list.deny("net.minecraftforge.fml");
		list.deny("net.minecraftforge.accesstransformer");
		list.deny("net.minecraftforge.coremod");
	}

	@Override
	public void addBindings(BindingsEvent event) {
		super.addBindings(event);

		if (event.type == ScriptType.STARTUP) {
			event.addFunction("onForgeEvent", args -> onPlatformEvent(event, args), null, KubeJSForgeEventHandlerWrapper.class);
		}
	}

	public static Object onPlatformEvent(BindingsEvent event, Object[] args) {
		if (args.length < 2 || !(args[0] instanceof CharSequence)) {
			throw new RuntimeException("Invalid syntax! onPlatformEvent(string, function) required event class and handler");
		} else if (!KubeJS.startupScriptManager.firstLoad) {
			ScriptType.STARTUP.console.warn("onPlatformEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
			return null;
		}

		try {
			Class type = Class.forName(args[0].toString());
			KubeJSForgeEventHandlerWrapper handler = (KubeJSForgeEventHandlerWrapper) args[1];
			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, type, handler);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}
}
