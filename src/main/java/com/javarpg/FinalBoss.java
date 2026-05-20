package main.java.com.javarpg;

import java.util.Random;

public class FinalBoss extends Enemy {
    private boolean isInSecondPhase = false;

    public FinalBoss() {
        super("暗影魔王", 300, 100, 40, 25, 8, 200);
    }

    @Override
    public BattleLog actInBattle(Character target, BattleEngine engine) {
        // 检查是否进入第二阶段
        if (!isInSecondPhase && this.getHP() < this.getMaxHP() * 0.5) {
            secondPhase();
        }

        Random rand = new Random();
        // 第一阶段技能
        if (!isInSecondPhase) {
            // 20%概率使用暗影箭
            if (rand.nextDouble() < 0.2) {
                return shadowBolt(target, engine);
            }
            // 10%概率恢复生命值
            if (rand.nextDouble() < 0.1) {
                heal();
                return new BattleLog("暗影魔王恢复了部分生命值！恢复了 40 点生命值。");
            }
        }
        // 第二阶段技能
        else {
            // 25%概率使用毁灭冲击
            if (rand.nextDouble() < 0.25) {
                return destructionBlast(target, engine);
            }
            // 15%概率使用暗影诅咒
            if (rand.nextDouble() < 0.15) {
                shadowCurse(target, engine);
                return new BattleLog("暗影魔王施放了暗影诅咒！");
            }
        }
        return engine.basicAttack(this, target);
    }

    // 进入第二阶段
    public void secondPhase() {
        System.out.println("暗影魔王的力量增强了！进入狂暴状态！");
        this.isInSecondPhase = true;
        this.setAttack(this.getAttack() + 15);
        this.setDefense(this.getDefense() + 10);
    }

    // 暗影箭：造成较高伤害
    public BattleLog shadowBolt(Character target, BattleEngine engine) {
        int damage = (int) (this.getAttack() * 1.8);
        target.takeDamage(damage);
        return new BattleLog("暗影魔王射出一支暗影箭！对 " + target.getName() + " 造成 " + damage + " 点伤害。");
    }

    // 毁灭冲击：造成巨大伤害
    public BattleLog destructionBlast(Character target, BattleEngine engine) {
        int damage = (int) (this.getAttack() * 2.5);
        target.takeDamage(damage);
        return new BattleLog("暗影魔王释放毁灭性的冲击波！对 " + target.getName() + " 造成 " + damage + " 点伤害。");
    }

    // 暗影诅咒：降低目标属性
    public BattleLog shadowCurse(Character target, BattleEngine engine) {
        // 这里可以添加降低目标属性的效果
        return new BattleLog("暗影魔王施放了暗影诅咒！");
    }

    // 治疗：恢复一定生命值
    public void heal() {
        System.out.println("暗影魔王恢复了部分生命值！");
        int healAmount = 40;
        this.setHP(Math.min(this.getHP() + healAmount, this.getMaxHP()));
    }
}