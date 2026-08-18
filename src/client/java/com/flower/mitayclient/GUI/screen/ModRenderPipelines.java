package com.flower.mitayclient.GUI.screen;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;

public class ModRenderPipelines {
    public static final RenderPipeline GUI_GLINT = RenderPipelines.register(
            RenderPipeline.builder(
                            RenderPipelines.MATRICES_PROJECTION_SNIPPET,
                            RenderPipelines.FOG_SNIPPET,
                            RenderPipelines.GLOBALS_SNIPPET
                    )
                    .withLocation(Identifier.fromNamespaceAndPath("your_mod_id", "pipeline/gui_glint"))
                    .withVertexShader("core/glint")
                    .withFragmentShader("core/glint")
                    .withSampler("Sampler0")
                    .withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
                    .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
                    // 关键：不设置深度状态，GUI 中没有深度缓冲，测试会失败
                    .build()
    );

    public static void init() {}
}