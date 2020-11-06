package com.gamesense.api.util.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class Discord {
    public static DiscordRichPresence presence;
    private static final club.minnced.discord.rpc.DiscordRPC rpc;
    private static Thread thread;
    private static ServerData sip;

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("774257326990950450", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.details =  Minecraft.getMinecraft().getSession().getUsername() + " > rockezhack menu";
        presence.state = "Beta 0.1";
        presence.largeImageKey = "rockez";
        rpc.Discord_UpdatePresence(presence);
        thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    rpc.Discord_RunCallbacks();
                    presence.details = (Minecraft.getMinecraft() + " > rockezhack menu");
                    presence.state = "rockEZHack b1";
                        if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
                            presence.details = (Minecraft.getMinecraft().getSession().getUsername() + " > thank oyzipfile");
                            presence.state = "Beta 0.1";
                        } else if (Minecraft.getMinecraft().getCurrentServerData() != null) {
                            sip = Minecraft.getMinecraft().getCurrentServerData();

                            if (!sip.serverIP.equals("")) {
                                presence.details = (Minecraft.getMinecraft().getSession().getUsername() + " > " + sip.serverIP);
                                presence.state = "Beta 0.1";
                            }
                        }
                  /*  switch (Client.settingManager.getSetting("RPC", "State").getEnumValue()) {
                        case "watermark":
                            presence.state = "Troll hack v1.0";
                            break;
                        case ".gg/xulu":
                            presence.state = "discord.gg/xulu";
                            break;
                        case "owns all":
                            presence.state = "Troll hack owns all";
                            break;
                        case "morgenpvp":
                            presence.state = "MorgenPvP on top!";
                            break;
                        default:
                            break;
                    } */
                    rpc.Discord_UpdatePresence(presence);
                }
                catch(Exception var2) {
                    var2.printStackTrace();
                }

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException var1) {
                }
            }

        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }

        rpc.Discord_Shutdown();
    }

    static {
        rpc = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }
}