package com.creamsicle42.heavypower;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ModTags {
    public static final TagKey<Block> TURBINE_BLOCKS = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "turbine_blocks")
    );
    public static final TagKey<Block> TURBINE_COILS = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "turbine_coils")
    );
    public static final TagKey<Block> TURBINE_CASINGS = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "turbine_casings")
    );
    public static final TagKey<Item> TURBINE_STAGES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "turbine_stages")
    );
    public static final TagKey<Item> REACTOR_RODS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(HeavyPower.MODID, "reactor_rods")
    );

    public static final TagKey<Block> FISSION_REACTOR_BLOCKS = TagKey.create(
            Registries.BLOCK,
            HeavyPower.modResource("fission_reactor_blocks")
    );
    public static final TagKey<Block> FISSION_REACTOR_CASINGS = TagKey.create(
            Registries.BLOCK,
            HeavyPower.modResource("fission_reactor_casings")
    );
    public static final TagKey<Block> FISSION_REACTOR_TOPPERS = TagKey.create(
            Registries.BLOCK,
            HeavyPower.modResource("fission_reactor_toppers")
    );

    public static final TagKey<Block> EVAPORATION_TOWER_CONDENSERS = TagKey.create(
            Registries.BLOCK,
            HeavyPower.modResource("evaporation_tower_condensers")
    );

    public static final TagKey<Block> TIER_ONE_GENERIC_MACHINE_BLOCKS = TagKey.create(
            Registries.BLOCK,
            HeavyPower.modResource("tier_one_machine_blocks")
    );

    public static final TagKey<Block> TIER_ONE_HATCH_BLOCKS = TagKey.create(
            Registries.BLOCK,
            HeavyPower.modResource("tier_one_hatch_blocks")
    );

    public static final TagKey<Fluid> FISSION_REACTOR_COOLANTS = TagKey.create(
            Registries.FLUID,
            HeavyPower.modResource("fission_reactor_coolants")
    );
}
