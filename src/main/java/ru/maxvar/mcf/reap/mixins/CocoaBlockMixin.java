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
import ru.maxvar.mcf.reap.menu.ConfigManager;

@SuppressWarnings("unused")
@Mixin(CocoaBlock.class)
public abstract class CocoaBlockMixin extends HorizontalFacingBlock {

    protected CocoaBlockMixin(final Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        if (ConfigManager.getConfig().isEnabled()) {
            if (state.get(CocoaBlock.AGE) >= 2) {
                if (world.isClient()) {
                    if (ConfigManager.getConfig().mustPlaySound())
                        player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
                    return super.onUse(state, world, pos, player, hand, hit);
                } else {
                    return ReapHelper.reap(state, world, pos, player, hand, Items.COCOA_BEANS, CocoaBlock.AGE);
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

}