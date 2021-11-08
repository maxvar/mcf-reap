package ru.maxvar.mcf.reap;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class Mod implements ModInitializer {
    public static final String MOD_ID = "mcf-reap";
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info(MOD_ID + " init done!");
    }
}
