package lielietea.mirai.plugin.core.messagehandler.responder.lovelypicture;

import lielietea.mirai.plugin.core.messagehandler.MessageChainPackage;
import lielietea.mirai.plugin.core.messagehandler.responder.MessageResponder;
import lielietea.mirai.plugin.utils.exception.NoHandlerMethodMatchException;
import lielietea.mirai.plugin.utils.image.ImageURLResolver;
import net.mamoe.mirai.event.events.GroupMessageEvent;;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Pattern;


public class LovelyImage implements MessageResponder<GroupMessageEvent> {

    static class ImageSource{
        static final String DOG_CEO_HUSKY = "https://dog.ceo/api/breed/husky/images/random";
        static final String DOG_CEO_BERNESE = "https://dog.ceo/api/breed/mountain/bernese/images/random";
        static final String DOG_CEO_MALAMUTE = "https://dog.ceo/api/breed/malamute/images/random";
        static final String DOG_CEO_GSD = "https://dog.ceo/api/breed/germanshepherd/images/random";
        static final String DOG_CEO_SAMOYED = "https://dog.ceo/api/breed/samoyed/images/random";
        static final String SHIBE_ONLINE_SHIBA = "https://shibe.online/api/shibes";
        static final String SHIBE_ONLINE_CAT = "https://shibe.online/api/cats";
        static final String RANDOM_DOG = "https://random.dog/woof.json";
    }
    static final List<MessageType> TYPES = new ArrayList<>(Collections.singletonList(MessageType.GROUP));
    static final Pattern DOG_REG_PATTERN = Pattern.compile("((/[Dd]og)|([oO][kK] [Dd]og))|(((来点)|/)((狗子)|狗|(狗狗)))");
    static final Pattern CAT_REG_PATTERN = Pattern.compile("((/[Cc]at)|([oO][kK] [Cc]at))|(((来点)|/)((猫猫)|猫|(猫咪)|(喵喵)))");
    static final Pattern SHIBA_REG_PATTERN = Pattern.compile("((/[Ss]hiba)|([oO][kK] [Ss]hiba))|(((来点)|/)((柴犬)|(柴柴)))");
    static final Pattern HUKSY_REG_PATTERN = Pattern.compile("((/[Hh]usky)|([oO][kK] [Hh]usky))|(((来点)|/)((哈士奇)|(二哈)))");
    static final Pattern BERNESE_REG_PATTERN = Pattern.compile("((/[Bb]ernese)|([oO][kK] 伯恩山))|(((来点)|/)((伯恩山)|(伯恩山犬)))");
    static final Pattern MALAMUTE_REG_PATTERN = Pattern.compile("((/[Mm]alamute)|([oO][kK] 阿拉))|(((来点)|/)(阿拉斯加))");
    static final Pattern GSD_REG_PATTERN = Pattern.compile("((/(([Gg]sd)|(GSD))|([oO][kK] 德牧))|(((来点)|/)((德牧)|(黑背))))");
    static final Pattern SAMOYED_REG_PATTERN = Pattern.compile("((/[Ss]amoyed)|([oO][kK] 萨摩耶))|(((来点)|/)(萨摩耶))");

    static final Map<Pattern, Function<GroupMessageEvent,MessageChainPackage>> PATTERN_SUPPLIER_MAP = new HashMap<>();

    static{
        {
            PATTERN_SUPPLIER_MAP.put(DOG_REG_PATTERN,LovelyImage::getDog);
            PATTERN_SUPPLIER_MAP.put(CAT_REG_PATTERN,LovelyImage::getCat);
            PATTERN_SUPPLIER_MAP.put(SHIBA_REG_PATTERN,LovelyImage::getShiba);
            PATTERN_SUPPLIER_MAP.put(HUKSY_REG_PATTERN,LovelyImage::getHusky);
            PATTERN_SUPPLIER_MAP.put(BERNESE_REG_PATTERN,LovelyImage::getBernese);
            PATTERN_SUPPLIER_MAP.put(MALAMUTE_REG_PATTERN,LovelyImage::getMalamute);
            PATTERN_SUPPLIER_MAP.put(GSD_REG_PATTERN,LovelyImage::getGSD);
            PATTERN_SUPPLIER_MAP.put(SAMOYED_REG_PATTERN,LovelyImage::getSamoyed);

        }
    }

    static LovelyImage INSTANCE = new LovelyImage();

    public static LovelyImage getINSTANCE() {
        return INSTANCE;
    }

    final ExecutorService executor;

    LovelyImage() {
        this.executor = Executors.newCachedThreadPool();
    }


    static MessageChainPackage getDog(GroupMessageEvent event){
        INSTANCE.executor.submit(new AnimalImagePusher(event,ImageSource.RANDOM_DOG,"狗",ImageURLResolver.Source.RADNOM_DOG));
        return MessageChainPackage.getDefaultImpl(event,"正在获取狗狗>>>>>>>",INSTANCE);
    }

    static MessageChainPackage getShiba(GroupMessageEvent event){
        INSTANCE.executor.submit(new AnimalImagePusher(event,ImageSource.SHIBE_ONLINE_SHIBA,"柴犬",ImageURLResolver.Source.SHIBE_ONLINE));
        return MessageChainPackage.getDefaultImpl(event,"正在获取柴犬>>>>>>>",INSTANCE);
    }

    static MessageChainPackage getHusky(GroupMessageEvent event){
        INSTANCE.executor.submit(new AnimalImagePusher(event,ImageSource.DOG_CEO_HUSKY,"哈士奇",ImageURLResolver.Source.DOG_CEO));
        return MessageChainPackage.getDefaultImpl(event,"正在获取哈士奇>>>>>>>",INSTANCE);
    }

    static MessageChainPackage getBernese(GroupMessageEvent event){
        INSTANCE.executor.submit(new AnimalImagePusher(event,ImageSource.DOG_CEO_BERNESE,"伯恩山",ImageURLResolver.Source.DOG_CEO));
        return MessageChainPackage.getDefaultImpl(event,"正在获取伯恩山>>>>>>>",INSTANCE);
    }

    static MessageChainPackage getMalamute(GroupMessageEvent event){
        INSTANCE.executor.submit(new AnimalImagePusher(event,ImageSource.DOG_CEO_MALAMUTE,"阿拉斯加",ImageURLResolver.Source.DOG_CEO));
        return MessageChainPackage.getDefaultImpl(event,"正在获取阿拉斯加>>>>>>>",INSTANCE);
    }

    static MessageChainPackage getGSD(GroupMessageEvent event){
        INSTANCE.executor.submit(new AnimalImagePusher(event,ImageSource.DOG_CEO_GSD,"德牧",ImageURLResolver.Source.DOG_CEO));
        return MessageChainPackage.getDefaultImpl(event,"正在获取德牧>>>>>>>",INSTANCE);
    }

    static MessageChainPackage getSamoyed(GroupMessageEvent event){
        INSTANCE.executor.submit(new AnimalImagePusher(event,ImageSource.DOG_CEO_SAMOYED,"萨摩耶",ImageURLResolver.Source.DOG_CEO));
        return MessageChainPackage.getDefaultImpl(event,"正在获取萨摩耶>>>>>>>",INSTANCE);
    }

    static MessageChainPackage getCat(GroupMessageEvent event){
        INSTANCE.executor.submit(new AnimalImagePusher(event,ImageSource.SHIBE_ONLINE_CAT,"猫",ImageURLResolver.Source.SHIBE_ONLINE));
        return MessageChainPackage.getDefaultImpl(event,"正在获取猫咪>>>>>>>",INSTANCE);
    }

    @Override
    public String getName() {
        return "OK Animal";
    }


    @Override
    public boolean match(GroupMessageEvent event) {
        for(Pattern pattern : PATTERN_SUPPLIER_MAP.keySet()){
            if(pattern.matcher(event.getMessage().contentToString()).matches())
                return true;
        }
        return false;
    }

    @Override
    public MessageChainPackage handle(GroupMessageEvent event) throws NoHandlerMethodMatchException {
        for(Map.Entry<Pattern, Function<GroupMessageEvent,MessageChainPackage>> entry: PATTERN_SUPPLIER_MAP.entrySet()){
            if(entry.getKey().matcher(event.getMessage().contentToString()).matches()){
                return entry.getValue().apply(event);
            }
        }
        throw new NoHandlerMethodMatchException();
    }

    @NotNull
    @Override
    public List<MessageType> types() {
        return TYPES;
    }


    @Override
    public void onclose() {
        executor.shutdown();
    }


}
