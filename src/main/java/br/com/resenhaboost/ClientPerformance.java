package br.com.resenhaboost;

import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ParticleStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public final class ClientPerformance {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(ResenhaBoost.MOD_ID, "main"));
    private static final KeyMapping TOGGLE = new KeyMapping(
            "key.resenhaboost.toggle", Type.KEYSYM, GLFW.GLFW_KEY_F8, CATEGORY);
    private static Snapshot original;
    private static boolean enabled;
    private ClientPerformance() {}

    @Mod.EventBusSubscriber(modid = ResenhaBoost.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBusEvents {
        private ModBusEvents() {}
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) { event.register(TOGGLE); }
    }

    @Mod.EventBusSubscriber(modid = ResenhaBoost.MOD_ID, value = Dist.CLIENT)
    public static final class ClientBusEvents {
        private ClientBusEvents() {}
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
            Minecraft minecraft = Minecraft.getInstance();
            while (TOGGLE.consumeClick()) {
                if (enabled) disable(minecraft);
                else enable(minecraft);
            }
        }
        @SubscribeEvent
        public static void onGameShutdown(GameShuttingDownEvent event) {
            if (enabled) disable(Minecraft.getInstance());
        }
    }

    private static void enable(Minecraft minecraft) {
        if (minecraft.options == null) return;
        original = Snapshot.capture(minecraft);
        minecraft.options.biomeBlendRadius().set(0);
        minecraft.options.cloudStatus().set(CloudStatus.OFF);
        minecraft.options.particles().set(ParticleStatus.MINIMAL);
        minecraft.options.entityShadows().set(false);
        minecraft.options.ambientOcclusion().set(false);
        minecraft.options.entityDistanceScaling().set(Math.min(original.entityDistanceScaling(), 0.75));
        minecraft.options.renderDistance().set(Math.min(original.renderDistance(), 8));
        minecraft.options.save();
        enabled = true;
        notify(minecraft, "ResenhaBoost: modo desempenho ativado. F8 restaura suas opções.");
    }

    private static void disable(Minecraft minecraft) {
        if (original == null) { enabled = false; return; }
        original.restore(minecraft);
        minecraft.options.save();
        original = null;
        enabled = false;
        notify(minecraft, "ResenhaBoost: suas opções gráficas originais foram restauradas.");
    }

    private static void notify(Minecraft minecraft, String message) {
        if (minecraft.gui != null) minecraft.gui.hud.setOverlayMessage(Component.literal(message), false);
    }

    private record Snapshot(int biomeBlendRadius, CloudStatus clouds, ParticleStatus particles,
            boolean entityShadows, boolean ambientOcclusion, double entityDistanceScaling, int renderDistance) {
        static Snapshot capture(Minecraft minecraft) {
            return new Snapshot(minecraft.options.biomeBlendRadius().get(), minecraft.options.cloudStatus().get(),
                    minecraft.options.particles().get(), minecraft.options.entityShadows().get(),
                    minecraft.options.ambientOcclusion().get(), minecraft.options.entityDistanceScaling().get(),
                    minecraft.options.renderDistance().get());
        }
        void restore(Minecraft minecraft) {
            minecraft.options.biomeBlendRadius().set(biomeBlendRadius);
            minecraft.options.cloudStatus().set(clouds);
            minecraft.options.particles().set(particles);
            minecraft.options.entityShadows().set(entityShadows);
            minecraft.options.ambientOcclusion().set(ambientOcclusion);
            minecraft.options.entityDistanceScaling().set(entityDistanceScaling);
            minecraft.options.renderDistance().set(renderDistance);
        }
    }
}