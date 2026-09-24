package coolguy.kutikiplayz.mixin.accessor;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public interface MultiPhaseParametersAccessor {
    @Accessor("texture") RenderPhase.TextureBase getTexture();
    @Accessor("cull") RenderPhase.Cull getCull();
}
