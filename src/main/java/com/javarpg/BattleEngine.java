package main.java.com.javarpg;

import java.util.Random;

public class BattleEngine {
    private Random random = new Random();

    public BattleLog basicAttack(Character attacker, Character target) {
        int rawDamage = attacker.getAttack();

        // 检查是否暴击（15%概率）
        boolean isCritical = random.nextDouble() < 0.15;
        if (isCritical) {
            rawDamage = (int)(rawDamage * 1.5); // 暴击伤害为1.5倍
        }

        target.takeDamage(rawDamage);

        return new BattleLog(attacker.getName() + " 对 " + target.getName() + " 使用普通攻击，造成 " + rawDamage + " 点伤害。" + (isCritical ? "【暴击！】" : ""));
    }

    public BattleLog smallSkill(Player player, Character target) {
        if (player.getMP() < 2) {
            return new BattleLog(player.getName() + " 蓝量不足！");
        }
        player.setMP(player.getMP() - 2);
        int rawDamage = (int)(player.getAttack() * 1.5);

        // 检查是否暴击（20%概率）
        boolean isCritical = random.nextDouble() < 0.20;
        if (isCritical) {
            rawDamage = (int)(rawDamage * 1.5); // 暴击伤害为1.5倍
        }

        target.takeDamage(rawDamage);
        return new BattleLog(player.getName() + " 使用小技能攻击 " + target.getName() + "，造成 " + rawDamage + " 点伤害。" + (isCritical ? "【暴击！】" : ""));
    }

    public BattleLog bigSkill(Player player, Character target) {
        if (player.getMP() < 4) {
            return new BattleLog(player.getName() + " 蓝量不足！");
        }
        player.setMP(player.getMP() - 4);
        int rawDamage = (int)(player.getAttack() * 2.0);

        // 检查是否暴击（25%概率）
        boolean isCritical = random.nextDouble() < 0.25;
        if (isCritical) {
            rawDamage = (int)(rawDamage * 1.5); // 暴击伤害为1.5倍
        }

        target.takeDamage(rawDamage);
        return new BattleLog(player.getName() + " 使用大招攻击 " + target.getName() + "，造成 " + rawDamage + " 点伤害。" + (isCritical ? "【暴击！】" : ""));
    }

    /*
      @param winner 战斗胜利者
      @param loser 战斗失败者
      @return 奖励信息字符串
     */

    public String processBattleWin(Player winner, Enemy loser,int HPcount,int MPcount) {
        String result = "恭喜你，战斗胜利！\n";

        // 经验
        int expGained = loser.getExpReward();
        winner.gainExp(expGained);
        result += "获得 " + expGained + " 点经验。\n";
        result += "获得 " + HPcount + " 瓶生命药水。\n";
        result += "获得 " + MPcount + " 瓶蓝量药水。\n";

        //战斗胜利回复10滴血
        winner.setHP(Math.min(winner.getMaxHP(), winner.getHP() + 10)); // 少量回复HP

        return result;
    }
}