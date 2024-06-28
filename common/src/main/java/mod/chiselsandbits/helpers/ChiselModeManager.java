package mod.chiselsandbits.helpers;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ReflectionWrapper;
import mod.chiselsandbits.interfaces.IChiselModeItem;
import mod.chiselsandbits.modes.ChiselMode;
import mod.chiselsandbits.modes.IToolMode;
import mod.chiselsandbits.modes.PositivePatternMode;
import mod.chiselsandbits.network.packets.PacketSetChiselMode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class ChiselModeManager {
    private static ChiselMode clientChiselMode = ChiselMode.SINGLE;
    private static ChiselMode clientBitMode = ChiselMode.SINGLE;

    public static void changeChiselMode(
            final ChiselToolType tool, final IToolMode originalMode, final IToolMode newClientChiselMode) {
        final boolean chatNotification =
                ChiselsAndBits.getConfig().getClient().chatModeNotification.get();
        final boolean itemNameModeDisplay =
                ChiselsAndBits.getConfig().getClient().itemNameModeDisplay.get();

        if (ChiselsAndBits.getConfig().getClient().perChiselMode.get() && tool.hasPerToolSettings()
                || tool.requiresPerToolSettings()) {
            final PacketSetChiselMode packet = new PacketSetChiselMode(newClientChiselMode, tool, chatNotification);

            if (!itemNameModeDisplay) {
                newClientChiselMode.setMode(Minecraft.getInstance().player.getHeldItemMainhand());
            }

            ChiselsAndBits.getNetworkChannel().sendToServer(packet);
        } else {
            if (tool == ChiselToolType.CHISEL) {
                clientChiselMode = (ChiselMode) newClientChiselMode;
            } else {
                clientBitMode = (ChiselMode) newClientChiselMode;
            }

            if (originalMode != newClientChiselMode && chatNotification) {
                Minecraft.getInstance()
                        .player
                        .sendMessage(
                                new TranslationTextComponent(
                                        newClientChiselMode.getName().toString()),
                                Util.DUMMY_UUID);
            }

            ReflectionWrapper.instance.clearHighlightedStack();
        }

        if (!itemNameModeDisplay) {
            ReflectionWrapper.instance.endHighlightedStack();
        }
    }

    public static void scrollOption(
            final ChiselToolType tool, final IToolMode originalMode, IToolMode currentMode, final int dwheel) {
        int offset = currentMode.ordinal() + (dwheel < 0 ? -1 : 1);

        if (offset >= ChiselMode.values().length) {
            offset = 0;
        }

        if (offset < 0) {
            offset = ChiselMode.values().length - 1;
        }

        currentMode = ChiselMode.values()[offset];

        if (currentMode.isDisabled()) {
            scrollOption(tool, originalMode, currentMode, dwheel);
        } else {
            changeChiselMode(tool, originalMode, currentMode);
        }
    }

    public static IToolMode getChiselMode(final PlayerEntity player, final ChiselToolType setting, final Hand hand) {
        if (setting == ChiselToolType.TAPEMEASURE || setting == ChiselToolType.POSITIVEPATTERN) {
            final ItemStack ei = player.getHeldItem(hand);
            if (ei != null && ei.getItem() instanceof IChiselModeItem) {
                return setting.getMode(ei);
            }

            return PositivePatternMode.REPLACE;
        } else if (setting == ChiselToolType.CHISEL) {
            if (ChiselsAndBits.getConfig().getClient().perChiselMode.get()) {
                final ItemStack ei = player.getHeldItemMainhand();
                if (ei != null && ei.getItem() instanceof IChiselModeItem) {
                    return setting.getMode(ei);
                }
            }

            return clientChiselMode;
        } else if (setting == ChiselToolType.BIT) {
            return clientBitMode;
        }

        return ChiselMode.SINGLE;
    }
}
