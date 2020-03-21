package ru.maxvar.mcf.reap.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

import static net.minecraft.block.CropBlock.AGE;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock {

    protected CropBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public boolean isMature(BlockState state) {
        return true;
    }

    @Shadow
    protected ItemConvertible getSeedsItem() {
        return Items.DIAMOND_BLOCK;
    }

    @Shadow
    public IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() & isMature(state)) {
            List<ItemStack> dropList = getDroppedStacks(state, (ServerWorld) world, pos, null, player, player.getStackInHand(hand));
            DefaultedList<ItemStack> drops = DefaultedList.of();
            drops.addAll(dropList);

            for (ItemStack stack : drops) {
                if (stack.getItem() == this.getSeedsItem()) {
                    ItemStack seedStack = stack.copy();
                    drops.remove(stack);
                    seedStack.decrement(1);
                    drops.add(seedStack);
                    break;
                }
            }

            world.setBlockState(pos, state.with(this.getAgeProperty(), 0));
            ItemScatterer.spawn(world, pos, drops);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

}