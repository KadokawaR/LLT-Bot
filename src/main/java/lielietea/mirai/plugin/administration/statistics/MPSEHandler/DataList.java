package lielietea.mirai.plugin.administration.statistics.MPSEHandler;

import java.util.ArrayList;
import java.util.List;

public class DataList {
    List<Data> datas;

    DataList(Data data){
        this.datas = new ArrayList<>();
        this.datas.add(data);
    }

    DataList(){
        this.datas = new ArrayList<>();
    }
}
