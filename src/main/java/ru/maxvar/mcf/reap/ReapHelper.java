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

import java.util.List;

public class ReapHelper {
    @SuppressWarnings("SameReturnValue")
    @NotNull
    public static ActionResult reap(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Item seedItem, IntProperty ageProperty) {
        //get loot from the plant block
        List<ItemStack> dropList = Block.getDroppedStacks(state, (ServerWorld) world, pos, null, player, player.getStackInHand(hand));
        //remove one seed from the loot
        DefaultedList<ItemStack> drops = DefaultedList.of();
        drops.addAll(dropList);
        for (ItemStack stack : drops) {
            if (stack.getItem() == seedItem) {
                ItemStack seedStack = stack.copy();
                drops.remove(stack);
                seedStack.decrement(1);
                drops.add(seedStack);
                break;
            }
        }
        //reduce to new plant
        world.setBlockState(pos, state.with(ageProperty, 0));
        //try to put loot into players inventory
        DefaultedList<ItemStack> remainingDrops;
        PlayerInventory playerInventory = player.getInventory();
        for (ItemStack stack : drops) {
            //collect directly while there is room in player's inventory
            playerInventory.insertStack(stack);
        }
        remainingDrops = DefaultedList.of();
        for (ItemStack stack : drops) if (stack.getCount() > 0) remainingDrops.add(stack);
        //scatter remaining drops
        if (!remainingDrops.isEmpty()) ItemScatterer.spawn(world, pos, remainingDrops);
        return ActionResult.SUCCESS;

    }
}
