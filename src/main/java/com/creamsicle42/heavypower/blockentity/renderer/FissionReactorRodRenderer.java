package com.creamsicle42.heavypower.blockentity.renderer;

import com.creamsicle42.heavypower.HeavyPower;
import com.creamsicle42.heavypower.blockentity.fissionreactor.FissionRodBlockEntity;
import com.creamsicle42.heavypower.blockentity.reactorcore.ReactorCoreBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class FissionReactorRodRenderer implements BlockEntityRenderer<FissionRodBlockEntity> {

    public FissionReactorRodRenderer(BlockEntityRendererProvider.Context ignoredContext) {

    }

    @Override
    public void render(FissionRodBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();


        for (int i = 0; i < 4; i ++) {
            ItemStack stack = blockEntity.heldItems.get(i);
            if (stack.isEmpty()) {
                continue;
            }


            poseStack.pushPose();
            poseStack.translate(0.25f + 0.5f * (i % 2), 1.5f, 0.25f + 0.5f * (float)(i / 2));
            poseStack.scale(0.5f, 0.5f, 0.5f);

            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, 255, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);
            poseStack.popPose();
        }
    }

}
