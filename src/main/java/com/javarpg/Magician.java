package main.java.com.javarpg;

public class Magician extends Player {
    // 初始化属性
    public Magician() {
        super("cyxxx", 100, 50, 20, 5, 1, 0, 50);
    }

    // 玩家由 UI 驱动，此方法在玩家类中留空
    public void actInBattle(Character target, BattleEngine engine) {

    }
    public BattleLog useBasicAttack(Character target, BattleEngine engine) {
        return engine.basicAttack(this, target);
    }

    public BattleLog useSmallSkill(Character target, BattleEngine engine) {
        return engine.smallSkill(this, target);
    }

    // 大招方法 (委托给 BattleEngine)
    public BattleLog useBigSkill(Character target, BattleEngine engine) {
        // 使用 BigSkill 方法
        return engine.bigSkill(this, target);
    }
}