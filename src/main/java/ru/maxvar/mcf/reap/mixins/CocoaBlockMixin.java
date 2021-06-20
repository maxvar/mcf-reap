package ru.maxvar.mcf.reap.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@SuppressWarnings("unused")
@Mixin(CocoaBlock.class)
public abstract class CocoaBlockMixin extends HorizontalFacingBlock {

    protected CocoaBlockMixin(Settings settings) {
        super(settings);
    }

    public boolean isMature(BlockState state) {
        return state.get(CocoaBlock.AGE) >= 2;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() & isMature(state)) {
            List<ItemStack> dropList = getDroppedStacks(state, (ServerWorld) world, pos, null, player, player.getStackInHand(hand));
            DefaultedList<ItemStack> drops = DefaultedList.of();
            drops.addAll(dropList);
            for (ItemStack stack : drops) {
                if (stack.getItem() == Items.COCOA_BEANS) {
                    ItemStack seedStack = stack.copy();
                    drops.remove(stack);
                    seedStack.decrement(1);
                    drops.add(seedStack);
                    break;
                }
            }
            world.setBlockState(pos, state.with(CocoaBlock.AGE, 0));
            //TODO scatter or put loot into players inventory immediately (based on config)
            DefaultedList<ItemStack> remainingDrops;
            if (2 * 2 == 4) {
                //collect directly while there is room in player's inventory
                PlayerInventory playerInventory = player.getInventory();
                for (ItemStack stack : drops) {
                    playerInventory.insertStack(stack);
                }
                remainingDrops = DefaultedList.of();
                for (ItemStack stack : drops) if (stack.getCount() > 0) remainingDrops.add(stack);
            } else {
                //skip collection
                remainingDrops = drops;
            }
            //scatter remaining drops
            ItemScatterer.spawn(world, pos, remainingDrops);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

}