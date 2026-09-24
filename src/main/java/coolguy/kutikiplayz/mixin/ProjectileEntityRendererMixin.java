package coolguy.kutikiplayz.mixin;

import coolguy.kutikiplayz.render.CodeRenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ProjectileEntityRenderer.class)
public abstract class ProjectileEntityRendererMixin {
    @ModifyArg(method = "render(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private RenderLayer render(RenderLayer layer) {
        return CodeRenderLayers.getEntityCutoutNoCode(CodeRenderLayers.getTextureFromRenderLayer(layer));
    }
}
