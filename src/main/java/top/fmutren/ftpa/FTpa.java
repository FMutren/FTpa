package top.fmutren.ftpa;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static top.fmutren.ftpa.TeleportEvent.*;

public class FTpa implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher,
                                                    registryAccess,
                                                    environment)
                -> dispatcher.register(CommandManager.literal("tpa")
                .then(CommandManager.argument("Player", EntityArgumentType.player())
                .executes(context -> {
                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "Player");
                    ServerPlayerEntity sender = context.getSource().getPlayerOrThrow();
                    return tpa(sender, target);
                }))
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher,
                                                    registryAccess,
                                                    environment)
                -> dispatcher.register(CommandManager.literal("tpahere")
                .then(CommandManager.argument("Player", EntityArgumentType.player())
                .executes(context -> {
                    ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "Player");
                    ServerPlayerEntity sender = context.getSource().getPlayerOrThrow();
                    return tpaHere(sender, target);
                }))
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher,
                                                    registryAccess,
                                                    environment)
                -> dispatcher.register(CommandManager.literal("tpaccept")
                .executes(context -> {
                    MinecraftServer server = context.getSource().getServer();
                    ServerPlayerEntity sender = context.getSource().getPlayer();
                    if(sender == null) {
                        context.getSource().sendError(Text.literal("该命令必须由玩家执行"));
                        return 0;
                    }
                    return tpaccept(sender, server);
                })
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher,
                                                    registryAccess,
                                                    environment)
                -> dispatcher.register(CommandManager.literal("tpadeny")
                .executes(context -> {
                    MinecraftServer server = context.getSource().getServer();
                    ServerPlayerEntity sender = context.getSource().getPlayer();
                    if(sender == null) {
                        context.getSource().sendError(Text.literal("该命令必须由玩家执行"));
                        return 0;
                    }
                    return tpadeny(sender, server);
                })
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher,
                                                    registryAccess,
                                                    environment)
                -> dispatcher.register(CommandManager.literal("tpacancel")
                .executes(context -> {
                    MinecraftServer server = context.getSource().getServer();
                    ServerPlayerEntity sender = context.getSource().getPlayer();
                    if(sender == null) {
                        context.getSource().sendError(Text.literal("该命令必须由玩家执行"));
                        return 0;
                    }
                    return tpacancel(sender, server);
                })
        ));

        ServerTickEvents.END_SERVER_TICK.register(TickEvent::tick);

    }
}
