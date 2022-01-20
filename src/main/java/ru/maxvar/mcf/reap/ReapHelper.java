package ru.maxvar.mcf.reap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.maxvar.mcf.reap.menu.ConfigManager;

import java.util.List;

public final class ReapHelper {
    private ReapHelper() {
    }

    @SuppressWarnings("SameReturnValue")
    @NotNull
    public static ActionResult reap(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final Item seedItem, final IntProperty ageProperty) {
        //get loot from the plant block
        //except one seed from the loot
        Mod.LOGGER.info("just started reap @" + (world.isClient ? "CLIENT" : "SERVER"));
        final List<ItemStack> dropList = Block.getDroppedStacks(state, (ServerWorld) world, pos, null, player, player.getStackInHand(hand));
        final DefaultedList<ItemStack> drops = getDrops(seedItem, dropList);
        //reduce to new plant
        Mod.LOGGER.info("reducing crop age…");
        world.setBlockState(pos, state.with(ageProperty, 0));
        if (ConfigManager.getConfig().mustCollectToInventory()) {
            Mod.LOGGER.info("must collect to inventory!");
            //try to put loot into players inventory
            final PlayerInventory playerInventory = player.inventory;
            for (final ItemStack stack : drops) {
                //collect directly while there is room in player's inventory
                playerInventory.insertStack(stack);
            }
            drops.removeIf(itemStack -> itemStack.getCount() == 0);
            Mod.LOGGER.info("done collecting into inventory!");
        }
        //scatter remaining drops
        Mod.LOGGER.info("scattering remaining items");
        if (!drops.isEmpty()) ItemScatterer.spawn(world, pos, drops);
        return ActionResult.SUCCESS;
    }

    @NotNull
    private static DefaultedList<ItemStack> getDrops(Item seedItem, List<ItemStack> dropList) {
        final DefaultedList<ItemStack> drops = DefaultedList.of();
        drops.addAll(dropList);
        for (final ItemStack stack : drops) {
            if (stack.getItem() == seedItem) {
                final ItemStack seedStack = stack.copy();
                drops.remove(stack);
                seedStack.decrement(1);
                drops.add(seedStack);
                break;
            }
        }
        return drops;
    }
}
