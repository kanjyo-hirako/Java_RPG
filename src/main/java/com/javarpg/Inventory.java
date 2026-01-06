package main.java.com.javarpg;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


public class Inventory implements java.io.Serializable {
    private List<Item> items = new ArrayList<>();

    private transient Runnable combatUIupdateCallback;

    private transient JFrame inventoryFrame;
    private File inventoryFile=new File("./main/resources/Inventory.txt");
    public Inventory() {
        try(Scanner scanner=new Scanner(inventoryFile)){
            while(scanner.hasNextLine()){
                String line=scanner.nextLine();
                System.out.println("处理的行: " + line);
                String[] parts=line.split(",");
                String ItemName=parts[0];
                int Itemcount=Integer.parseInt(parts[1]);
                if(ItemName.equals("HP")){
                    items.add(new HP(ItemName,Itemcount));
                }
                else if(ItemName.equals("MP")){
                    items.add(new MP(ItemName,Itemcount));
                }
            }
        }catch(IOException e){
            System.out.println("文件未找到");
        }
    }

    public void saveInventory(){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(inventoryFile))){
            for(Item item:items){
                writer.write(item.getName()+","+item.getCount()+"\n");
            }
        }catch(IOException e){
            System.out.println("文件未找到");
        }
    }
    private int findItem(String itemName){
        for(int i=0;i<items.size();i++){
            if(items.get(i).getName().equals(itemName)){
                return i;
            }
        }
        return -1;
    }
    public void addItem(String itemName,int count) {
        if(itemName.equals("HP")){
            int index=findItem(itemName);
            if(index==-1){
                items.add(new HP(itemName,count));
            }
            else{
                items.get(index).setCount(count);
            }
        }
        else if(itemName.equals("MP")){
            int index=findItem(itemName);
            if(index==-1){
                items.add(new MP(itemName,count));
            }
            else{
                items.get(index).setCount(count);
            }
        }
    }
    
    public void useItem(Character user,String Itemname) {
        int index=findItem(Itemname);
        if(index!=-1){
            items.get(index).use(user);
            if(items.get(index).getCount().equals(0)){
                items.remove(index);
            }
        }
        else{
            System.out.println("道具未获得");
        }
    }
    public void displayItems() {
        System.out.println("背包道具如下:");
        for(int i=0;i<items.size();i++){
            System.out.println(items.get(i).getName()+"*"+items.get(i).getCount());
        }
    }

    //接受回调方法
    public void setCombatUIupdateCallback(Runnable callback){
        this.combatUIupdateCallback=callback;
    }


    //背包窗口
    public void showInventory(Magician user, JFrame Parent) {
        inventoryFrame = new JFrame("背包道具栏 - " + user.getName());
        inventoryFrame.setSize(450, 350);
        inventoryFrame.setLocationRelativeTo(Parent);
        inventoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inventoryFrame.getContentPane().setBackground(new Color(35, 35, 45));
        inventoryFrame.setAlwaysOnTop(true); // 确保背包窗口总是在最前面

        // 创建主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(35, 35, 45));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建标题标签
        JLabel titleLabel = new JLabel("背包道具", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 223, 0)); // 更亮的金色
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 创建道具面板
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 18));
        itemsPanel.setBackground(new Color(50, 50, 65));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建提示标签
        JLabel hintLabel = new JLabel("按 ESC 键关闭背包", SwingConstants.CENTER);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        hintLabel.setForeground(new Color(220, 220, 220));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(itemsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(hintLabel);

        inventoryFrame.add(mainPanel);

        inventoryFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 窗口关闭后，将焦点返回给父窗口
                if (Parent != null) {
                    Parent.requestFocusInWindow();
                    Parent.toFront(); // 将父窗口带到前面
                }
            }
        });

        inventoryFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    inventoryFrame.dispose();
                }
            }
        });

        // 获取窗口的焦点，确保键盘事件能够被正确捕获
        inventoryFrame.setFocusable(true);
        inventoryFrame.requestFocusInWindow();

        // 绘制背包窗口
        repaintInventory(user, itemsPanel);
        inventoryFrame.setVisible(true);
    }


    public void repaintInventory(Magician user) {
        // 这个方法保持向后兼容
        repaintInventory(user, null);
    }

    public void repaintInventory(Magician user, JPanel itemsPanel) {
        if (itemsPanel != null) {
            // 清除旧的道具按钮
            itemsPanel.removeAll();

            // 添加道具按钮
            for (Item item : items) {
                if (!item.getCount().equals(0)) {
                    JButton itemButton = createItemButton(item);
                    itemButton.addActionListener(e -> FrameUse(user, item));
                    itemsPanel.add(itemButton);
                }
            }

            itemsPanel.revalidate();
            itemsPanel.repaint();
        } else {
            // 向后兼容的实现
            inventoryFrame.getContentPane().removeAll();

            for (Item item : items) {
                JButton itemButton = new JButton(item.getName() + "*" + item.getCount());
                if (!item.getCount().equals(0)) {
                    itemButton.addActionListener(e -> FrameUse(user, item));
                    inventoryFrame.getContentPane().add(itemButton);
                }
            }
            inventoryFrame.getContentPane().validate();
            inventoryFrame.getContentPane().repaint();
        }
    }

    // 创建样式化的道具按钮
    private JButton createItemButton(Item item) {
        JButton button = new JButton("<html><center>" + item.getName() + "<br/>数量: " + item.getCount() + "</center></html>");
        button.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        button.setBackground(new Color(90, 90, 140));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(110, 110, 160), 2, true),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        // 添加悬停和点击效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(110, 110, 160));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(130, 130, 180), 2, true),
                    BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(90, 90, 140));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(110, 110, 160), 2, true),
                    BorderFactory.createEmptyBorder(12, 20, 12, 20)
                ));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // 添加点击反馈效果
                button.setBackground(new Color(130, 130, 180));
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(100);
                        button.setBackground(new Color(110, 110, 160));
                    } catch (InterruptedException ex) {
                        button.setBackground(new Color(110, 110, 160));
                    }
                });
            }
        });

        return button;
    }

    public void FrameUse(Magician user,Item item){
        item.use(user);
        if(combatUIupdateCallback!=null){
            combatUIupdateCallback.run();
            System.out.println("回调结束");
        }
        // 使用道具后自动关闭背包窗口
        if(inventoryFrame != null) {
            inventoryFrame.dispose();
        }
    }


}