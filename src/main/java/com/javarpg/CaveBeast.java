package main.java.com.javarpg;

import java.util.Random;

public class CaveBeast extends Enemy {
    public CaveBeast() {
        super("洞穴巨兽", 250, 40, 35, 20, 5, 100);
    }

    @Override
    public BattleLog actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 25%概率使用地震攻击
        if (rand.nextDouble() < 0.25) {
            return earthquake(target, engine);
        }
        // 15%概率防御姿态，提升防御力
        if (rand.nextDouble() < 0.15) {
            defend();
            return new BattleLog("洞穴巨兽进入防御姿态，防御力大幅提升！");
        }
        return engine.basicAttack(this, target);
    }

    // 地震：对目标造成大量伤害
    public BattleLog earthquake(Character target, BattleEngine engine) {
        int damage = (int) (this.getAttack() * 2.5);
        target.takeDamage(damage);
        return new BattleLog("洞穴巨兽引发强烈地震！对 " + target.getName() + " 造成 " + damage + " 点伤害。");
    }

    // 防御姿态：提升防御力
    public void defend() {
        System.out.println("洞穴巨兽进入防御姿态，防御力大幅提升！");
        this.setDefense(this.getDefense() + 10);
    }
}