package main.java.com.javarpg;

import javax.swing.*;
import java.awt.*;

public class Player extends Character{
    public Player(String name, int maxHP, int maxMP, int attack, int defense, int level, int exp, int expToNextLevel) {
        super(name, maxHP, maxMP, attack, defense, level);
        this.exp = exp;
        this.expToNextLevel = expToNextLevel;
    }
    private int exp;//经验  
    private int expToNextLevel; // 升到下一级所需经验
    public int getExp() { return exp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    
    public void gainExp(int amount) {
        this.exp += amount;
        System.out.println(getName() + " 获得了 " + amount + " 点经验。");
        while (this.exp >= this.expToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        this.exp -= this.expToNextLevel;
        this.setLevel(getLevel() + 1);
        this.setMaxHP(getMaxHP() + 15);
        this.setMaxMP(getMaxMP() + 10);
        this.setAttack(getAttack() + 3);
        this.setDefense(getDefense() + 1);
        this.expToNextLevel = (int) (this.expToNextLevel * 1.3); // 调整经验升级曲线，让升级更容易
        showLevelUpDialog();
    }

    // 升级弹窗方法
    public void showLevelUpDialog() {
        System.out.println("🎉 " + getName() + " 升级到 Level " + getLevel() + "!");

        // 创建升级弹窗
        JDialog levelUpDialog = new JDialog();
        levelUpDialog.setTitle("升级提醒");
        levelUpDialog.setSize(400, 250);
        levelUpDialog.setAlwaysOnTop(true);
        levelUpDialog.setLocationRelativeTo(null);
        levelUpDialog.getContentPane().setBackground(new Color(50, 50, 60));
        levelUpDialog.setLayout(new BorderLayout(20, 20));

        // 消息标签
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; font-family: 微软雅黑; font-size: 16px; color: #f0f0f0;'>" +
                "🎉 恭喜升级！<br>" +
                getName() + " 升级到 Level " + getLevel() + "!<br><br>" +
                "生命值 +" + 15 + "<br>" +
                "魔法值 +" + 10 + "<br>" +
                "攻击力 +" + 3 + "<br>" +
                "防御力 +" + 1 + "</div></html>");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setVerticalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 确定按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(50, 50, 60));

        JButton confirmButton = new JButton("确定");
        confirmButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        confirmButton.addActionListener(e -> levelUpDialog.dispose());
        buttonPanel.add(confirmButton);

        levelUpDialog.add(messageLabel, BorderLayout.CENTER);
        levelUpDialog.add(buttonPanel, BorderLayout.SOUTH);

        // 显示弹窗
        levelUpDialog.setVisible(true);
    }

    @Override
    public void displayStatus() {
        System.out.println("玩家状态：" + getName() + " Lv." + getLevel() +
                           " HP:" + getHP() + "/" + getMaxHP() +
                           " MP:" + getMP() + "/" + getMaxMP()+
                           " 经验: " + getExp() + "/" + getExpToNextLevel());
    }

}
