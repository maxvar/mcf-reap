package ru.maxvar.mcf.reap.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.maxvar.mcf.reap.ReapHelper;
import ru.maxvar.mcf.reap.menu.ConfigManager;

@SuppressWarnings("unused")
@Mixin(NetherWartBlock.class)
public abstract class NetherWartBlockMixin extends PlantBlock {

    protected NetherWartBlockMixin(final Settings settings) {
        super(settings);
    }

    @Shadow
    public static final IntProperty AGE = Properties.AGE_3;

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult hit) {
        if (ConfigManager.getConfig().isEnabled()) {
            if (state.get(AGE) >= 3) {
                if (world.isClient()) {
                    if (ConfigManager.getConfig().mustPlaySound())
                        player.playSound(SoundEvents.ITEM_CROP_PLANT, 1.0f, 1.0f);
                    return super.onUse(state, world, pos, player, hand, hit);
                } else {
                    return ReapHelper.reap(state, world, pos, player, hand, Items.NETHER_WART, AGE);
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

}