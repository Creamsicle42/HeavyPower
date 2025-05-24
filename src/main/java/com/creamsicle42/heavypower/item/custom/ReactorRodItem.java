package com.creamsicle42.heavypower.item.custom;

import com.creamsicle42.heavypower.registry.ModDataRegistries;
import com.creamsicle42.heavypower.registry.types.ReactorMaterial;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ReactorRodItem extends Item {

    private ReactorMaterial reactorMaterial;
    private final ResourceLocation reactorMaterialLocation;

    public ReactorRodItem(Properties pProperties, ResourceLocation reactorMaterial) {
        super(pProperties);
        this.reactorMaterialLocation = reactorMaterial;
    }

    public ReactorMaterial getReactorMaterial(Level pLevel) {
        if (reactorMaterial == null) {
            RegistryAccess access = pLevel.registryAccess();
            reactorMaterial = access.registry(ModDataRegistries.REACTOR_MATERIAL).get().get(reactorMaterialLocation);
        }
        return reactorMaterial;
    }

    public ReactorMaterial getReactorMaterial(HolderLookup.Provider pProvider) {
        if (reactorMaterial == null) {
            Optional<HolderLookup.RegistryLookup<ReactorMaterial>> lookup =
                    pProvider.lookup(ModDataRegistries.REACTOR_MATERIAL);
            if (lookup.isEmpty()) {return ReactorMaterial.BLANK;}
            Optional<Holder.Reference<ReactorMaterial>> lookupResult = lookup.get().get(ResourceKey.create(ModDataRegistries.REACTOR_MATERIAL, reactorMaterialLocation));
            if (lookupResult.isEmpty()) {return ReactorMaterial.BLANK;}
            reactorMaterial = lookupResult.get().value();
        }
        return reactorMaterial;
    }
}
