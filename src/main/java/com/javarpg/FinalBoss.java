package main.java.com.javarpg;

import java.util.Random;

public class FinalBoss extends Enemy {
    private boolean isInSecondPhase = false;

    public FinalBoss() {
        super("暗影魔王", 300, 100, 40, 25, 8, 200);
    }

    @Override
    public void actInBattle(Character target, BattleEngine engine) {
        // 检查是否进入第二阶段
        if (!isInSecondPhase && this.getHP() < this.getMaxHP() * 0.5) {
            secondPhase();
        }

        Random rand = new Random();
        // 第一阶段技能
        if (!isInSecondPhase) {
            // 20%概率使用暗影箭
            if (rand.nextDouble() < 0.2) {
                shadowBolt(target, engine);
                return;
            }
            // 10%概率恢复生命值
            if (rand.nextDouble() < 0.1) {
                heal();
                return;
            }
        }
        // 第二阶段技能
        else {
            // 25%概率使用毁灭冲击
            if (rand.nextDouble() < 0.25) {
                destructionBlast(target, engine);
                return;
            }
            // 15%概率使用暗影诅咒
            if (rand.nextDouble() < 0.15) {
                shadowCurse(target, engine);
                return;
            }
        }
        engine.basicAttack(this, target);
    }

    // 进入第二阶段
    public void secondPhase() {
        System.out.println("暗影魔王的力量增强了！进入狂暴状态！");
        this.isInSecondPhase = true;
        this.setAttack(this.getAttack() + 15);
        this.setDefense(this.getDefense() + 10);
    }

    // 暗影箭：造成较高伤害
    public void shadowBolt(Character target, BattleEngine engine) {
        System.out.println("暗影魔王射出一支暗影箭！");
        int damage = (int) (this.getAttack() * 1.8);
        target.takeDamage(damage);
    }

    // 毁灭冲击：造成巨大伤害
    public void destructionBlast(Character target, BattleEngine engine) {
        System.out.println("暗影魔王释放毁灭性的冲击波！");
        int damage = (int) (this.getAttack() * 2.5);
        target.takeDamage(damage);
    }

    // 暗影诅咒：降低目标属性
    public void shadowCurse(Character target, BattleEngine engine) {
        System.out.println("暗影魔王施放了暗影诅咒！");
        // 这里可以添加降低目标属性的效果
    }

    // 治疗：恢复一定生命值
    public void heal() {
        System.out.println("暗影魔王恢复了部分生命值！");
        int healAmount = 40;
        this.setHP(Math.min(this.getHP() + healAmount, this.getMaxHP()));
    }
}