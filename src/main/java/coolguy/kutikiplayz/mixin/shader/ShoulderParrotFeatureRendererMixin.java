package coolguy.kutikiplayz.mixin.shader;

import coolguy.kutikiplayz.render.CodeRenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ShoulderParrotFeatureRenderer.class)
public abstract class ShoulderParrotFeatureRendererMixin {
    @ModifyArg(method = "method_17958", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private RenderLayer renderShoulderParrot(RenderLayer layer) {
        return CodeRenderLayers.getEntityCutoutNoCullNoCode(CodeRenderLayers.getTextureFromRenderLayer(layer));
    }
}
