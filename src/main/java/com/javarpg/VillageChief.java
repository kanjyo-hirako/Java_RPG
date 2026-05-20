package main.java.com.javarpg;

import java.util.Random;

public class VillageChief extends Enemy {
    public VillageChief() {
        super("村庄首领", 150, 20, 25, 10, 3, 50);
    }

    @Override
    public BattleLog actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 15%概率使用重击
        if (rand.nextDouble() < 0.15) {
            return heavyStrike(target, engine);
        }
        return engine.basicAttack(this, target);
    }

    // 重击：造成额外伤害
    public BattleLog heavyStrike(Character target, BattleEngine engine) {
        int damage = (int) (this.getAttack() * 1.5);
        target.takeDamage(damage);
        return new BattleLog("村庄首领发动重击！对 " + target.getName() + " 造成 " + damage + " 点伤害。");
    }
}