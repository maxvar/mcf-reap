package ru.maxvar.mcf.reap.menu;

import com.google.gson.*;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
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

    private static CompletableFuture<Void> save() {
        Mod.LOGGER.trace("Saving config file to {}", configPath);
        return CompletableFuture.runAsync(() -> {
            try (final BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(Optional.ofNullable(config).orElseGet(Config::new), writer);
            } catch (final IOException | JsonIOException e) {
                Mod.LOGGER.error("Unable to write config file", e);
            }
        }, EXECUTOR);
    }

    public static Screen createConfigScreen(final Screen parent) {
        final Config config = getConfig();
        final ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.of(String.format("config.%s.title", Mod.MOD_ID)));
        builder.getOrCreateCategory(Text.of("general"))
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(Text.of("Reaping enabled"), config.isEnabled())
                        .setDefaultValue(true)
                        .setSaveConsumer(config::setEnabled).build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(Text.of("Collect to inventory"), config.mustCollectToInventory())
                        .setDefaultValue(true)
                        .setSaveConsumer(config::setCollectToInventory).build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(Text.of("Play planting sound"), config.mustPlaySound())
                        .setDefaultValue(true)
                        .setSaveConsumer(config::setPlaySound).build());
        builder.setSavingRunnable(ConfigManager::save);
        return builder.build();
    }
}
