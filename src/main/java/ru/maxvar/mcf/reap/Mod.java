package ru.maxvar.mcf.reap;

import net.fabricmc.api.ModInitializer;

public class Mod implements ModInitializer {
    static final String MOD_ID = "mcf-reap";

    @Override
    public void onInitialize() {
        System.out.println(MOD_ID + " init done!");
    }
}
