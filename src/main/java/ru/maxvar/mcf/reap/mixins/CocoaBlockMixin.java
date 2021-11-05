package ru.maxvar.mcf.reap.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import ru.maxvar.mcf.reap.ReapHelper;

@SuppressWarnings("unused")
@Mixin(CocoaBlock.class)
public abstract class CocoaBlockMixin extends HorizontalFacingBlock {

    protected CocoaBlockMixin(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(CocoaBlock.AGE) >= 2)
            if (world.isClient()) {
                player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            } else {
                return ReapHelper.reap(state, world, pos, player, hand, Items.COCOA_BEANS, CocoaBlock.AGE);
            }
        return super.onUse(state, world, pos, player, hand, hit);
    }

}