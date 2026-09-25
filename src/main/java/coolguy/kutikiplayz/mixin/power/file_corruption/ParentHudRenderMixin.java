package coolguy.kutikiplayz.mixin.power.file_corruption;

import coolguy.kutikiplayz.extension.power.CodeHudRender;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.hud_render.ParentHudRender;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ParentHudRender.class)
public abstract class ParentHudRenderMixin {
    @Redirect(method = "lambda$getActive$1", at = @At(value = "INVOKE", target = "Lio/github/apace100/apoli/util/HudRender;shouldRender(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean allowFade(HudRender hudRender, Entity viewer) {
        if (hudRender.shouldRender() && ((CodeHudRender) hudRender).code_breaker_origin$shouldFade())
            return true;
        return hudRender.shouldRender(viewer);
    }
}
