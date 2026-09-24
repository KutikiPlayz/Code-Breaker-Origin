package coolguy.kutikiplayz.mixin;

import coolguy.kutikiplayz.render.CodeRenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TridentRiptideFeatureRenderer.class)
public abstract class TridentRiptideFeatureRendererMixin {
    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private RenderLayer render(RenderLayer layer) {
        return CodeRenderLayers.getEntityCutoutNoCullNoCode(CodeRenderLayers.getTextureFromRenderLayer(layer));
    }
}
