package coolguy.kutikiplayz.mixin.accessor;

import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.MultiPhase.class)
public interface MultiPhaseAccessor {
    @Accessor("phases") RenderLayer.MultiPhaseParameters phases();
}
