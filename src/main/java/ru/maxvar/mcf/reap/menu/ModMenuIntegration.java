package ru.maxvar.mcf.reap.menu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.maxvar.mcf.reap.Mod;

@SuppressWarnings("unused")
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::createConfigScreen;
    }


    public static Screen createConfigScreen(final Screen parent) {
        final Config config = ConfigManager.getConfig();
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
