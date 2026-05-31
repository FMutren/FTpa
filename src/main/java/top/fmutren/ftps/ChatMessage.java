package top.fmutren.ftps;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatMessage {
    private ChatMessage() {}

    public static  int tpaSelf(ServerPlayerEntity sender) {
        sender.sendMessage(Text.literal("你不能传送自己").formatted(Formatting.RED));
        return 0;
    }

    public static void tpaSucceed(ServerPlayerEntity sender, ServerPlayerEntity target) {
        sender.sendMessage(Text.literal("传送成功！").formatted(Formatting.GREEN));
        target.sendMessage(Text.literal("传送成功！").formatted(Formatting.GREEN));
    }

    public static void tpaCancel(ServerPlayerEntity sender, ServerPlayerEntity target) {
        sender.sendMessage(Text.literal("已取消申请！").formatted(Formatting.YELLOW));
        target.sendMessage(Text.literal("对方已取消申请！").formatted(Formatting.YELLOW));
    }

    public static void tpaDeny(ServerPlayerEntity sender, ServerPlayerEntity target) {
        sender.sendMessage(Text.literal("对方拒绝申请！").formatted(Formatting.RED));
        target.sendMessage(Text.literal("已拒绝申请！").formatted(Formatting.RED));
    }

    public static void tpaTimeOut(ServerPlayerEntity sender, ServerPlayerEntity target) {
        sender.sendMessage(Text.literal("请求超时！").formatted(Formatting.RED));
        target.sendMessage(Text.literal("请求超时！").formatted(Formatting.RED));
    }

    public static int tpaFail(ServerPlayerEntity sender) {
        sender.sendMessage(Text.literal("传送失败!").formatted(Formatting.RED));
        return 1;
    }

    public static int tpaCancelFail(ServerPlayerEntity sender) {
        sender.sendMessage(Text.literal("取消失败!").formatted(Formatting.RED));
        return 1;
    }

    public static int tpaDenyFail(ServerPlayerEntity sender) {
        sender.sendMessage(Text.literal("拒绝失败!").formatted(Formatting.RED));
        return 1;
    }

    public static int noTpaRequest(ServerPlayerEntity sender) {
        sender.sendMessage(Text.literal("你没有传送申请。").formatted(Formatting.RED));
        return 0;
    }

    public static int hasTpaRequest(ServerPlayerEntity sender) {
        sender.sendMessage(Text.literal("你已有传送申请。").formatted(Formatting.RED));
        return 0;
    }

    public static int hasOtherRequest(ServerPlayerEntity sender) {
        sender.sendMessage(Text.literal("对方有未处理的传送请求。").formatted(Formatting.RED));
        return 0;
    }

    public static final Text clickToAccept = Text.literal("[同意申请]")
            .styled(style -> style
                    .withColor(Formatting.GREEN)
                    .withClickEvent(new ClickEvent.RunCommand("/tpaccept"))
            );

    public static final Text clickToDeny = Text.literal("[拒绝申请]")
            .styled(style -> style
                    .withColor(Formatting.RED)
                    .withClickEvent(new ClickEvent.RunCommand("/tpadeny"))
            );
}
