package com.creamsicle42.heavypower.data;

import com.creamsicle42.heavypower.ModTags;
import com.creamsicle42.heavypower.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.TURBINE_STAGES)
                .add(ModItems.SMALL_IRON_TURBINE_BLADE.get());
        tag(ModTags.REACTOR_RODS)
                .add(ModItems.GRAPHITE_ROD.get())
                .add(ModItems.MODERATOR_ROD.get())
                .add(ModItems.HEAT_EXCHANGER_ROD.get())
                .add(ModItems.URANIUM_ROD.get());
    }
}
