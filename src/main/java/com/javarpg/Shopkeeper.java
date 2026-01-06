package main.java.com.javarpg;

import java.util.Arrays;
import java.util.List;

public class Shopkeeper extends NPC {
    public Shopkeeper() {
        super("商人", Arrays.asList(
            "欢迎光临我的商店，这里有你需要的一切装备！",
            "新到的武器和药水，品质保证！",
            "看上什么就告诉我，价格绝对公道。",
            "冒险者，你需要一些强力的装备吗？"
        ));
    }
}