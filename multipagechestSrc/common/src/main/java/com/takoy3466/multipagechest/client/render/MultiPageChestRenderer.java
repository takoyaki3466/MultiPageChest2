package com.takoy3466.multipagechest.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.takoy3466.multipagechest.MultiPageChestCommon;
import com.takoy3466.multipagechest.block.MultiPageChestBlock;
import com.takoy3466.multipagechest.block.entity.MultiPageChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Draws the chest in the world with the vanilla single chest model (as the 1.7.10 version did with
 * {@code ModelChest}) and this mod's own texture, animating the lid like the vanilla chest renderer.
 */
public class MultiPageChestRenderer implements BlockEntityRenderer<MultiPageChestBlockEntity> {

    private static final ResourceLocation TEXTURE = MultiPageChestCommon.id("textures/entity/multipagechest.png");
    private static final float MAX_LID_ANGLE = (float) (Math.PI / 2);

    private final ModelPart lid;
    private final ModelPart lock;
    private final ModelPart bottom;

    public MultiPageChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = root.getChild("bottom");
        this.lid = root.getChild("lid");
        this.lock = root.getChild("lock");
    }

    @Override
    public void render(MultiPageChestBlockEntity chest, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float facingRotation = chest.getBlockState().getValue(MultiPageChestBlock.FACING).toYRot();
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facingRotation));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        this.lid.xRot = -(easeLidOpenness(chest.getOpenNess(partialTick)) * MAX_LID_ANGLE);
        this.lock.xRot = this.lid.xRot;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        this.lid.render(poseStack, consumer, packedLight, packedOverlay);
        this.lock.render(poseStack, consumer, packedLight, packedOverlay);
        this.bottom.render(poseStack, consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    /** Same easing curve as the vanilla chest renderer. */
    private static float easeLidOpenness(float openness) {
        float closedness = 1.0F - openness;
        return 1.0F - closedness * closedness * closedness;
    }
}
