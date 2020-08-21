package com.creativechasm.cropcultivation.init;

import com.creativechasm.cropcultivation.client.gui.TabletScreen;
import net.minecraft.client.Minecraft;

public class ClientProxy implements IProxy
{
    @Override
    public void openTabletScreen() {
        Minecraft.getInstance().displayGuiScreen(new TabletScreen(Minecraft.getInstance()));
    }
}
