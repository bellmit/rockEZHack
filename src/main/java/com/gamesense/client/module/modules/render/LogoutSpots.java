package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.PlayerJoinEvent;
import com.gamesense.api.event.events.PlayerLeaveEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.GameSenseTessellator;
import com.gamesense.api.util.world.GeometryMasks;
import com.gamesense.api.util.world.Timer;
import com.gamesense.client.GameSenseMod;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.WorldEvent;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rewrote by Hoosiers on 10/30/20
 */

public class LogoutSpots extends Module {
	public LogoutSpots(){
		super("LogoutSpots", Category.Render);
	}

	Setting.Boolean chatMsg;
	Setting.Boolean nameTag;
	Setting.Integer lineWidth;
	Setting.Integer range;
	Setting.Mode renderMode;
	Setting.ColorSetting color;

	public void setup(){
		ArrayList<String> renderModes = new ArrayList<>();
		renderModes.add("Both");
		renderModes.add("Outline");
		renderModes.add("Fill");

		range = registerInteger("Range", "Range", 100, 10, 260);
		chatMsg = registerBoolean("Chat Msgs", "ChatMsgs", true);
		nameTag = registerBoolean("Nametag", "Nametag", true);
		lineWidth = registerInteger("Width", "Width", 1, 1, 10);
		renderMode = registerMode("Render", "Render", renderModes, "Both");
		color = registerColor("Color", "Color", new GSColor(255, 0, 0, 255));
	}

	Map<net.minecraft.entity.Entity, String> loggedPlayers = new ConcurrentHashMap<>();
	List<EntityPlayer> worldPlayers = new ArrayList<>();
	Timer timer = new Timer();

	public void onUpdate(){
		mc.world.playerEntities.stream()
				.filter(entityPlayer -> entityPlayer != mc.player)
				.filter(entityPlayer -> entityPlayer.getDistance(mc.player) <= range.getValue())
				.forEach(entityPlayer -> worldPlayers.add(entityPlayer));
	}

	public void onWorldRender(RenderEvent event){
		if (mc.player != null && mc.world != null){
			loggedPlayers.forEach((entity, string) -> {
				startFunction(entity, string);
			});
		}
	}

	public void onEnable(){
		loggedPlayers.clear();
		worldPlayers = new ArrayList<>();
		GameSenseMod.EVENT_BUS.subscribe(this);
	}

	public void onDisable(){
		worldPlayers.clear();
		GameSenseMod.EVENT_BUS.unsubscribe(this);
	}

	private void startFunction(Entity entity, String string){
		if (entity.getDistance(mc.player) > range.getValue()){
			return;
		}

		int posX = (int) entity.posX;
		int posY = (int) entity.posY;
		int posZ = (int) entity.posZ;

		String[] nameTagMessage = new String[2];
		nameTagMessage[0] = entity.getName() + " (" + string + ")";
		nameTagMessage[1] = "(" + posX + "," + posY + "," + posZ + ")";

		GlStateManager.pushMatrix();
		GameSenseTessellator.drawNametag(entity, nameTagMessage, color.getValue(),0);

		switch (renderMode.getValue()){
			case "Both": {
				GameSenseTessellator.drawBoundingBox(entity.getRenderBoundingBox(), lineWidth.getValue(), color.getValue());
				GameSenseTessellator.drawBox(entity.getRenderBoundingBox(), true, -0.4,  new GSColor(color.getValue(), 50), GeometryMasks.Quad.ALL);
				break;
			}
			case "Outline": {
				GameSenseTessellator.drawBoundingBox(entity.getRenderBoundingBox(), lineWidth.getValue(), color.getValue());
				break;
			}
			case "Fill": {
				GameSenseTessellator.drawBox(entity.getRenderBoundingBox(), true, -0.4,  new GSColor(color.getValue(), 50), GeometryMasks.Quad.ALL);
				break;
			}
		}
		GlStateManager.popMatrix();
	}

	/** event handlers below: **/

	@EventHandler
	private final Listener<PlayerJoinEvent> playerJoinEventListener1 = new Listener<>(event -> {
		if (mc.world != null) {
			loggedPlayers.forEach((entity, string) -> {
				if (entity.getName().equalsIgnoreCase(event.getName())) {
					loggedPlayers.remove(entity);

					if (chatMsg.getValue()) {
						MessageBus.sendClientPrefixMessage(event.getName() + " reconnected!");
					}
				}
			});
		}
	});

	@EventHandler
	private final Listener<PlayerLeaveEvent> playerLeaveEventListener2 = new Listener<>(event -> {
		if (mc.world != null) {
			worldPlayers.forEach(entity -> {
				if (entity.getName().equalsIgnoreCase(event.getName()) && !(loggedPlayers.containsKey(entity.getName()))) {
					String date = new SimpleDateFormat("k:mm").format(new Date());
					loggedPlayers.put(entity, date);
					worldPlayers.remove(entity);

					if (chatMsg.getValue() && timer.getTimePassed() / 50L >= 5) {
						String location = "(" + (int) entity.posX + "," + (int) entity.posY + "," + (int) entity.posZ + ")";
						MessageBus.sendClientPrefixMessage(event.getName() + " disconnected at " + location + "!");
						timer.reset();
					}
				}
			});
		}
	});

	@EventHandler
	private final Listener<WorldEvent.Unload> unloadListener1 = new Listener<>(event -> {
		worldPlayers.clear();
		if (mc.player == null || mc.world == null){
			loggedPlayers.clear();
		}
	});

	@EventHandler
	private final Listener<WorldEvent.Load> unloadListener2 = new Listener<>(event -> {
		worldPlayers.clear();
		if (mc.player == null || mc.world == null){
			loggedPlayers.clear();
		}
	});
}