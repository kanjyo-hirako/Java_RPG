package main.java.com.javarpg;

import java.util.Random;

public class Orc extends Monster {
    public Orc() {
        super("兽人战士", 120, 15, 25, 15, 4, 35);
    }

    @Override
    public void actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 10%概率狂暴，攻击力提升
        if (rand.nextDouble() < 0.1) {
            enrage(target, engine);
            return;
        }
        engine.basicAttack(this, target);
    }

    // 狂暴：临时提升攻击力
    public void enrage(Character target, BattleEngine engine) {
        System.out.println("兽人战士进入狂暴状态，攻击力大幅提升！");
        this.setAttack(this.getAttack() + 10);
        engine.basicAttack(this, target);
    }
}