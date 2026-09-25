package coolguy.kutikiplayz.mixin.shader;

import coolguy.kutikiplayz.render.CodeRenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StuckStingersFeatureRenderer.class)
public abstract class StuckStingersFeatureRendererMixin {
    @ModifyArg(method = "renderObject", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private RenderLayer render(RenderLayer layer) {
        return CodeRenderLayers.getEntityCutoutNoCullNoCode(CodeRenderLayers.getTextureFromRenderLayer(layer));
    }
}
