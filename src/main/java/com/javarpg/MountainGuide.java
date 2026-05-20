package main.java.com.javarpg;

import java.util.Arrays;

public class MountainGuide extends NPC {
    public MountainGuide() {
        super("登山向导", Arrays.asList(
            "你好，冒险者！我是这座山的向导。",
            "山顶上据说有一条沉睡的冰霜巨龙，非常危险。",
            "如果你一定要去，记得带上足够的保暖装备。",
            "传说只有真正的勇者才能击败那条龙，获得它的宝藏。",
            "山路崎岖，要小心脚下的冰裂缝。"
        ));
    }
}