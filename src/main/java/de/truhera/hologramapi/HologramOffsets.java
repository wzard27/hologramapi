package de.truhera.hologramapi;

import org.bukkit.entity.Player;

public class HologramOffsets {

//    private static final double OFFSET_18 = 0;
//    private static final double OFFSET_19 = 0;//-1;
//    private static final double OFFSET_19_1 = 0;//-1;
//    private static final double OFFSET_19_2 = 0;//-1;
//    private static final double OFFSET_19_34 = 0;//-1;
//    private static final double OFFSET_10 = 0;//-1;
//    private static final double OFFSET_11 = 0;
//    private static final double OFFSET_11_1 = 0;
//    private static final double OFFSET_12 = 0;
//    private static final double OFFSET_12_1 = 0;
//    private static final double OFFSET_12_2 = 0;

    public static double getOffset(int protocolId) {
//        if(protocolId == 47)
//            return OFFSET_18;
//        if(protocolId == 107)
//            return OFFSET_19;
//        if(protocolId == 108)
//            return OFFSET_19_1;
//        if(protocolId == 109)
//            return OFFSET_19_2;
//        if(protocolId == 110)
//            return OFFSET_19_34;
//        if(protocolId == 210)
//            return OFFSET_10;
//        if(protocolId == 315)
//            return OFFSET_11;
//        if(protocolId == 316)
//            return OFFSET_11_1;
//        if(protocolId == 335)
//            return OFFSET_12;
//        if(protocolId == 338)
//            return OFFSET_12_1;
//        return OFFSET_12_2;
        return 1;
    }

    public static double getOffset(Player player) {
            return 1;
    }


}
