package lielietea.mirai.plugin.dice;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



class CommonDice {
    int bound;
    int repeat;
    List<Integer> result;

    CommonDice(int bound, int repeat) {
        this.bound = bound;
        this.repeat = repeat;
        result = new ArrayList<>();
    }

    public static CommonDice getInstance(int bound, int repeat){
        CommonDice dice = new CommonDice(bound,repeat);
        dice.reroll();
        return dice;
    }

    /**
     * 扔骰子操作，用这个方法来重新扔一次骰子
     */
    public void reroll(){
        if(!result.isEmpty()) result = new ArrayList<>();
        for(int i=0;i<repeat;i++){
            result.add(new Random(System.nanoTime()).nextInt(bound)+1);
        }
    }

    /**
     * 返回一个结果组成的List
     *
     * @return 一个包含了投掷结果的ArrayList
     */
    public List<Integer> toList(){
        return result;
    }

    /**
     * 私聊某人投掷结果
     *
     * @param directMessageTarget 私聊对象
     */
    public void privatelyInfoResult(Friend directMessageTarget){
        directMessageTarget.sendMessage(buildMessage());
    }

    /**
     * 在某群中广播投掷结果并引用投掷命令
     *
     * @param quoteMessage 被引用的消息
     * @param broadcastGroup 广播对象
     */
    public void broadcastResultByQuote(MessageChain quoteMessage,Group broadcastGroup){
        broadcastGroup.sendMessage(buildMessage(quoteMessage));
    }

    /**
     * 在某群中广播投掷结果
     *
     * @param broadcastGroup 广播对象
     */
    public void broadcastResult(Group broadcastGroup){
        broadcastGroup.sendMessage(buildMessage());
    }


    MessageChain buildMessage(){
        MessageChainBuilder message = new MessageChainBuilder();
        message.append("您掷出的点数是:");
        result.stream().forEach(result->{
            message.append(+result+" ");
        });
        return message.build();
    }

    MessageChain buildMessage(MessageChain quoteMessage){
        MessageChainBuilder message = new MessageChainBuilder();
        message.append("您掷出的点数是:");
        message.append(new QuoteReply(quoteMessage));
        result.stream().forEach(result->{
            message.append(result+" ");
        });
        return message.build();
    }


    @Override
    public String toString() {
        return "CommonDice{" +
                "bound=" + bound +
                ", repeat=" + repeat +
                ", result=" + result +
                '}';
    }
}
