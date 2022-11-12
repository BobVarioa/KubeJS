package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ScriptManager implements ClassShutter {
	private static final ThreadLocal<Context> CURRENT_CONTEXT = new ThreadLocal<>();

	@Nullable
	public static Context getCurrentContext() {
		return CURRENT_CONTEXT.get();
	}

	public final ScriptType scriptType;
	public final Path directory;
	private final String exampleScript;
	public final Map<String, ScriptPack> packs;
	private final ClassFilter classFilter;
	public boolean firstLoad;
	public Context context;
	public Scriptable topLevelScope;
	private Map<String, Optional<NativeJavaClass>> javaClassCache;
	public boolean canListenEvents;

	public ScriptManager(ScriptType t, Path p, String e) {
		scriptType = t;
		directory = p;
		exampleScript = e;
		packs = new LinkedHashMap<>();
		firstLoad = true;
		classFilter = KubeJSPlugins.createClassFilter(scriptType);
	}

	public void unload() {
		packs.clear();
		scriptType.unload();
		javaClassCache = null;
	}

	public void reload(@Nullable ResourceManager resourceManager) {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::clearCaches);

		unload();
		loadFromDirectory();

		if (resourceManager != null) {
			loadFromResources(resourceManager);
		}

		load();
	}

	private void loadFromResources(ResourceManager resourceManager) {
		Map<String, List<ResourceLocation>> packMap = new HashMap<>();

		for (var resource : resourceManager.listResources("kubejs", s -> s.getPath().endsWith(".js")).keySet()) {
			packMap.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}

		for (var entry : packMap.entrySet()) {
			var pack = new ScriptPack(this, new ScriptPackInfo(entry.getKey(), "kubejs/"));

			for (var id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}

			for (var fileInfo : pack.info.scripts) {
				var scriptSource = (ScriptSource.FromResource) info -> resourceManager.getResourceOrThrow(info.id);
				var error = fileInfo.preload(scriptSource);

				if (fileInfo.skipLoading()) {
					continue;
				}

				if (error == null) {
					pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
				} else {
					scriptType.console.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
				}
			}

			pack.scripts.sort(null);
			packs.put(pack.info.namespace, pack);
		}
	}

	public void loadFromDirectory() {
		if (Files.notExists(directory)) {
			UtilsJS.tryIO(() -> Files.createDirectories(directory));

			try (var in = Files.newInputStream(KubeJS.thisMod.findResource("data", "kubejs", exampleScript).get());
				 var out = Files.newOutputStream(directory.resolve("script.js"))) {
				in.transferTo(out);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		var pack = new ScriptPack(this, new ScriptPackInfo(directory.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, directory, "");

		for (var fileInfo : pack.info.scripts) {
			var scriptSource = (ScriptSource.FromPath) info -> directory.resolve(info.file);

			var error = fileInfo.preload(scriptSource);

			if (fileInfo.skipLoading()) {
				continue;
			}

			if (error == null) {
				pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
			} else {
				KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
			}
		}

		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}

	public boolean isClassAllowed(String name) {
		return classFilter.isAllowed(name);
	}

	public void load() {
		var remapper = RemappingHelper.getMinecraftRemapper();

		var startAll = System.currentTimeMillis();
		context = Context.enter();
		topLevelScope = context.initStandardObjects();

		CURRENT_CONTEXT.set(context);

		canListenEvents = true;
		var i = 0;
		var t = 0;

		context.sharedContextData.setExtraProperty("Type", scriptType);
		context.sharedContextData.setExtraProperty("Console", scriptType.console);
		context.sharedContextData.setClassShutter(this);
		context.sharedContextData.setRemapper(remapper);
		var typeWrappers = context.sharedContextData.getTypeWrappers();
		// typeWrappers.removeAll();
		var bindingsEvent = new BindingsEvent(this, topLevelScope);
		var customJavaToJsWrappersEvent = new CustomJavaToJsWrappersEvent(this, context.sharedContextData);

		for (var plugin : KubeJSPlugins.getAll()) {
			plugin.registerTypeWrappers(scriptType, typeWrappers);
			plugin.registerBindings(bindingsEvent);
			plugin.registerCustomJavaToJsWrappers(customJavaToJsWrappersEvent);
		}

		for (var registryTypeWrapperFactory : RegistryTypeWrapperFactory.getAll()) {
			try {
				typeWrappers.register(registryTypeWrapperFactory.type, UtilsJS.cast(registryTypeWrapperFactory));
			} catch (IllegalArgumentException ignored) {
			}
		}

		for (var pack : packs.values()) {
			try {
				pack.scope = new NativeObject(context);
				pack.scope.setParentScope(topLevelScope);

				for (var file : pack.scripts) {
					t++;
					var start = System.currentTimeMillis();

					try {
						file.load();
						i++;
						scriptType.console.info("Loaded script " + file.info.location + " in " + (System.currentTimeMillis() - start) / 1000D + " s");
					} catch (Throwable ex) {
						scriptType.console.handleError(ex, null, "Error loading KubeJS script: " + file.info.location + "'");
					}
				}
			} catch (Throwable ex) {
				scriptType.console.error("Failed to read script pack " + pack.info.namespace + ": ", ex);
			}
		}

		scriptType.console.info("Loaded " + i + "/" + t + " KubeJS " + scriptType.name + " scripts in " + (System.currentTimeMillis() - startAll) / 1000D + " s");
		firstLoad = false;
		canListenEvents = false;
	}

	public NativeJavaClass loadJavaClass(String name0, boolean warn) {
		if (name0 == null || name0.equals("null") || name0.isEmpty()) {
			throw Context.reportRuntimeError("Class name can't be empty!", context);
		}

		String name = RemappingHelper.getMinecraftRemapper().getUnmappedClass(name0);

		if (name.isEmpty()) {
			name = name0;
		}

		if (javaClassCache == null) {
			javaClassCache = new HashMap<>();
		}

		var ch = javaClassCache.get(name);

		if (ch == null) {
			javaClassCache.put(name, Optional.empty());

			try {
				if (!isClassAllowed(name)) {
					throw Context.reportRuntimeError("Failed to load Java class '%s': Class is not allowed by class filter!".formatted(name), context);
				}

				var c = Class.forName(name);
				var njc = new NativeJavaClass(context, topLevelScope, c);
				javaClassCache.put(name, Optional.of(njc));
				scriptType.console.info("Loaded Java class '%s'".formatted(name0));
				return njc;
			} catch (ClassNotFoundException cnf) {
				throw Context.reportRuntimeError("Failed to load Java class '%s': Class could not be found!\n%s".formatted(name, cnf.getMessage()), context);
			}
		}

		if (ch.isPresent()) {
			return ch.get();
		}

		throw Context.reportRuntimeError("Failed to load Java class '%s'!".formatted(name), context);
	}

	@Override
	public boolean visibleToScripts(String fullClassName, int type) {
		return type != ClassShutter.TYPE_CLASS_IN_PACKAGE || isClassAllowed(fullClassName);
	}
}