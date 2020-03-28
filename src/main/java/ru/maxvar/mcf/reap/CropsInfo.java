package ru.maxvar.mcf.reap;

import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class CropsInfo {
    public static Item getSeedsItem(PlantBlock plant) {
        if (plant instanceof BeetrootsBlock)
            return Items.BEETROOT_SEEDS;
        if (plant instanceof CarrotsBlock)
            return Items.CARROT;
        if (plant instanceof PotatoesBlock)
            return Items.POTATO;
        if (plant instanceof CropBlock)
            return Items.WHEAT_SEEDS;
        return Items.DIAMOND_BLOCK;
    }

}
