package main.java.com.javarpg;

import java.util.Random;

public class ForestGuardian extends Enemy {
    public ForestGuardian() {
        super("森林守护者", 180, 30, 30, 15, 4, 70);
    }

    @Override
    public BattleLog actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 20%概率使用自然之力
        if (rand.nextDouble() < 0.2) {
            return naturePower(target, engine);
        }
        // 10%概率恢复生命值
        if (rand.nextDouble() < 0.1) {
            heal();
            return new BattleLog("森林守护者汲取自然能量恢复生命值！恢复了 25 点生命值。");
        }
        return engine.basicAttack(this, target);
    }

    // 自然之力：造成伤害并有一定几率使对手中毒
    public BattleLog naturePower(Character target, BattleEngine engine) {
        int damage = this.getAttack() * 2;
        target.takeDamage(damage);
        return new BattleLog("森林守护者召唤自然之力！对 " + target.getName() + " 造成 " + damage + " 点伤害。");
    }

    // 治疗：恢复一定生命值
    public void heal() {
        System.out.println("森林守护者汲取自然能量恢复生命值！");
        int healAmount = 25;
        this.setHP(Math.min(this.getHP() + healAmount, this.getMaxHP()));
    }
}