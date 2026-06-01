package top.fmutren.ftpa;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static top.fmutren.ftpa.ChatMessage.*;
import static top.fmutren.ftpa.TickEvent.seconds;

public class TeleportEvent {
    public enum TpaType {
        TPA,
        TPAHERE,
        NULL,
        TARGET
    }

    public static Map<UUID, UUID> tpaList = new HashMap<>();
    public static Map<UUID, TpaType> senderTpaType = new HashMap<>();
    public static Map<UUID, Long> removeList = new HashMap<>();

    public static int tpa(ServerPlayerEntity A, ServerPlayerEntity B) {
        if(preTpa(A, B) == 0) return 0;

        tpaList.put(B.getUuid(), A.getUuid());
        tpaList.put(A.getUuid(), B.getUuid());
        senderTpaType.put(A.getUuid(), TpaType.TPA);
        removeList.put(A.getUuid(), seconds);
        A.sendMessage(Text.literal("已发送传送申请。").formatted(Formatting.YELLOW));
        B.sendMessage(Text.literal(A.getName() + " 向你发出传送至 你 的申请。\n").formatted(Formatting.YELLOW)
                .append(clickToAccept)
                .append(clickToDeny));
        return 1;
    }

    public static int tpaHere(ServerPlayerEntity A, ServerPlayerEntity B) {
        if(preTpa(A, B) == 0) return 0;

        tpaList.put(B.getUuid(), A.getUuid());
        tpaList.put(A.getUuid(), B.getUuid());
        senderTpaType.put(A.getUuid(), TpaType.TPAHERE);
        removeList.put(A.getUuid(), seconds);
        A.sendMessage(Text.literal("已发送传送至此申请。").formatted(Formatting.YELLOW));
        B.sendMessage(Text.literal(A.getName() + " 向你发出传送至 对方 的申请。\n").formatted(Formatting.YELLOW)
                .append(clickToAccept)
                .append(clickToDeny));
        return 1;
    }

    public static int tpaccept(ServerPlayerEntity B, MinecraftServer server) {

        if(tpaList.containsKey(B.getUuid())) noTpaRequest(B);

        UUID AUUID = getTargetUUID(B);
        if(AUUID == null) return tpaFail(B);

        ServerPlayerEntity A = server.getPlayerManager().getPlayer(AUUID);
        if(A == null) return tpaFail(B);

        if(!isTpaRequest(AUUID)) return noTpaRequest(B);

        switch (senderTpaType.get(AUUID)) {
            case TPA -> {
                TeleportTarget targetPos = new TeleportTarget(B.getEntityWorld(),
                        B.getEntityPos(),
                        Vec3d.ZERO,
                        B.getYaw(),
                        B.getPitch(),
                        TeleportTarget.NO_OP);
                A.teleportTo(targetPos);
                tpaSucceed(A, B);
                removeTpa(AUUID, B.getUuid());
            }
            case TPAHERE -> {
                TeleportTarget targetPos = new TeleportTarget(A.getEntityWorld(),
                        A.getEntityPos(),
                        Vec3d.ZERO,
                        A.getYaw(),
                        A.getPitch(),
                        TeleportTarget.NO_OP);
                B.teleportTo(targetPos);
                tpaSucceed(B, A);
                removeTpa(AUUID, B.getUuid());
            }
        }

        return 1;
    }

    public static int tpadeny(ServerPlayerEntity B, MinecraftServer server) {

        if(!isTpaRequest(B.getUuid())) return noTpaRequest(B);

        UUID AUUID = getTargetUUID(B);
        if(AUUID == null) return tpaDenyFail(B);

        ServerPlayerEntity A = server.getPlayerManager().getPlayer(AUUID);
        if(A == null) return tpaDenyFail(B);

        removeTpa(AUUID, B.getUuid());
        tpaDeny(A, B);
        return 1;
    }

    public static int tpacancel(ServerPlayerEntity A, MinecraftServer server) {

        if(!isTpaRequest(A.getUuid())) return noTpaRequest(A);

        UUID BUUID = getTargetUUID(A);
        if(BUUID == null) return tpaCancelFail(A);

        ServerPlayerEntity B = server.getPlayerManager().getPlayer(BUUID);
        if(B == null) return tpaCancelFail(A);

        if(!isTpaRequest(BUUID)) return noTpaRequest(A);

        if(removeTpa(A.getUuid(), BUUID)) return tpaCancelFail(B);
        tpaCancel(A, B);
        return 1;
    }

    public static boolean removeTpa(UUID AUUID, UUID BUUID) {
        try{
            tpaList.remove(AUUID, BUUID);
            tpaList.remove(BUUID, AUUID);
            removeList.remove(AUUID, removeList.get(AUUID));
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    private static int preTpa(ServerPlayerEntity A, ServerPlayerEntity B) {
        if(A == B) return tpaSelf(A);
        if(isTpaRequest(A.getUuid())) return hasTpaRequest(A);
        if(isTpaRequest(B.getUuid())) return hasOtherRequest(A);
        return 1;
    }

    private static UUID getTargetUUID(ServerPlayerEntity sender) {
        return tpaList.get(sender.getUuid());
    }

    private static boolean isTpaRequest(UUID AUUID) {
        return tpaList.containsKey(AUUID);
    }
}
