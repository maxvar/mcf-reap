package ru.maxvar.mcf.reap.menu;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import ru.maxvar.mcf.reap.Mod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class ConfigManager {
    private static Config config;
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve(Mod.MOD_ID + ".json");
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(r -> new Thread(r, "mfc-reap Config Manager"));

    private ConfigManager() {
    }

    public static Config getConfig() {
        return config != null ? config : init();
    }

    private static Config init() {
        if (!Files.exists(configPath)) {
            Mod.LOGGER.info("Creating config file ({})", configPath::getFileName);
            save().join();
        }
        load().thenApply(o -> config = o).join();
        return Objects.requireNonNull(config, "Failed to init config");
    }

    private static CompletableFuture<Config> load() {
        return CompletableFuture.supplyAsync(() -> {
            try (final BufferedReader reader = Files.newBufferedReader(configPath)) {
                return GSON.fromJson(reader, Config.class);
            } catch (final IOException | JsonSyntaxException | JsonIOException e) {
                Mod.LOGGER.error("Unable to read config file, restoring defaults!", e);
                save();
                return new Config();
            }
        }, EXECUTOR);
    }

    public static CompletableFuture<Void> save() {
        Mod.LOGGER.trace("Saving config file to {}", configPath);
        return CompletableFuture.runAsync(() -> {
            try (final BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(Optional.ofNullable(config).orElseGet(Config::new), writer);
            } catch (final IOException | JsonIOException e) {
                Mod.LOGGER.error("Unable to write config file", e);
            }
        }, EXECUTOR);
    }

}
