package lielietea.mirai.plugin.core.messagehandler.game.stock;

public class MarketOperation {

    //正态分布算法
    static double NormalDistribution(double u,double v){
        java.util.Random random = new java.util.Random();
        return Math.sqrt(v)*random.nextGaussian()+u;
    }

    //读取记录
    static void readRecord(){

    }

    //存储记录
    static void saveRecord(){

    }


}
