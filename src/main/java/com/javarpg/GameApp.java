package main.java.com.javarpg;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;

public class GameApp {
    public static void main(String[] args) {
        String story = "在一个遥远的魔法王国，黑暗势力逐渐蔓延。\n\n" +
                       "年轻的魔法师 cyxxx 啊，你肩负着拯救世界的使命！\n\n" +
                       "踏上旅程，与邪恶的生物战斗，揭开古老的秘密，成为传说中的英雄！\n\n" +
                       "你的冒险即将开始...";

        // 创建一个带有滚动条的文本区域来显示故事
        JTextArea textArea = new JTextArea(story);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setColumns(30);
        textArea.setRows(10);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 200));

        JOptionPane.showMessageDialog(null, scrollPane, "Java RPG 冒险之旅", JOptionPane.INFORMATION_MESSAGE);

        Magician player = new Magician();
        new GameMenu(player);
    }
}
