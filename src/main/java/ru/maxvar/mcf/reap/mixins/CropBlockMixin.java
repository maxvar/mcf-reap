package ru.maxvar.mcf.reap.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.maxvar.mcf.reap.ReapHelper;

import static net.minecraft.block.CropBlock.AGE;

@SuppressWarnings("unused")
@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock {

    protected CropBlockMixin(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("SameReturnValue")
    @Shadow
    public boolean isMature(BlockState state) {
        return true;
    }

    @SuppressWarnings("SameReturnValue")
    @Shadow
    public IntProperty getAgeProperty() {
        return AGE;
    }

    @SuppressWarnings("SameReturnValue")
    @Shadow
    public ItemConvertible getSeedsItem() {
        return Items.WHEAT_SEEDS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (isMature(state))
            if (world.isClient()) {
                player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            } else {
                return ReapHelper.reap(state, world, pos, player, hand, getSeedsItem().asItem(), getAgeProperty());
            }
        return super.onUse(state, world, pos, player, hand, hit);
    }

}