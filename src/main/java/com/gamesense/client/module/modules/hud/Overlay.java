package com.gamesense.client.module.modules.hud;

import org.lwjgl.opengl.GL11;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.util.misc.Wrapper;

public class Overlay extends Module {
    public Overlay(){
        super("Overlay", Category.HUD);
    }

    Setting.Boolean welcomer;
    Setting.Boolean watermark;
    Setting.Integer welcomerX;
    Setting.Integer welcomerY;
    Setting.Integer watermarkX;
    Setting.Integer watermarkY;
    Setting.Boolean rusherbuy;
    Setting.ColorSetting color;

    public void setup(){
        welcomer = registerBoolean("Welcomer", "Welcomer", true);
        welcomerX = registerInteger("Welcomer X", "WelcomerX", 450, 0, 1000);
        welcomerY = registerInteger("Welcomer Y", "WelcomerY", 0, 0, 1000);
        watermark = registerBoolean("Watermark", "Watermark", true);
        rusherbuy = registerBoolean("BUY RUSHER", "BUY RUSHERHACK WITH rockez PROMO CODE", true);
        watermarkX = registerInteger("Watermark X", "WatermarkX", 0, 0, 1000);
        watermarkY = registerInteger("Watermark Y", "WatermarkY", 0, 0, 1000);
        color = registerColor("Color", "Color", new GSColor(255, 0, 0, 255));
    }

    public void onRender(){
        if (rusherbuy.getValue()){
    		GL11.glPushMatrix();
    		GL11.glScalef(2.5f, 2.5f, 2.5f);
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, watermarkX.getValue(), watermarkY.getValue(), color.getValue());
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, 10, 10, color.getValue());
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, 10, 30, color.getValue());
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, 10, 50, color.getValue());
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, 10, 70, color.getValue());
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, 10, 90, color.getValue());
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, 10, 110, color.getValue());
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, 10, 130, color.getValue());
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.RUSHER, 10, 160, color.getValue());
    		GL11.glPopMatrix();
        }

        if (welcomer.getValue()){
            FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "Hello " + mc.player.getName() + " :^)", welcomerX.getValue(), welcomerY.getValue(), color.getValue());
        }
        if (watermark.getValue()){
    		GL11.glPushMatrix();
    		GL11.glScalef(1.5f, 1.5f, 1.5f);
    		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), GameSenseMod.MODNAME, watermarkX.getValue(), watermarkY.getValue(), color.getValue());
    		GL11.glPopMatrix();
    }
    }
}