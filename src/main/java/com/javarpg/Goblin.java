package main.java.com.javarpg;

import java.util.Random;

public class Goblin extends Monster {
    public Goblin() {
        super("哥布林", 70, 10, 15, 8, 2, 15);
    }

    @Override
    public void actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 20%概率逃跑
        if (rand.nextDouble() < 0.2) {
            flee(target, engine);
            return;
        }
        // 10%概率使用特殊攻击
        if (rand.nextDouble() < 0.1) {
            specialAttack(target, engine);
            return;
        }
        engine.basicAttack(this, target);
    }

    // 特殊攻击：快速连击
    public void specialAttack(Character target, BattleEngine engine) {
        System.out.println("哥布林发动快速连击！");
        engine.basicAttack(this, target);
        if (target.isAlive()) {
            engine.basicAttack(this, target);
        }
    }
}