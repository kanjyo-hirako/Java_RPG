package main.java.com.javarpg;

import java.util.Random;

public class Skeleton extends Monster {
    public Skeleton() {
        super("骷髅战士", 90, 5, 20, 12, 3, 25);
    }

    @Override
    public void actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        // 15%概率格挡，减少受到的伤害
        if (rand.nextDouble() < 0.15) {
            defend(engine);
            return;
        }
        engine.basicAttack(this, target);
    }

    // 格挡：临时提升防御力
    public void defend(BattleEngine engine) {
        System.out.println("骷髅战士举起盾牌格挡！");
        this.setDefense(this.getDefense() + 5);
        // 在这里可以添加下一回合恢复防御的逻辑，但简化处理
    }
}