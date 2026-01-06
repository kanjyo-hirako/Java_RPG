package main.java.com.javarpg;

import java.util.Random;

public class IceDragon extends Enemy {
    private boolean isInSecondPhase = false;

    public IceDragon() {
        super("冰霜巨龙", 400, 150, 50, 30, 10, 300);
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
            // 20%概率使用冰霜吐息
            if (rand.nextDouble() < 0.2) {
                return frostBreath(target, engine);
            }
            // 15%概率冻结目标
            if (rand.nextDouble() < 0.15) {
                freezeTarget(target);
                return new BattleLog("冰霜巨龙释放出强烈的寒气，试图冻结" + target.getName() + "！");
            }
        }
        // 第二阶段技能
        else {
            // 25%概率使用暴风雪
            if (rand.nextDouble() < 0.25) {
                return blizzard(target, engine);
            }
            // 20%概率使用冰锥术
            if (rand.nextDouble() < 0.2) {
                return iceSpike(target, engine);
            }
        }
        return engine.basicAttack(this, target);
    }

    // 进入第二阶段
    public void secondPhase() {
        System.out.println("冰霜巨龙进入了狂暴状态！周围的温度骤降！");
        this.isInSecondPhase = true;
        this.setAttack(this.getAttack() + 20);
        this.setDefense(this.getDefense() + 15);
    }

    // 冰霜吐息：造成较高伤害并有可能冻结目标
    public BattleLog frostBreath(Character target, BattleEngine engine) {
        int damage = (int) (this.getAttack() * 2.0);
        target.takeDamage(damage);
        String freezeMsg = "";
        // 30%概率冻结目标（使目标下一回合无法行动）
        if (new Random().nextDouble() < 0.3) {
            freezeMsg = target.getName() + "被冻结了，下一回合无法行动！";
        }
        return new BattleLog("冰霜巨龙喷吐出刺骨的寒气！对 " + target.getName() + " 造成 " + damage + " 点伤害。" + freezeMsg);
    }

    // 冻结目标：有一定概率使目标无法行动一回合
    public void freezeTarget(Character target) {
        System.out.println("冰霜巨龙释放出强烈的寒气，试图冻结" + target.getName() + "！");
        // 冻结效果的具体实现可以在Player类中添加相关状态
    }

    // 暴风雪：造成巨大伤害并降低目标攻击力
    public BattleLog blizzard(Character target, BattleEngine engine) {
        int damage = (int) (this.getAttack() * 2.8);
        target.takeDamage(damage);
        return new BattleLog("冰霜巨龙召唤了一场猛烈的暴风雪！对 " + target.getName() + " 造成 " + damage + " 点伤害。" + target.getName() + "在暴风雪中迷失了方向，攻击力暂时下降！");
    }

    // 冰锥术：连续攻击三次
    public BattleLog iceSpike(Character target, BattleEngine engine) {
        StringBuilder logMsg = new StringBuilder("冰霜巨龙召唤出三根锋利的冰锥连续攻击！");
        for (int i = 0; i < 3; i++) {
            int damage = this.getAttack();
            target.takeDamage(damage);
            logMsg.append("第").append(i+1).append("根冰锥造成了").append(damage).append("点伤害！");
            if (i < 2) logMsg.append(" ");
        }
        return new BattleLog(logMsg.toString());
    }
}