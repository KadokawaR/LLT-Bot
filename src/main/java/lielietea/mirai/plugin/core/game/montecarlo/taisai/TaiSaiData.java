package lielietea.mirai.plugin.core.game.montecarlo.taisai;

public class TaiSaiData {

    int specificNumber;
    TaiSaiBetType type;

    TaiSaiData(int specificNumber, TaiSaiBetType type){
        this.type = type;
        this.specificNumber=specificNumber;
    }

}
