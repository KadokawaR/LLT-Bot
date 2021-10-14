package lielietea.mirai.plugin.core.messagehandler.responder.autoreply.fgi;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lielietea.mirai.plugin.utils.image.ImageSender;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class FurryGamesIndex {
    public static final String FGIUrl = "https://furrygames.top/";
    public static final String FGIListUrl = "https://furrygames.top/zh-cn/list.html";

    public FurryGamesIndex() throws MalformedURLException {
    }

    //从furrygames.top中获得所有游戏的列表，存储成中英混合名-链接的形式
    public static Map<String,String> getFGIlist(){
        Map<String,String> FGIlist = new HashMap<>();
        Document document = null;
        try {
            document = Jsoup.parse(new URL(FGIListUrl).openStream(), "utf-8", FGIListUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert document != null;
        Elements links = document.select("a[href]");

        String linkStr = "";
        for (Element link : links) {
            if (link.attr("href").contains("/game") && (!link.attr("href").contains("http"))) {
                linkStr = link.attr("href").replace("../","");
                linkStr = FGIUrl + linkStr;
                FGIlist.put(link.text(),linkStr);
            }
        }
        return FGIlist;
    }

    //通过游戏页面链接获得游戏介绍
    public static String getGameDescription(String gameURL){
        Document document = null;
        try {
            document = Jsoup.parse(new URL(gameURL).openStream(), "utf-8", gameURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert document != null;
        Elements links = document.getElementsByClass("description");
        for (Element link : links) {
            return link.text();
        }
        return null;
    }

    //通过游戏页面链接获得游戏图片
    public static String getGameImageURL(String gameURL){
        Document document = null;
        try {
            document = Jsoup.parse(new URL(gameURL).openStream(), "utf-8", gameURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert document != null;
        Elements links = document.select("img[src]");
        for (Element link : links) {
            String gameImageLink = link.baseUri() + link.attr("src");
            gameImageLink = gameImageLink.replace(gameURL,"");
            if (!gameImageLink.contains("https")){
                gameImageLink = gameImageLink.replace("../../","");
                gameImageLink = FGIUrl + gameImageLink;
                return gameImageLink;
            }
        }
        return null;
    }

    //通过用户给的名字获取游戏信息
    public static String[] getGameInfo(String givenGameName){
        Map<String,String> FGIList= getFGIlist();
        String[] result = new String[4];
        String name = searchGameName(givenGameName, FGIList);
        if (name == null){
            return null;
        } else {
            String gameURL = FGIList.get(name);
            if (gameURL == null){
                return null;
            } else {
                String gameDescription = getGameDescription(gameURL);
                String gameImageURL = getGameImageURL(gameURL);
                result[0] = name;
                result[1] = gameURL;
                result[2] = gameDescription;
                result[3] = gameImageURL;
            }
        }
        return result;
    }

    //通过用户给的名字查看List中是否含有相应的游戏
    public static String searchGameName(String givenGameName, Map<String,String> FGIList){
        for (String key : FGIList.keySet()) {
            if (key.contains(givenGameName)) {
                return key;
            }
        }
        return null;
    }

    public static void search(GroupMessageEvent event) throws MalformedURLException {
        if (event.getMessage().contentToString().contains("/fgi ")||event.getMessage().contentToString().contains("/FGI ")){
            String givenGameName = event.getMessage().contentToString().replace("/fgi ","").replace("/FGI ","");
            StringBuilder sb = new StringBuilder();

            String[] gameInfo = getGameInfo(givenGameName);
            if (gameInfo==null){
                event.getSubject().sendMessage("没有找到对应的游戏信息。");
            } else {
                if (gameInfo[2] == null){
                    event.getSubject().sendMessage("该游戏没有相关描述。");
                } else {
                    sb.append(gameInfo[0])
                            .append("\n\n")
                            .append(gameInfo[2])
                            .append("\n\n")
                            .append(gameInfo[1]);
                    event.getSubject().sendMessage(sb.toString());
                }
                if (gameInfo[3]!=null){
                    URL url = new URL(gameInfo[3]);
                    ImageSender.sendImageFromURL(event.getSubject(),url);
                }
            }
        }


    }
}
