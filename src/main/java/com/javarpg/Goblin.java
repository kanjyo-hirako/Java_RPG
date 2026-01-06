package main.java.com.javarpg;

import java.util.Random;

public class Goblin extends Monster {
    public Goblin() {
        super("哥布林", 70, 10, 15, 8, 2, 15);
    }

    @Override
    public BattleLog actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 20%概率逃跑
        if (rand.nextDouble() < 0.2) {
            return flee(target, engine);
        }
        // 10%概率使用特殊攻击
        if (rand.nextDouble() < 0.1) {
            return specialAttack(target, engine);
        }
        return engine.basicAttack(this, target);
    }

    // 特殊攻击：快速连击
    public BattleLog specialAttack(Character target, BattleEngine engine) {
        engine.basicAttack(this, target);
        if (target.isAlive()) {
            engine.basicAttack(this, target);
        }
        return new BattleLog("哥布林发动快速连击！对 " + target.getName() + " 进行了两次攻击。");
    }
}