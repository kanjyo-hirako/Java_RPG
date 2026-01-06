package main.java.com.javarpg;

import java.util.Arrays;
import java.util.List;

public class Villager extends NPC {
    public Villager() {
        super("村民", Arrays.asList(
            "你好，冒险者！村子里最近很不太平。",
            "听说森林里出现了很多危险的怪物，你要小心。",
            "如果你需要补给，可以去村长家看看。",
            "愿光明保佑你的旅途。"
        ));
    }
}