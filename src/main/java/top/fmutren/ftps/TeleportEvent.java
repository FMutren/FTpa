package top.fmutren.ftps;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static top.fmutren.ftps.ChatMessage.*;
import static top.fmutren.ftps.TickEvent.seconds;

public class TeleportEvent {
    public enum TpaType {
        TPA,
        TPAHERE,
        NULL
    }

    public static Map<UUID, UUID> tpaListR = new HashMap<>();
    public static Map<UUID, UUID> tpaList = new HashMap<>();
    public static Map<UUID, UUID> tpaHereList = new HashMap<>();
    public static Map<UUID, Long> removeList =  new HashMap<>();

    public static int tpa(ServerPlayerEntity A, ServerPlayerEntity B) {
        if(preTpa(A, B) == 0) return 0;

        tpaList.put(B.getUuid(), A.getUuid());
        tpaListR.put(A.getUuid(), B.getUuid());
        removeList.put(A.getUuid(), seconds);
        A.sendMessage(Text.literal("已发送传送申请。").formatted(Formatting.YELLOW));
        B.sendMessage(Text.literal(A.getName() + " 向你发出传送至 你 的申请。\n").formatted(Formatting.YELLOW)
                .append(clickToAccept)
                .append(clickToDeny));
        return 1;
    }

    public static int tpaHere(ServerPlayerEntity A, ServerPlayerEntity B) {
        if(preTpa(A, B) == 0) return 0;

        tpaHereList.put(B.getUuid(), A.getUuid());
        tpaListR.put(A.getUuid(), B.getUuid());
        removeList.put(A.getUuid(), seconds);
        A.sendMessage(Text.literal("已发送传送至此申请。").formatted(Formatting.YELLOW));
        B.sendMessage(Text.literal(A.getName() + " 向你发出传送至 对方 的申请。\n").formatted(Formatting.YELLOW)
                .append(clickToAccept)
                .append(clickToDeny));
        return 1;
    }

    public static int tpaccept(ServerPlayerEntity B, MinecraftServer server) {

        TpaRequest result = getTargetUUID(B);
        UUID targetUUID = result.playerUuid();
        if(targetUUID == null) return tpaFail(B);

        ServerPlayerEntity A = server.getPlayerManager().getPlayer(targetUUID);
        if(A == null) return tpaFail(B);

        if(!isTpaRequest(A)) return noTpaRequest(B);

        switch (result.type()){
            case TPA -> {
                TeleportTarget targetPos = new TeleportTarget(A.getEntityWorld(),
                        A.getEntityPos(),
                        Vec3d.ZERO,
                        A.getYaw(),
                        A.getPitch(),
                        TeleportTarget.NO_OP);
                B.teleportTo(targetPos);
                tpaSucceed(A, B);
                removeList.remove(B.getUuid());
            }
            case TPAHERE -> {
                TeleportTarget targetPos = new TeleportTarget(B.getEntityWorld(),
                        B.getEntityPos(),
                        Vec3d.ZERO,
                        B.getYaw(),
                        B.getPitch(),
                        TeleportTarget.NO_OP);
                A.teleportTo(targetPos);
                tpaSucceed(B, A);
                removeList.remove(A.getUuid());
            }
        }

        return 1;
    }

    public static int tpadeny(ServerPlayerEntity B, MinecraftServer server) {

        if(!isTpaRequest(B)) return noTpaRequest(B);

        TpaRequest result = getTargetUUID(B);
        UUID targetUUID = result.playerUuid();
        if(targetUUID == null) return tpaDenyFail(B);

        ServerPlayerEntity A = server.getPlayerManager().getPlayer(targetUUID);
        if(A == null) return tpaDenyFail(B);

        tpaDeny(A, B);
        removeList.remove(A.getUuid());
        return 1;
    }

    public static int tpacancel(ServerPlayerEntity A, MinecraftServer server) {


        UUID targetUUID = tpaListR.get(A.getUuid());
        if(targetUUID == null) return tpaCancelFail(A);

        ServerPlayerEntity B = server.getPlayerManager().getPlayer(targetUUID);
        if(B == null) return tpaCancelFail(A);
        if(!isTpaRequest(B)) return noTpaRequest(A);

        tpaCancel(A, B);
        removeList.remove(A.getUuid());
        return 1;
    }

    private static int preTpa(ServerPlayerEntity sender, ServerPlayerEntity target) {
        if(sender == target) return tpaSelf(sender);
        if(isTpaRequest(sender)) return hasTpaRequest(sender);
        if(isOtherRequest(target)) return hasOtherRequest(sender);
        return 1;
    }

    private static TpaRequest getTargetUUID(ServerPlayerEntity B) {
        UUID targetUUID = null;
        TpaType tpaType = TpaType.NULL;
        if(tpaHereList.containsKey(B.getUuid())) {
            targetUUID = tpaHereList.get(B.getUuid());
            tpaHereList.remove(B.getUuid(), targetUUID);
            tpaListR.remove(targetUUID, B.getUuid());
            tpaType = TpaType.TPAHERE;
        }
        else if(tpaList.containsKey(B.getUuid())) {
            targetUUID = tpaList.get(B.getUuid());
            tpaList.remove(B.getUuid(), targetUUID);
            tpaListR.remove(targetUUID, B.getUuid());
            tpaType = TpaType.TPA;
        }
        return new TpaRequest(targetUUID, tpaType);
    }

    private static boolean isTpaRequest(ServerPlayerEntity sender) {
        return tpaList.containsKey(sender.getUuid()) ||
                tpaHereList.containsKey(sender.getUuid());
    }

    private static boolean isOtherRequest(ServerPlayerEntity sender) {
        return tpaList.containsValue(sender.getUuid()) ||
                tpaHereList.containsValue(sender.getUuid());
    }

    private record TpaRequest(UUID playerUuid, TpaType type) {}
}
