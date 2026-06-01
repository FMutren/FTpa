package top.fmutren.ftpa;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static top.fmutren.ftpa.ChatMessage.tpaTimeOut;
import static top.fmutren.ftpa.TeleportEvent.*;

public class TickEvent {

    private static int tick = 0;
    public static  long seconds = 0;
    private static final Map<UUID, UUID> toRemove = new HashMap<>();

    public static void tick(MinecraftServer server){
        int timeOut = 60;
        tick++;
        if(tick >= 20) {
            tick = 0;
            seconds++;
        }
        if(!removeList.containsValue(seconds - timeOut)) return;
        for (Map.Entry<UUID, Long> entry : removeList.entrySet()) {
            if (entry.getValue().equals(seconds - timeOut)) {
                UUID AUUID = entry.getKey();
                UUID BUUID = tpaList.get(AUUID);
                toRemove.put(AUUID, BUUID);
            }
        }
        for(Map.Entry<UUID, UUID> entry : toRemove.entrySet()){
            UUID AUUID = entry.getKey();
            UUID BUUID = entry.getValue();
            ServerPlayerEntity A = server.getPlayerManager().getPlayer(AUUID);
            ServerPlayerEntity B = server.getPlayerManager().getPlayer(BUUID);
            if (A != null && B != null) {
                tpaTimeOut(A, B);
            }
            removeTpa(AUUID, BUUID);
        }
    }
}
