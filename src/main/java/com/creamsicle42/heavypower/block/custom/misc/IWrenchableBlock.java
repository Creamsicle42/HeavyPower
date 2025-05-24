package com.creamsicle42.heavypower.block.custom.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;


public interface IWrenchableBlock {
    public void setFacing(Level level, BlockPos pos, Direction dir);
}
