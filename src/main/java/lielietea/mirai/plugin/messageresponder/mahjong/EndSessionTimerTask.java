package lielietea.mirai.plugin.messageresponder.mahjong;

import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.TimerTask;

class EndSessionTimerTask extends TimerTask {
    int id;
    GroupMessageEvent event;

    public EndSessionTimerTask(int id,GroupMessageEvent event) {
        this.id = id;
        this.event = event;
    }

    @Override
    public void run() {
        if (MahjongRiddle.riddleSessionHolder.get(event.getGroup().getId()).id == this.id //判断sessionID相等
                &&!MahjongRiddle.isAllTrue(MahjongRiddle.riddleSessionHolder.get(event.getGroup().getId()).isGuessed /*而且牌没有被全部猜出*/)) {
            try {
                event.getSubject().sendMessage("公布答案:");
                BufferedImage imgAnswer = MahjongRiddle.getTileImage(MahjongRiddle.resolveRandomTiles(MahjongRiddle.riddleSessionHolder.get(event.getGroup().getId()).answerNum));
                MahjongRiddle.sendTileImage(imgAnswer, event);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //清空该Session
            MahjongRiddle.riddleSessionHolder.remove(event.getGroup().getId());
        }

    }
}
