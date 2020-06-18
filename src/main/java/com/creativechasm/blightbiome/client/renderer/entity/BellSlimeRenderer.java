package com.creativechasm.blightbiome.client.renderer.entity;

import com.creativechasm.blightbiome.client.renderer.entity.layers.BellSlimeSproutLayer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;

public class BellSlimeRenderer extends SlimeRenderer {

    public BellSlimeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        addLayer(new BellSlimeSproutLayer<>(this));
    }
}
