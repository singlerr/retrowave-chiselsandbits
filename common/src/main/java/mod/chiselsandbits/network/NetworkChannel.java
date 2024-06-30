package mod.chiselsandbits.network;

import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Our wrapper for Forge network layer
 */
public class NetworkChannel {
    private static final String LATEST_PROTO_VER = "1.0";
    private static final String ACCEPTED_PROTO_VERS = LATEST_PROTO_VER;
    /**
     * Forge network channel
     */
    private final SimpleChannel rawChannel;

    /**
     * Creates a new instance of network channel.
     *
     * @param channelName unique channel name
     * @throws IllegalArgumentException if channelName already exists
     */
    public NetworkChannel(final String channelName) {
        rawChannel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("chiselsandbits", channelName),
                () -> LATEST_PROTO_VER,
                ACCEPTED_PROTO_VERS::equals,
                ACCEPTED_PROTO_VERS::equals);
    }

    /**
     * Registers all common messages.
     */
    public void registerCommonMessages() {
        ModPacketTypes.init(this);
    }

    /**
     * Register a message into rawChannel.
     *
     * @param <MSG>      message class type
     * @param id         network id
     * @param msgClazz   message class
     * @param msgCreator supplier with new instance of msgClazz
     */
    public <MSG extends ModPacket> void registerMessage(
            final int id, final Class<MSG> msgClazz, final Function<PacketBuffer, MSG> msgCreator) {
        rawChannel.registerMessage(id, msgClazz, ModPacket::getPayload, msgCreator, (msg, ctxIn) -> {
            final Context ctx = ctxIn.get();
            final LogicalSide packetOrigin = ctx.getDirection().getOriginationSide();
            ctx.setPacketHandled(true);
            // boolean param MUST equals true if packet arrived at logical server
            ctx.enqueueWork(() -> msg.processPacket(ctx, packetOrigin.equals(LogicalSide.CLIENT)));
        });
    }

    /**
     * Sends to server.
     *
     * @param msg message to send
     */
    public void sendToServer(final ModPacket msg) {
        rawChannel.sendToServer(msg);
    }

    /**
     * Sends to player.
     *
     * @param msg    message to send
     * @param player target player
     */
    public void sendToPlayer(final ModPacket msg, final ServerPlayerEntity player) {
        rawChannel.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    /**
     * Sends to origin client.
     *
     * @param msg message to send
     * @param ctx network context
     */
    public void sendToOrigin(final ModPacket msg, final Context ctx) {
        final ServerPlayerEntity player = ctx.getSender();
        if (player != null) // side check
        {
            sendToPlayer(msg, player);
        } else {
            sendToServer(msg);
        }
    }

    /**
     * Sends to everyone in dimension.
     *
     * @param msg message to send
     * @param dim target dimension
     */
    public void sendToDimension(final ModPacket msg, final ResourceLocation dim) {
        rawChannel.send(
                PacketDistributor.DIMENSION.with(() -> RegistryKey.getOrCreateKey(Registry.WORLD_KEY, dim)), msg);
    }

    /**
     * Sends to everyone in circle made using given target point.
     *
     * @param msg message to send
     * @param pos target position and radius
     * @see TargetPoint
     */
    public void sendToPosition(final ModPacket msg, final TargetPoint pos) {
        rawChannel.send(PacketDistributor.NEAR.with(() -> pos), msg);
    }

    /**
     * Sends to everyone.
     *
     * @param msg message to send
     */
    public void sendToEveryone(final ModPacket msg) {
        rawChannel.send(PacketDistributor.ALL.noArg(), msg);
    }

    /**
     * Sends to everyone who is in range from entity's pos using formula below.
     *
     * <pre>
     * Math.min(Entity.getType().getTrackingRange(), ChunkManager.this.viewDistance - 1) * 16;
     * </pre>
     * <p>
     * as of 24-06-2019
     *
     * @param msg    message to send
     * @param entity target entity to look at
     */
    public void sendToTrackingEntity(final ModPacket msg, final Entity entity) {
        rawChannel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
    }

    /**
     * Sends to everyone (including given entity) who is in range from entity's pos using formula below.
     *
     * <pre>
     * Math.min(Entity.getType().getTrackingRange(), ChunkManager.this.viewDistance - 1) * 16;
     * </pre>
     * <p>
     * as of 24-06-2019
     *
     * @param msg    message to send
     * @param entity target entity to look at
     */
    public void sendToTrackingEntityAndSelf(final ModPacket msg, final Entity entity) {
        rawChannel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    /**
     * Sends to everyone in given chunk.
     *
     * @param msg   message to send
     * @param chunk target chunk to look at
     */
    public void sendToTrackingChunk(final ModPacket msg, final Chunk chunk) {
        rawChannel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
    }
}
