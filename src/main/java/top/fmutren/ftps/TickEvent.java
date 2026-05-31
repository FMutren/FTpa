package top.fmutren.ftps;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static top.fmutren.ftps.ChatMessage.tpaTimeOut;
import static top.fmutren.ftps.TeleportEvent.*;

public class TickEvent {

    private static int tick = 0;
    public static  long seconds = 0;

    public static void tick(MinecraftServer server){
        int timeOut = 60;
        tick++;
        if(tick >= 20) {
            tick = 0;
            seconds++;
        }
        if(!removeList.containsValue(seconds - timeOut)) return;
        Iterator<Map.Entry<UUID, Long>> iterator = removeList.entrySet().iterator();
        for (Map.Entry<UUID, Long> entry : removeList.entrySet()) {
            if (entry.getValue().equals(seconds - timeOut)) {
                UUID uuid = entry.getKey();
                if(uuid != null){
                    ServerPlayerEntity A = server.getPlayerManager().getPlayer(uuid);
                    UUID targetUUID = tpaListR.get(uuid);
                    ServerPlayerEntity B = server.getPlayerManager().getPlayer(targetUUID);
                    if(B != null && A != null){
                        tpaTimeOut(A, B);
                        iterator.remove();
                        tpaListR.remove(uuid, targetUUID);
                        if(tpaList.containsKey(targetUUID)) tpaList.remove(targetUUID, uuid);
                        if(tpaHereList.containsKey(targetUUID)) tpaHereList.remove(targetUUID, uuid);
                    }
                }
            }
        }
    }
}
