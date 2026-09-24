package coolguy.kutikiplayz.extension;

import net.minecraft.client.gl.Framebuffer;
import org.jetbrains.annotations.Nullable;

public interface CodeWorldRenderer {
    @Nullable Framebuffer code_breaker_origin$getCodeEntitiesFramebuffer();
    void code_breaker_origin$drawCodeEntitiesFramebuffer();
}
