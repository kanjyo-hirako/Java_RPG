package main.java.com.javarpg;

import java.util.Random;

public class Monster extends Enemy {

    // 初始化小怪属性：(Name, MaxHP, MaxMP, Attack, Defense, Level, ExpReward)
    public Monster() {
        super("小怪", 50, 5, 10, 4, 1, 15);
    }
    public Monster(String name, int maxHP, int maxMP, int attack, int defense, int level, int expReward) {
        super(name, maxHP, maxMP, attack, defense, level, expReward);
    }

    @Override
    public BattleLog actInBattle(Character target, BattleEngine engine) {
        // 简单的战斗行为：90%概率普通攻击，10%概率逃跑
        Random rand = new Random();
        if (rand.nextDouble() < 0.1) {
            return flee(target, engine);
        }
        return engine.basicAttack(this, target);
    }

    // 逃跑方法
    public BattleLog flee(Character target, BattleEngine engine) {
        System.out.println("👻 小怪试图逃跑...");
        System.out.println("但它太笨拙了，逃跑失败！");
        // 可以在这里添加逃跑成功的逻辑，比如结束战斗
        // 目前只是打印信息，然后继续战斗
        return new BattleLog("👻 小怪试图逃跑...但它太笨拙了，逃跑失败！");
    }
}
