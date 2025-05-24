package com.creamsicle42.heavypower.data;

import com.creamsicle42.heavypower.block.ModBlocks;
import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HeavyPower.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.TURBINE_COILS)
                .add(ModBlocks.COPPER_DYNAMO.get())
                .add(ModBlocks.BLANK_DYNAMO.get());
        tag(ModTags.TURBINE_CASINGS)
                .add(ModBlocks.TURBINE_CASING.get());
        tag(ModTags.TURBINE_BLOCKS)
                .addTag(ModTags.TURBINE_COILS)
                .addTag(ModTags.TURBINE_CASINGS)
                .add(ModBlocks.TURBINE_CORE.get());

        tag(ModTags.FISSION_REACTOR_CASINGS)
                .add(ModBlocks.RADIATION_PROOF_CASING.get());
        tag(ModTags.FISSION_REACTOR_TOPPERS)
                .add(ModBlocks.MECHANIZED_RADIATION_PROOF_CASING.get());
        tag(ModTags.FISSION_REACTOR_BLOCKS)
                .addTag(ModTags.FISSION_REACTOR_CASINGS)
                .addTag(ModTags.FISSION_REACTOR_TOPPERS);

        tag(ModTags.EVAPORATION_TOWER_CONDENSERS)
                .add(ModBlocks.STEEL_MESH_CASING.get());

        tag(ModTags.TIER_ONE_GENERIC_MACHINE_BLOCKS)
                .add(ModBlocks.TIER_ONE_CASING.get())
                .add(ModBlocks.AUX_TIER_ONE_CASING.get())
                .add(ModBlocks.MECHANIZED_TIER_ONE_CASING.get());

        tag(ModTags.TIER_ONE_HATCH_BLOCKS)
                .add(ModBlocks.TIER_ONE_CASING.get())
                .add(ModBlocks.CENTRIFUGE_CASING.get());
    }
}
