package com.creamsicle42.heavypower.block.custom.misc;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IMachineHatchBlock {

    /**
     * Get the item stack to be returned when the machine associated with this block is deconstructed
     * @return The Item Stack to drop
     */
    public ItemStack getDeconstructionItemStack();

    /**
     * Get the blockstate to replace this block with when machine broken
     * @return The Blockstate
     */
    public BlockState getDeconstructionBlockstate();

}
