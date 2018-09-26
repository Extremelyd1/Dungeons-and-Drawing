package graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {
    private final int resolution;
    private final int depthMapFBO;
    private final Texture depthMap;

    public ShadowMap(int resolution) throws Exception{
        this.resolution = resolution;

        // Create depth map texture
        depthMap = new Texture(resolution, resolution, GL_DEPTH_COMPONENT);

        // Create FBO
        depthMapFBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        depthMap.bind();
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D,
                depthMap.getId(), 0);

        // Error Check
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new Exception("ShadowMap could not create FrameBuffer");

        // Unbind Depth Map and FBO
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public Texture getDepthMap() {
        return depthMap;
    }

    public int getResolution() {
        return resolution;
    }

    public void cleanup() {
        // Delete resources
        glDeleteFramebuffers(depthMapFBO);
        depthMap.cleanup();
    }
}
