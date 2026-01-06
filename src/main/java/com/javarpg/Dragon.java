package main.java.com.javarpg;

import java.util.Random;

public class Dragon extends Monster {
    public Dragon() {
        super("火焰巨龙", 200, 50, 35, 20, 6, 100);
    }

    @Override
    public void actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 20%概率使用火焰吐息
        if (rand.nextDouble() < 0.2) {
            fireBreath(target, engine);
            return;
        }
        // 10%概率恢复生命值
        if (rand.nextDouble() < 0.1) {
            heal();
            return;
        }
        engine.basicAttack(this, target);
    }

    // 火焰吐息：造成大量伤害
    public void fireBreath(Character target, BattleEngine engine) {
        System.out.println("火焰巨龙喷出炽热的龙息！");
        int damage = this.getAttack() * 2;
        target.takeDamage(damage);
    }

    // 治疗：恢复一定生命值
    public void heal() {
        System.out.println("火焰巨龙恢复了一些生命值！");
        int healAmount = 30;
        this.setHP(Math.min(this.getHP() + healAmount, this.getMaxHP()));
    }
}