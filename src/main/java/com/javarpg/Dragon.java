package main.java.com.javarpg;

import java.util.Random;

public class Dragon extends Monster {
    public Dragon() {
        super("火焰巨龙", 200, 50, 35, 20, 6, 100);
    }

    @Override
    public BattleLog actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 20%概率使用火焰吐息
        if (rand.nextDouble() < 0.2) {
            return fireBreath(target, engine);
        }
        // 10%概率恢复生命值
        if (rand.nextDouble() < 0.1) {
            heal();
            return new BattleLog("火焰巨龙恢复了一些生命值！恢复了 30 点生命值。");
        }
        return engine.basicAttack(this, target);
    }

    // 火焰吐息：造成大量伤害
    public BattleLog fireBreath(Character target, BattleEngine engine) {
        int damage = this.getAttack() * 2;
        target.takeDamage(damage);
        return new BattleLog("火焰巨龙喷出炽热的龙息！对 " + target.getName() + " 造成 " + damage + " 点伤害。");
    }

    // 治疗：恢复一定生命值
    public void heal() {
        System.out.println("火焰巨龙恢复了一些生命值！");
        int healAmount = 30;
        this.setHP(Math.min(this.getHP() + healAmount, this.getMaxHP()));
    }
}