package me.bewf.clique;

import me.bewf.clique.userstate.UserState;
import me.bewf.clique.userstate.UserStateManager;
import me.bewf.clique.util.UpdateCheckListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliqueMod implements ClientModInitializer {

	public static final String MOD_ID = "clique";
	public static final String NAME = "Clique";
	public static final String MC_VERSION = "26.2-pre4";
	public static final String LOADER = "fabric";
	public static final String MODRINTH_PROJECT_ID = "";
	public static final String MODRINTH_SLUG = "clique";

	public static final String VERSION = FabricLoader.getInstance()
			.getModContainer(MOD_ID)
			.map(c -> c.getMetadata().getVersion().getFriendlyString())
			.orElse("unknown");

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Clique {} initializing", VERSION);

		UpdateCheckListener.register();

		UserStateManager.setState(UserState.NORMAL);

		me.bewf.clique.input.Keybinds.register();

		me.bewf.clique.debug.PresenceDebugLogger.spamLog();
	}
}