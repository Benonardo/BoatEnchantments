package com.benonardo.boatenchantments.mixin;

import com.benonardo.boatenchantments.duck.EnchantableBoatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BoatEntityRenderer.class)
public class BoatEntityRendererMixin {

    @ModifyVariable(method = "render(Lnet/minecraft/entity/vehicle/BoatEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), argsOnly = true)
    private VertexConsumerProvider wrapWithGlint(VertexConsumerProvider original, BoatEntity boatEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (((EnchantableBoatEntity)boatEntity).isEnchanted()) {
            return layer -> VertexConsumers.union(original.getBuffer(RenderLayer.getDirectEntityGlint()), original.getBuffer(layer));
        }
        return original;
    }

}
