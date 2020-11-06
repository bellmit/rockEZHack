package com.gamesense.client.module.modules.movement;

import com.gamesense.api.event.events.PlayerMoveEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.MotionUtils;
import com.gamesense.api.util.world.Timer;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.*;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

/**
 * Rewrote by Hoosiers on 11/1/2020
 *
 * @author Crystallinqq/Auto for original code
 * @source https://github.com/Crystallinqq/Mercury-Client/blob/master/src/main/java/fail/mercury/client/client/modules/movement/Speed.java
 */

public class Speed extends Module {
	public Speed(){
		super("Speed", Category.Movement);
	}

	Setting.Boolean iceSpeed;
	Setting.Boolean timerBool;
	Setting.Double timerVal;
	Setting.Double jumpHeight;
	Setting.Mode mode;

	public void setup(){
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Strafe");
		modes.add("Fake");
		modes.add("YPort");

		mode = registerMode("Mode", "Mode", modes, "Strafe");
		jumpHeight = registerDouble("Jump Speed", "JumpSpeed", 0.41, 0, 1);
		timerBool = registerBoolean("Timer", "Timer", true);
		timerVal = registerDouble("Timer Speed", "TimerSpeed", 1.15, 1, 1.5);
		iceSpeed = registerBoolean("Ice", "Ice", true);
	}

	private boolean slowDown;
	private boolean isOnIce = false;
	private double playerSpeed;
	private Timer timer = new Timer();

	public void onEnable(){
		GameSenseMod.EVENT_BUS.subscribe(this);
		playerSpeed = MotionUtils.getBaseMoveSpeed();
	}

	public void onDisable(){
		GameSenseMod.EVENT_BUS.unsubscribe(this);
		timer.reset();
		EntityUtil.resetTimer();
	}

	public void onUpdate(){
		if (mc.player == null || mc.world == null){
			disable();
			return;
		}

		BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).down();

		if (iceSpeed.getValue() && (mc.world.getBlockState(blockPos).getBlock() instanceof BlockIce || mc.world.getBlockState(blockPos).getBlock() instanceof BlockPackedIce)){
			isOnIce = true;
			MotionUtils.setSpeed(mc.player, MotionUtils.getBaseMoveSpeed() + (mc.player.isPotionActive(MobEffects.SPEED) ? (mc.player.ticksExisted % 2 == 0 ? 0.7 : 0.1) : 0.4));
		}
		else {
			if (mode.getValue().equalsIgnoreCase("YPort")){
				handleYPortSpeed();
			}
		}
	}

	private void handleYPortSpeed(){
		if (!MotionUtils.isMoving(mc.player) || mc.player.isInWater() && mc.player.isInLava() || mc.player.collidedHorizontally){
			return;
		}

		if (mc.player.onGround) {
			EntityUtil.setTimer(1.15f);
			mc.player.jump();
			MotionUtils.setSpeed(mc.player, MotionUtils.getBaseMoveSpeed() + (isOnIce ? 0.3 : 0.06));
		}
		else {
			mc.player.motionY = -1;
			EntityUtil.resetTimer();
		}
	}

	@EventHandler
	private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener<>(event -> {
		if (!isOnIce){
			if (mc.player.isInLava() || mc.player.isInWater() || mc.player.isOnLadder() || mc.player.isInWeb){
				return;
			}

			if (mode.getValue().equalsIgnoreCase("Strafe")){
				double speedY = jumpHeight.getValue();

				if (mc.player.onGround && MotionUtils.isMoving(mc.player) && timer.hasReached(300)){
					EntityUtil.setTimer((float)timerVal.getValue());
					if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)){
						speedY += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
					}

					event.setY(mc.player.motionY = speedY);
					playerSpeed = MotionUtils.getBaseMoveSpeed() * (EntityUtil.isColliding(0, -0.5, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.9 : 1.901);
					slowDown = true;
					timer.reset();
				}
				else{
					EntityUtil.resetTimer();
					if (slowDown || mc.player.collidedHorizontally){
						playerSpeed -= (EntityUtil.isColliding(0, -0.8, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.4 : 0.7 * (playerSpeed = MotionUtils.getBaseMoveSpeed());
						slowDown = false;
					}
					else{
						playerSpeed -= playerSpeed / 159.0;
					}
				}
				playerSpeed = Math.max(playerSpeed, MotionUtils.getBaseMoveSpeed());
				double[] dir = MotionUtils.forward(playerSpeed);
				event.setX(dir[0]);
				event.setZ(dir[1]);
			}
		}
	});

	public String getHudInfo(){
		String t = "";
		if (mode.getValue().equalsIgnoreCase("Strafe")){
			t = "[" + ChatFormatting.WHITE + "Strafe" + ChatFormatting.GRAY + "]";
		}
		else if (mode.getValue().equalsIgnoreCase("YPort")){
			t = "[" + ChatFormatting.WHITE + "YPort" + ChatFormatting.GRAY + "]";
		}
		else if (mode.getValue().equalsIgnoreCase("Fake")){
			t = "[" + ChatFormatting.WHITE + "Fake" + ChatFormatting.GRAY + "]";
		}
		return t;
	}
}