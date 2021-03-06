package lielietea.mirai.plugin.core.game.garden.propertyenum;

public enum PlantSeed {
    None,

    OrangeTree,
    AppleTree,
    StrawberryPlant,
    CoconutTree,
    RaspberryPlant,

    BlueberryPlant,
    RosePlant,
    DesertRosePlant,
    LycheePlant,
    CoffeeBeanTree;

    public final int[] seedTime = new int[]{
            0,
            60, 60, 60, 240, 60,
            60, 120, 120, 120, 120
    };

    public final GardenType[] plantType = new GardenType[]{
            GardenType.None,
            GardenType.None, GardenType.None, GardenType.Grassland, GardenType.Jungle, GardenType.Grassland,
            GardenType.Grassland, GardenType.Wetland, GardenType.Desert, GardenType.Tropical, GardenType.Jungle

    };

}
