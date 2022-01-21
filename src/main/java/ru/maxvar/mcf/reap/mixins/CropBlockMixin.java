package ru.maxvar.mcf.reap.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.maxvar.mcf.reap.CropsInfo;
import ru.maxvar.mcf.reap.Mod;
import ru.maxvar.mcf.reap.ReapHelper;
import ru.maxvar.mcf.reap.menu.ConfigManager;

import static net.minecraft.block.CropBlock.AGE;

@SuppressWarnings("unused")
@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock {

    protected CropBlockMixin(final Settings settings) {
        super(settings);
    }

    @SuppressWarnings("SameReturnValue")
    @Shadow
    public boolean isMature(final BlockState state) {
        return true;
    }

    @SuppressWarnings("SameReturnValue")
    @Shadow
    public IntProperty getAgeProperty() {
        return AGE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        if (ConfigManager.getConfig().isEnabled()) {
            if (isMature(state)) {
                if (world.isClient()) {
                    if (ConfigManager.getConfig().mustPlaySound())
                    player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
                    return super.onUse(state, world, pos, player, hand, hit);
                } else {
                    return ReapHelper.reap(state, world, pos, player, hand, CropsInfo.getSeedsItem(this).asItem(), getAgeProperty());
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

}