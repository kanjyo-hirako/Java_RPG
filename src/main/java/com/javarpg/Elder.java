package main.java.com.javarpg;

import java.util.Arrays;
import java.util.List;

public class Elder extends NPC {
    public Elder() {
        super("长老", Arrays.asList(
            "孩子，我能感受到你身上有着特殊的命运。",
            "传说中，只有真正的勇者才能击败黑暗深处的魔王。",
            "记住，勇气和智慧同样重要。",
            "当你准备好的时候，去挑战洞穴中的远古守护者吧。"
        ));
    }
}