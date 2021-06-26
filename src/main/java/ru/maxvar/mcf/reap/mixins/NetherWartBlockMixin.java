package ru.maxvar.mcf.reap.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
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

@SuppressWarnings("unused")
@Mixin(NetherWartBlock.class)
public abstract class NetherWartBlockMixin extends PlantBlock {

    protected NetherWartBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public static final IntProperty AGE = Properties.AGE_3;

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() & state.get(AGE) >= 3) {
            return ReapHelper.reap(state, world, pos, player, hand, Items.NETHER_WART, AGE);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

}