package coolguy.kutikiplayz.render;

import net.minecraft.client.gl.ShaderProgram;
import org.jetbrains.annotations.Nullable;

public class CodeGameRenderer {
    @Nullable private static ShaderProgram renderTypeCodeProgram;

    public static @Nullable ShaderProgram getRenderTypeCodeProgram() {
        return renderTypeCodeProgram;
    }
    public static void setRenderTypeCodeProgram(@Nullable ShaderProgram codeProgram) {
        renderTypeCodeProgram = codeProgram;
    }
}
