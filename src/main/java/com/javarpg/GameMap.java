package main.java.com.javarpg;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.AbstractMap;
import java.lang.Math;

public class GameMap extends JFrame implements KeyListener, MouseListener {
    // 核心对象
    private Magician m;
    // private Godzila g;
    
    // **新增：背景地图/地形数组**
    // 存储地图符号：# 墙壁，. 路径，^ 玩家初始位，! 怪物刷新区

    private int h_size=5;
    private int w_size=5;

    // 玩家在地图中的坐标 (y=行, x=列)
    private int village_x = 1;
    private int village_y = 4;
    private int forest_x = 0;
    private int forest_y = 2; 
    private int cave_x = 0;
    private int cave_y = 15; 
    private int x,y;
    //存放不同地图实例
    private boolean win=false;
    //不同场景地图的存放哈希表
    private Map<String,String[][]>maps=new HashMap<>();
    //不同场景存放角色起始位置的哈希表
    private Map<String,AbstractMap.SimpleEntry<Integer,Integer>>mapspos=new HashMap<>();
    
    //不同场景存放角色数组的哈希表
    private Map<String,ArrayList<ArrayList<Character>>>gmaps =new HashMap<>();
    //不同场景存放NPC实例的哈希表
    private Map<String, Map<String, NPC>> npcMaps = new HashMap<>();


    // 实体地图：仅用于存放 Character 实体（Player/Enemy），空位为 null
    private ArrayList<ArrayList<Character>> gm=new ArrayList<>();

    //不同场景地图的地形数组
    private String [][] terrain;
    // NPC 地图：存放 NPC 实例
    private Map<String,NPC>npcMap=new HashMap<>();

    // 根据玩家等级生成合适的随机敌人
    private Enemy generateRandomEnemy(int playerLevel) {
        Random random = new Random();

        // 根据玩家等级确定可遇到的敌人类型
        // 等级1-2：史莱姆、哥布林
        // 等级3-4：骷髅战士、兽人战士
        // 等级5+：火焰巨龙

        if (playerLevel <= 2) {
            int enemyType = random.nextInt(2);
            if (enemyType == 0) {
                return new Monster("史莱姆", 50, 0, 10, 5, 1, 10);
            } else {
                return new Goblin();
            }
        } else if (playerLevel <= 4) {
            int enemyType = random.nextInt(3);
            switch (enemyType) {
                case 0:
                    return new Monster("史莱姆", 50, 0, 10, 5, 1, 10);
                case 1:
                    return new Goblin();
                case 2:
                    return new Skeleton();
                default:
                    return new Monster("史莱姆", 50, 0, 10, 5, 1, 10);
            }
        } else {
            // 等级5以上可能遇到更强大的敌人
            int enemyType = random.nextInt(4);
            switch (enemyType) {
                case 0:
                    return new Monster("史莱姆", 50, 0, 10, 5, 1, 10);
                case 1:
                    return new Goblin();
                case 2:
                    return new Skeleton();
                case 3:
                    return new Orc();
                default:
                    return new Monster("史莱姆", 50, 0, 10, 5, 1, 10);
            }
        }
    }

    // 为不同地图生成相应的Boss
    private Enemy generateBossForMap(String mapName) {
        switch (mapName) {
            case "village":
                return new VillageChief();
            case "forest":
                return new ForestGuardian();
            case "cave":
                return new CaveBeast();
            case "mountain":
                return new IceDragon();
            case "final":
                // 最终Boss
                return new FinalBoss();
            default:
                return new Monster("未知敌人", 50, 0, 10, 5, 1, 10);
        }
    }
    
    // 战斗引擎：处理所有战斗计算
    private BattleEngine battleEngine = new BattleEngine();
    // 背包
    private Inventory inventory = new Inventory();

    // UI 组件：用于实时更新战斗信息
    private JProgressBar enemyHpBar;
    private JProgressBar playerHpBar;
    private JProgressBar playerMpBar;
    private JPanel mapJPanel;
    private JTextArea battleLogTextArea; // 战斗日志文本区域

    private int mhp_initial; // 初始/最大HP用于显示
    private int ghp_initial;
    private String showmapName;
    private String mapName="home";
    
    public GameMap(Magician m,String showmapName) {
        this.m = m;
        this.showmapName=showmapName;
        // 保存初始最大值，用于 UI 显示
        this.mhp_initial = m.getMaxHP();

        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initJFrame();
                initMap();
                initPos();
                initGM(); // 现在只初始化实体列表 gm
                addGameMap();
                setVisible(true);
            }
        });
    }

    // 新增：读档构造函数
    public GameMap(SaveData data) {
        this.m = data.player;
        this.showmapName = data.mapName;
        this.mhp_initial = m.getMaxHP();
        this.inventory = data.inventory; // 恢复背包

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initJFrame();
                initMap();
                initPos();
                
                // 覆盖当前地图的玩家位置为存档位置
                mapspos.put(showmapName, new AbstractMap.SimpleEntry<>(data.mapX, data.mapY));
                
                initGM(); 
                addGameMap();
                setVisible(true);
                show("欢迎回来，冒险者！");
            }
        });
    }


    private void initJFrame() {
        this.setSize(1200, 900);
        this.setTitle("Java RPG 冒险之旅 - 地图探索");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.addKeyListener(this);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 处理窗口关闭
                inventory.saveInventory();
                System.exit(0);
            }
        });
    }

    public void initMap(){
        String [][] villagemap={
            {"#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#"},
            {"#", "#", ".", ".", "!"},
            {"N", ".", ".", "#", "#"},
            {"#", "O", "#", "#", "#"}
        };
        String [][] forestmap={
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"0", ".", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "N", "#", "#", "#", "#", "#", "#"},
            {"N", ".", ".", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", ".", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", ".", ".", ".", ".", ".", "#", "#"},
            {"#", "#", "#", "#", ".", "#", "#", "!", "#", "#"},
        };
        String [][] cavemap={
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "N", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", ".", ".", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", ".", ".", "N", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", ".", "#", "#", ".", ".", ".", ".", "#", "#", "#"},
            {"#", "#", "#", "#", "N", ".", ".", "#", "#", "#", "#", "N", ".", ".", "#", "#"},
            {"#", "#", "#", "#", ".", ".", "N", "#", "#", "#", "#", "#", "#", ".", ".", "#"},
            {"#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "!"},
            {"#", "#", "#", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "N", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"0", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
        };
        String [][] mountainmap={
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", ".", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", ".", ".", ".", ".", ".", ".", ".", ".", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", ".", ".", ".", ".", ".", ".", ".", ".", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "N", ".", ".", ".", ".", ".", ".", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "!", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"}
        };
        // 最终地图：暗影王座
        String [][] finalmap={
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", ".", ".", ".", ".", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", ".", ".", "#", "#", "#", ".", ".", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", ".", "#", "#", "#", "#", ".", "#", "#", ".", ".", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", ".", ".", "#", "#", "#", ".", ".", "#", "#", "#", ".", "#", "#", "#", "#", "#"},
            {"#", "#", "#", ".", "#", "#", "#", "#", ".", ".", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#"},
            {"#", "#", ".", ".", "#", "#", "#", ".", ".", "#", "#", "#", "#", "#", ".", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", "#", "#", ".", ".", "#", "#", "#", "#", "#", "#", "!", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"}
        };
        maps.put("village", villagemap);
        maps.put("forest", forestmap);
        maps.put("cave", cavemap);
        maps.put("mountain", mountainmap);
        maps.put("final", finalmap);

    }
    public void initPos(){
        mapspos.put("village", new AbstractMap.SimpleEntry<>(village_x,village_y));
        mapspos.put("forest", new AbstractMap.SimpleEntry<>(forest_x,forest_y));
        mapspos.put("cave", new AbstractMap.SimpleEntry<>(cave_x,cave_y));
        // 山脉地图的起始位置 (设置在左下角的开放区域)
        mapspos.put("mountain", new AbstractMap.SimpleEntry<>(1, 4));
        // 最终地图的起始位置
        mapspos.put("final", new AbstractMap.SimpleEntry<>(14, 14));
    }
    public void initGM() {
        // 根据 terrain 数组的大小初始化 gm
        for (Map.Entry<String, String[][]> entry : maps.entrySet()) {
            String mapName = entry.getKey();
            String[][] mapData = entry.getValue();
            ArrayList<ArrayList<Character>> now = new ArrayList<>();
            Map<String, NPC> nowNPC=new HashMap<>();
            for (int i = 0; i < mapData.length; i++) {
                ArrayList<Character> row = new ArrayList<>();
                for (int j = 0; j < mapData[i].length; j++) {
                    if (mapData[i][j].equals("N")){
                        String npc_y=Integer.valueOf(i).toString();
                        String npc_x=Integer.valueOf(j).toString();
                        // 根据地图和位置决定NPC类型
                        NPC npc;
                        if (mapName.equals("village")) {
                            // 在村庄地图中，第一个N是村民，第二个是长老，第三个是商人
                            if (j == 3 && i == 3) {
                                npc = new Elder();
                            } else if (j == 0 && i == 3) {
                                npc = new Shopkeeper();
                            } else {
                                npc = new Villager();
                            }
                        } else if (mapName.equals("forest")) {
                            // 在森林地图中，第一个N是村民，其他是旅行者
                            if (j == 3 && i == 3) {
                                npc = new Villager();
                            } else {
                                npc = new NPC("旅行者", java.util.Arrays.asList(
                                    "这片森林充满了危险，你要小心。",
                                    "我听说深处有一只可怕的守护者。",
                                    "找到正确的道路才能穿越这片森林。"));
                            }
                        } else if (mapName.equals("mountain")) {
                            // 在山脉地图中，第一个N是登山向导
                            if (j == 2 && i == 10) {
                                npc = new MountainGuide();
                            } else {
                                npc = new NPC("登山者", java.util.Arrays.asList(
                                    "这座山峰终年积雪，非常危险。",
                                    "听说山顶有一条沉睡的冰霜巨龙。",
                                    "你需要特殊的装备才能攀登到顶峰。"));
                            }
                        } else {
                            // 其他地图使用默认村民
                            npc = new Villager();
                        }
                        nowNPC.put(npc_y+","+npc_x, npc);
                    }
                    row.add(null);
                }
                now.add(row);
            }
            npcMaps.put(mapName, nowNPC);
            int x=mapspos.get(mapName).getKey();
            int y=mapspos.get(mapName).getValue();
            if (y >= 0 && y < now.size() && x >= 0 && x < now.get(y).size()) {
                now.get(y).set(x, m);
            }
            gmaps.put(mapName, now);
        }
        //NPC
    }

    // 修正：使用 terrain 数组和 gm 列表共同渲染
    private void addGameMap() {
        if (win){
            show("恭喜你，勇者，战胜了十恶不赦的哥斯拉，但是你的冒险还没有结束，道路的前方依旧充满着未知，坚持下去吧，勇者！");
        }
        this.getContentPane().removeAll();
        if (this.mapName!=this.showmapName){
            this.x=mapspos.get(showmapName).getKey();
            this.y=mapspos.get(showmapName).getValue();
            this.gm=gmaps.get(showmapName);
            this.npcMap=npcMaps.get(showmapName);
            this.terrain=maps.get(showmapName);
            this.mapName=showmapName;
        }
        int yup=Math.max(0,y-h_size/2);
        int xleft=Math.max(0,x-w_size/2);
        int ydown=Math.min(terrain.length-1,y+h_size/2);
        int xright=Math.min(terrain[0].length-1,x+w_size/2);
        if (Integer.valueOf(yup).equals(0)){
            ydown=4;
        }else if (Integer.valueOf(ydown).equals(terrain.length-1)){
            yup=terrain.length-5;
        }
        if (Integer.valueOf(xleft).equals(0)){
            xright=4;
        }else if (Integer.valueOf(xright).equals(terrain[0].length-1)){
            xleft=terrain[0].length-5;
        }

        // 创建一个主面板来容纳地图和信息面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(35, 35, 45));

        // 创建地图面板
        JPanel mapPanel = new JPanel(new GridLayout(h_size, w_size));
        mapPanel.setBackground(new Color(50, 50, 60));
        mapPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = yup; i <= ydown; i++) {
            for (int j = xleft; j <= xright; j++) {
                String symbol = terrain[i][j]; // 获取背景符号

                // 如果该位置有 Character 实体 (Player/Enemy)
                if (gm.get(i).get(j) != null) {
                    symbol = "^"; // 玩家显示为 '^'
                }

                JLabel jlabel = new JLabel(symbol, SwingConstants.CENTER);
                jlabel.setFont(new Font("Monospaced", Font.BOLD, 32));
                jlabel.setForeground(symbol.equals("#") ? new Color(120, 120, 140) : new Color(240, 240, 240));
                jlabel.setBackground(new Color(50, 50, 60));
                jlabel.setOpaque(true);

                mapPanel.add(jlabel);
            }
        }

        // 创建信息面板显示玩家位置和地图信息
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(45, 45, 55));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 显示当前地图名称
        String mapDisplayName = getMapDisplayName(showmapName);
        JLabel mapLabel = new JLabel("当前地图: " + mapDisplayName);
        mapLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        mapLabel.setForeground(new Color(255, 223, 0)); // 金色
        mapLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 显示玩家坐标
        JLabel positionLabel = new JLabel("位置: (" + x + ", " + y + ")");
        positionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        positionLabel.setForeground(new Color(220, 220, 220));
        positionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 显示玩家等级和经验值
        JLabel levelLabel = new JLabel("等级: " + m.getLevel() + "  经验值: " + m.getExp() + "/" + m.getExpToNextLevel());
        levelLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        levelLabel.setForeground(new Color(173, 216, 230)); // 浅蓝色
        levelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 显示玩家状态
        JLabel playerStatusLabel = new JLabel("生命值: " + m.getHP() + "/" + m.getMaxHP() + "  魔法值: " + m.getMP() + "/" + m.getMaxMP());
        playerStatusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        playerStatusLabel.setForeground(new Color(220, 220, 220));
        playerStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 添加一键胜利按钮（仅用于演示）
        JButton victoryButton = new JButton("一键胜利 (V)");
        victoryButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        victoryButton.setBackground(new Color(255, 215, 0)); // 金色
        victoryButton.setForeground(Color.BLACK);
        victoryButton.setFocusPainted(false);
        victoryButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        victoryButton.addActionListener(e -> triggerVictory());

        infoPanel.add(mapLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(positionLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(levelLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(playerStatusLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(victoryButton);

        mainPanel.add(mapPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
        this.getContentPane().revalidate(); // 重新验证组件层次结构
        this.getContentPane().repaint();    // 重绘面板
    }

    // 获取地图的显示名称
    private String getMapDisplayName(String mapKey) {
        switch (mapKey) {
            case "village": return "村庄";
            case "forest": return "森林";
            case "cave": return "洞穴";
            case "mountain": return "雪山";
            case "final": return "最终战场";
            default: return mapKey;
        }
    }

    // 显示地图切换消息
    private void showMapChangeMessage(String oldMap, String newMap) {
        String oldMapName = getMapDisplayName(oldMap);
        String newMapName = getMapDisplayName(newMap);
        show("恭喜你击败了Boss！\n即将从" + oldMapName + "前往" + newMapName + "继续冒险！");
    }
    
    
    // --- 键盘事件处理：移动逻辑 ---

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        // 记录旧位置
        int oldX = x;
        int oldY = y;
        // 尝试移动
        if (e.getKeyCode() == KeyEvent.VK_W) { // 上
            y--;
        } else if (e.getKeyCode() == KeyEvent.VK_S) { // 下
            y++;
        } else if (e.getKeyCode() == KeyEvent.VK_A) { // 左
            x--;
        } else if (e.getKeyCode() == KeyEvent.VK_D) { // 右
            x++;
        }else if(e.getKeyCode()==KeyEvent.VK_Q){ // 打开背包
            // 添加打开背包的视觉反馈
            inventory.showInventory(m,this);
        }else if(e.getKeyCode()==KeyEvent.VK_P){ // 保存游戏
            // 添加保存游戏的视觉反馈
            saveGame();
        }else if(e.getKeyCode()==KeyEvent.VK_V){ // 一键胜利（演示功能）
            // 触发胜利
            triggerVictory();
        }

        // 边界和碰撞检测
        if (y < 0 || y >= terrain.length || x < 0 || x >= terrain[0].length || terrain[y][x].equals("#")) {
            // 移动无效，恢复位置
            x = oldX;
            y = oldY;
            // 可以在这里添加碰撞音效或视觉反馈
            return;
        }

        // 1. 清除旧位置的实体
        gm.get(oldY).set(oldX, null);

        // 2. 将玩家实体放置到新位置
        gm.get(y).set(x, m);

        // 3. 检查是否触发事件
        String currentTerrain = terrain[y][x];
        if (currentTerrain.equals("N")) {
            String npcKey = y + "," + x;
            if (npcMap.containsKey(npcKey)) {
                NPC npc = npcMap.get(npcKey);
                show(npc.getRandomDialogue());
            }
        } else {
            Random random = new Random();
            double rate  = 0.2;
            if(terrain[y][x].equals(".")){
                if(random.nextDouble()<rate){
                    System.out.println("遇到敌人");
                    Enemy encounteredEnemy = generateRandomEnemy(m.getLevel());
                    if (encounteredEnemy != null) {
                        // 添加遇到敌人的视觉反馈
                        fight(encounteredEnemy);
                    }
                }
            }if(terrain[y][x].equals("!")){
                    System.out.println("遇到敌人");
                    Enemy encounteredEnemy = generateBossForMap(mapName);
                    // 添加遇到Boss的视觉反馈
                    fight(encounteredEnemy);
            }
        }


        // 4. 刷新地图 UI
        addGameMap();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // 可以在此处设置一个调试用的战斗触发
            // fight(); 
        }
    }
    
    // ... (MouseListener 接口方法，保持为空) ...
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}


    // ---------------------------------------------------
    // 战斗 UI 和逻辑
    // ---------------------------------------------------

    public void fight(Enemy g) {
        // 确保战斗只在玩家存活时开始
        if (!m.isAlive()) {
             show("角色已死亡，无法战斗。");
             return;
        }

        JFrame fightFrame = new JFrame("战斗中 - VS " + g.getName());
        fightFrame.setSize(800, 600); // 进一步增大窗口尺寸
        fightFrame.setLocationRelativeTo(this);
        fightFrame.setLayout(new BorderLayout(15, 15));
        fightFrame.setAlwaysOnTop(true);
        fightFrame.setFocusable(true); // 确保窗口可以获取焦点
        fightFrame.requestFocusInWindow(); // 立即请求焦点
        fightFrame.getContentPane().setBackground(new Color(45, 45, 45));

        // 创建顶部信息面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(55, 55, 55));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 创建敌人信息面板
        JPanel enemyPanel = new JPanel();
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));
        enemyPanel.setBackground(new Color(55, 55, 55));
        enemyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel enemyNameLabel = new JLabel(g.getName());
        enemyNameLabel.setForeground(Color.RED);
        enemyNameLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));

        // 敌人HP进度条
        this.enemyHpBar = new JProgressBar(0, g.getMaxHP());
        this.enemyHpBar.setValue(g.getHP());
        this.enemyHpBar.setStringPainted(true);
        this.enemyHpBar.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        this.enemyHpBar.setForeground(Color.RED);
        this.enemyHpBar.setBackground(new Color(40, 40, 40));
        this.enemyHpBar.setString("HP: " + g.getHP() + "/" + g.getMaxHP());

        enemyPanel.add(enemyNameLabel);
        enemyPanel.add(Box.createVerticalStrut(8));
        enemyPanel.add(this.enemyHpBar);

        // 创建玩家信息面板
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.setBackground(new Color(55, 55, 55));
        playerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel playerNameLabel = new JLabel(m.getName());
        playerNameLabel.setForeground(Color.GREEN);
        playerNameLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));

        // 玩家HP进度条
        this.playerHpBar = new JProgressBar(0, m.getMaxHP());
        this.playerHpBar.setValue(m.getHP());
        this.playerHpBar.setStringPainted(true);
        this.playerHpBar.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        this.playerHpBar.setForeground(Color.GREEN);
        this.playerHpBar.setBackground(new Color(40, 40, 40));
        this.playerHpBar.setString("HP: " + m.getHP() + "/" + m.getMaxHP());

        // 玩家MP进度条
        this.playerMpBar = new JProgressBar(0, m.getMaxMP());
        this.playerMpBar.setValue(m.getMP());
        this.playerMpBar.setStringPainted(true);
        this.playerMpBar.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        this.playerMpBar.setForeground(Color.BLUE);
        this.playerMpBar.setBackground(new Color(40, 40, 40));
        this.playerMpBar.setString("MP: " + m.getMP() + "/" + m.getMaxMP());

        playerPanel.add(playerNameLabel);
        playerPanel.add(Box.createVerticalStrut(8));
        playerPanel.add(this.playerHpBar);
        playerPanel.add(Box.createVerticalStrut(5));
        playerPanel.add(this.playerMpBar);

        infoPanel.add(enemyPanel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(playerPanel);

        // 创建中央面板，包含操作按钮和战斗日志
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(45, 45, 45));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 创建操作面板
        JPanel actionPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        actionPanel.setBackground(new Color(45, 45, 45));

        // 创建美化后的按钮，增大按钮尺寸
        JButton basicAttack = createStyledButton("普攻", new Color(70, 130, 180), 16);
        basicAttack.addActionListener(e -> {
            // 添加点击反馈效果
            basicAttack.setBackground(new Color(90, 150, 200));
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                    basicAttack.setBackground(new Color(70, 130, 180));
                } catch (InterruptedException ex) {
                    basicAttack.setBackground(new Color(70, 130, 180));
                }
            });
            playerAction(m.useBasicAttack(g, battleEngine), fightFrame, g);
            fightFrame.requestFocusInWindow();
        });

        JButton smallSkill = createStyledButton("日之呼吸—炎舞 (MP: 2)", new Color(255, 140, 0), 16);
        smallSkill.addActionListener(e -> {
            // 添加点击反馈效果
            smallSkill.setBackground(new Color(255, 160, 20));
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                    smallSkill.setBackground(new Color(255, 140, 0));
                } catch (InterruptedException ex) {
                    smallSkill.setBackground(new Color(255, 140, 0));
                }
            });
            playerAction(m.useSmallSkill(g, battleEngine), fightFrame, g);
            fightFrame.requestFocusInWindow();
        });

        JButton bigSkill = createStyledButton("爆裂魔法 (MP: 4)", new Color(220, 20, 60), 16);
        bigSkill.addActionListener(e -> {
            // 添加点击反馈效果
            bigSkill.setBackground(new Color(240, 40, 80));
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                    bigSkill.setBackground(new Color(220, 20, 60));
                } catch (InterruptedException ex) {
                    bigSkill.setBackground(new Color(220, 20, 60));
                }
            });
            playerAction(m.useBigSkill(g, battleEngine), fightFrame, g);
            fightFrame.requestFocusInWindow();
        });

        JButton inventoryButton = createStyledButton("背包", new Color(154, 205, 50), 16);
        inventoryButton.addActionListener(e -> {
            // 添加点击反馈效果
            inventoryButton.setBackground(new Color(174, 225, 70));
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                    inventoryButton.setBackground(new Color(154, 205, 50));
                } catch (InterruptedException ex) {
                    inventoryButton.setBackground(new Color(154, 205, 50));
                }
            });
            Inventoryabout(fightFrame, g);
            fightFrame.requestFocusInWindow();
        });

        JButton fleeButton = createStyledButton("逃跑", new Color(169, 169, 169), 16);
        fleeButton.addActionListener(e -> {
            // 添加点击反馈效果
            fleeButton.setBackground(new Color(189, 189, 189));
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(100);
                    fleeButton.setBackground(new Color(169, 169, 169));
                } catch (InterruptedException ex) {
                    fleeButton.setBackground(new Color(169, 169, 169));
                }
            });
            fleeBattle(fightFrame, g);
        });

        // 添加快捷键提示标签
        JLabel shortcutLabel = new JLabel("快捷键: [1] 使用HP药水  [2] 使用MP药水  [ESC] 逃跑");
        shortcutLabel.setForeground(Color.LIGHT_GRAY);
        shortcutLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        shortcutLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 将按钮添加到操作面板
        actionPanel.add(basicAttack);
        actionPanel.add(smallSkill);
        actionPanel.add(bigSkill);
        actionPanel.add(inventoryButton);
        actionPanel.add(fleeButton);
        actionPanel.add(shortcutLabel);

        // 创建战斗日志面板
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(new Color(50, 50, 60));
        logPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
            "战斗日志",
            0,
            0,
            new Font("微软雅黑", Font.PLAIN, 14),
            new Color(220, 220, 220)
        ));

        battleLogTextArea = new JTextArea();
        battleLogTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        battleLogTextArea.setBackground(new Color(60, 60, 70));
        battleLogTextArea.setForeground(new Color(240, 240, 240));
        battleLogTextArea.setEditable(false);
        battleLogTextArea.setLineWrap(true);
        battleLogTextArea.setWrapStyleWord(true);

        JScrollPane logScrollPane = new JScrollPane(battleLogTextArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setPreferredSize(new Dimension(0, 100));

        logPanel.add(logScrollPane, BorderLayout.CENTER);

        centerPanel.add(actionPanel, BorderLayout.CENTER);
        centerPanel.add(logPanel, BorderLayout.SOUTH);

        //道具按键监听
        fightFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    endBattle(fightFrame, false, g);
                } else if (e.getKeyCode() == KeyEvent.VK_1) {
                    inventory.useItem(m, "HP");
                    updateCombatUI(g);
                } else if (e.getKeyCode() == KeyEvent.VK_2) {
                    inventory.useItem(m, "MP");
                    updateCombatUI(g);
                }
            }
        });

        // 组装界面
        fightFrame.add(infoPanel, BorderLayout.NORTH);
        fightFrame.add(centerPanel, BorderLayout.CENTER);

        fightFrame.setVisible(true);
    }

    // 创建样式化按钮的辅助方法
    private JButton createStyledButton(String text, Color backgroundColor) {
        return createStyledButton(text, backgroundColor, 14);
    }

    // 创建样式化按钮的辅助方法（带字体大小参数）
    private JButton createStyledButton(String text, Color backgroundColor, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, fontSize));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 2, true),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        // 添加悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker().darker(), 2, true),
                    BorderFactory.createEmptyBorder(12, 18, 12, 18)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker(), 2, true),
                    BorderFactory.createEmptyBorder(12, 18, 12, 18)
                ));
            }
        });

        return button;
    }

    // 逃跑方法
    private void fleeBattle(JFrame frame, Enemy g) {
        frame.dispose(); // 关闭战斗窗口

        // 检查敌人是否是最终Boss
        if (g instanceof FinalBoss) {
            show("面对如此强大的敌人，逃跑是明智的选择...\n但你终究还是要面对它的，勇者。");
        } else {
            show("算你运气好，给你成功逃跑了。");
        }
        // 战斗结束后，刷新一次地图，确保 UI 状态更新
        addGameMap();
    }

    public void Inventoryabout(JFrame fightFrame,Enemy g){
        System.out.println("1. 开始设置回调");
        inventory.setCombatUIupdateCallback(()->{
            System.out.println("3. 回调函数体正在执行");
            updateCombatUI(g);
        });
        System.out.println("2. 回调设置完成");
        // 设置背包窗口总是在最前面
        inventory.showInventory(m,fightFrame);
        updateCombatUI(g);
    }

    /**
     * 处理玩家行动后的流程：刷新UI -> 检查胜利 -> 怪物回合 -> 检查失败
     * @param battleLog 战斗日志
     * @param frame 当前战斗窗口
     */
    private void playerAction(BattleLog battleLog, JFrame frame, Enemy g) {
        if (battleLog == null) {
            show("行动失败！可能是蓝量不足。");
            return;
        }

        // 显示战斗日志
        System.out.println(battleLog.getMessage());
        if (battleLogTextArea != null) {
            battleLogTextArea.append(battleLog.getMessage() + "\n");
            // 自动滚动到最新日志
            battleLogTextArea.setCaretPosition(battleLogTextArea.getDocument().getLength());
        }

        // 1. 刷新 UI
        updateCombatUI(g);

        // 2. 检查玩家是否获胜 (怪物死亡)
        if (!g.isAlive()) {
            endBattle(frame, true, g);
            return;
        }

        // 3. 怪物回合
        System.out.println("\n--- 怪物回合 ---");
        BattleLog enemyLog = g.actInBattle(m, battleEngine);
        if (enemyLog != null) {
            System.out.println(enemyLog.getMessage());
            if (battleLogTextArea != null) {
                battleLogTextArea.append(enemyLog.getMessage() + "\n");
                // 自动滚动到最新日志
                battleLogTextArea.setCaretPosition(battleLogTextArea.getDocument().getLength());
            }
        }

        // 4. 刷新 UI
        updateCombatUI(g);

        // 5. 检查玩家是否失败 (玩家死亡)
        if (!m.isAlive()) {
            endBattle(frame, false, g);
        }
    }

    private void updateCombatUI(Enemy g) {
        // 更新敌人HP进度条
        if (enemyHpBar != null) {
            enemyHpBar.setMaximum(g.getMaxHP());
            enemyHpBar.setValue(g.getHP());
            enemyHpBar.setString("HP: " + g.getHP() + "/" + g.getMaxHP());
        }

        // 更新玩家HP和MP进度条
        if (playerHpBar != null) {
            playerHpBar.setMaximum(m.getMaxHP());
            playerHpBar.setValue(m.getHP());
            playerHpBar.setString("HP: " + m.getHP() + "/" + m.getMaxHP());
        }

        if (playerMpBar != null) {
            playerMpBar.setMaximum(m.getMaxMP());
            playerMpBar.setValue(m.getMP());
            playerMpBar.setString("MP: " + m.getMP() + "/" + m.getMaxMP());
        }
    }

    private void endBattle(JFrame frame, boolean playerWon,Enemy g) {
        frame.dispose(); // 关闭战斗窗口
        int HPcount=0;
        int MPcount=0;

        if (playerWon) {
            // 检查是否击败了最终Boss
            if (g instanceof FinalBoss) {
                // 玩家击败了最终Boss，游戏胜利
                showVictoryMessage();
                return;
            }

            // 结算奖励并更新玩家状态 (LevelUp/Exp)
            if (g instanceof VillageChief || g instanceof ForestGuardian || g instanceof CaveBeast || g instanceof IceDragon) {
                HPcount=10;
                MPcount=10;
                inventory.addItem("HP",10);
                inventory.addItem("MP",10);
                String oldMapName = showmapName;
                if (mapName.equals("village")){
                        showmapName="forest";
                    }else if(mapName.equals("forest")){
                        showmapName="cave";
                    }else if(mapName.equals("cave")){
                        // 在洞穴地图中击败Boss后，进入山脉地图
                        showmapName="mountain";
                    }else if(mapName.equals("mountain")){
                        // 在山脉地图中击败Boss后，进入最终战斗区域
                        showmapName="final";
                    }
                // 显示地图切换提示
                if (!oldMapName.equals(showmapName)) {
                    showMapChangeMessage(oldMapName, showmapName);
                }
            }else{
                HPcount=3;
                MPcount=3;
                inventory.addItem("HP",3);
                inventory.addItem("MP",3);
            }



            String winMessage = battleEngine.processBattleWin(m, g ,HPcount,MPcount);

            this.ghp_initial = g.getMaxHP();
            show(winMessage);
        } else {
            showDefeatMessage();
        }

        // 战斗结束后，刷新一次地图，确保 UI 状态更新
        addGameMap();
    }


    public void showVictoryMessage() {
        JDialog victoryDialog = new JDialog(this, true);
        victoryDialog.setTitle("游戏胜利");
        victoryDialog.setSize(600, 400);
        victoryDialog.setLocationRelativeTo(this);
        victoryDialog.getContentPane().setBackground(new Color(50, 50, 60));
        victoryDialog.setLayout(new BorderLayout(20, 20));

        // 胜利消息标签
        String victoryText = "<html>" +
                "<div style='text-align: center; font-family: 微软雅黑; font-size: 16px; color: #FFD700;'>" +
                "<h2>🎉 恭喜你，勇者！🎉</h2>" +
                "<p>你已经成功击败了暗影魔王，拯救了这个世界！</p>" +
                "<br/>" +
                "<p>在漫长的冒险旅程中，你经历了无数的挑战：</p>" +
                "<p>• 从宁静的村庄出发</p>" +
                "<p>• 穿越危险的森林</p>" +
                "<p>• 探索幽深的洞穴</p>" +
                "<p>• 攀登寒冷的雪山</p>" +
                "<p>• 最终在暗影王座击败了邪恶的暗影魔王</p>" +
                "<br/>" +
                "<p>你的勇气、智慧和毅力将被永远传颂！</p>" +
                "<p>这片土地将因你的英勇而重获和平与光明！</p>" +
                "</div>" +
                "</html>";

        JLabel victoryLabel = new JLabel(victoryText);
        victoryLabel.setHorizontalAlignment(JLabel.CENTER);
        victoryLabel.setVerticalAlignment(JLabel.CENTER);
        victoryLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(50, 50, 60));

        // 返回主菜单按钮
        JButton menuButton = new JButton("返回主菜单");
        menuButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        menuButton.setBackground(new Color(70, 130, 180));
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        menuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuButton.setBackground(new Color(100, 149, 237));
                menuButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180).darker().darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuButton.setBackground(new Color(70, 130, 180));
                menuButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        menuButton.addActionListener(e -> {
            victoryDialog.dispose();
            this.dispose();
            // 返回主菜单
            Magician newPlayer = new Magician();
            new GameMenu(newPlayer);
        });

        // 退出游戏按钮
        JButton exitButton = new JButton("退出游戏");
        exitButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitButton.setBackground(new Color(169, 169, 169));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(169, 169, 169).darker(), 2, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitButton.setBackground(new Color(189, 189, 189));
                exitButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(169, 169, 169).darker().darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setBackground(new Color(169, 169, 169));
                exitButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(169, 169, 169).darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        exitButton.addActionListener(e -> {
            victoryDialog.dispose();
            System.exit(0);
        });

        buttonPanel.add(menuButton);
        buttonPanel.add(exitButton);

        victoryDialog.add(victoryLabel, BorderLayout.CENTER);
        victoryDialog.add(buttonPanel, BorderLayout.SOUTH);
        victoryDialog.setVisible(true);
    }

    public void showDefeatMessage() {
        JDialog defeatDialog = new JDialog(this, true);
        defeatDialog.setTitle("游戏结束");
        defeatDialog.setSize(400, 200);
        defeatDialog.setLocationRelativeTo(this);
        defeatDialog.getContentPane().setBackground(new Color(50, 50, 60));
        defeatDialog.setLayout(new BorderLayout(20, 20));

        // 消息标签
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; font-family: 微软雅黑; font-size: 16px; color: #f0f0f0;'>你被击败了，游戏结束...</div></html>");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setVerticalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(50, 50, 60));

        // 新游戏按钮
        JButton newGameButton = new JButton("开始新游戏");
        newGameButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        newGameButton.setBackground(new Color(70, 130, 180));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false);
        newGameButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        newGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                newGameButton.setBackground(new Color(100, 149, 237));
                newGameButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180).darker().darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                newGameButton.setBackground(new Color(70, 130, 180));
                newGameButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        newGameButton.addActionListener(e -> {
            defeatDialog.dispose();
            this.dispose();
            // 创建新游戏
            Magician newPlayer = new Magician();
            new GameMenu(newPlayer);
        });

        // 退出按钮
        JButton exitButton = new JButton("退出游戏");
        exitButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitButton.setBackground(new Color(169, 169, 169));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(169, 169, 169).darker(), 2, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitButton.setBackground(new Color(189, 189, 189));
                exitButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(169, 169, 169).darker().darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setBackground(new Color(169, 169, 169));
                exitButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(169, 169, 169).darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        exitButton.addActionListener(e -> {
            defeatDialog.dispose();
            System.exit(0);
        });

        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);

        defeatDialog.add(messageLabel, BorderLayout.CENTER);
        defeatDialog.add(buttonPanel, BorderLayout.SOUTH);
        defeatDialog.setVisible(true);
    }

    // 一键胜利功能（仅用于演示）
    public void triggerVictory() {
        showVictoryMessage();
    }

    public void show(String s) {
        JDialog jDialog = new JDialog(this, true);
        jDialog.setSize(500, 250);
        jDialog.setAlwaysOnTop(true);
        jDialog.setLocationRelativeTo(this);
        jDialog.getContentPane().setBackground(new Color(50, 50, 60));

        jDialog.setLayout(new BorderLayout(20, 20));

        // 使用 HTML 支持换行和更好的样式
        JLabel j = new JLabel("<html><div style='text-align: center; font-family: 微软雅黑; font-size: 16px; color: #f0f0f0;'>" +
                             s.replaceAll("\n", "<br>") + "</div></html>");
        j.setHorizontalAlignment(JLabel.CENTER);
        j.setVerticalAlignment(JLabel.CENTER);
        j.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加确定按钮
        JButton okButton = new JButton("确定");
        okButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        okButton.setBackground(new Color(70, 130, 180));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                okButton.setBackground(new Color(100, 149, 237));
                okButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180).darker().darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                okButton.setBackground(new Color(70, 130, 180));
                okButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
                ));
            }
        });

        okButton.addActionListener(e -> jDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 50, 60));
        buttonPanel.add(okButton);

        jDialog.add(j, BorderLayout.CENTER);
        jDialog.add(buttonPanel, BorderLayout.SOUTH);
        jDialog.setVisible(true);
    }
    public void saveGame() {
        // 显示存档槽位选择对话框
        String[] options = new String[GameSaver.getMaxSaveSlots()];
        for (int i = 0; i < options.length; i++) {
            int slot = i + 1;
            if (GameSaver.hasSaveFile(slot)) {
                options[i] = "槽位 " + slot + " (已有存档)";
            } else {
                options[i] = "槽位 " + slot + " (空)";
            }
        }

        int selectedSlot = JOptionPane.showOptionDialog(
            this,
            "请选择存档槽位:",
            "保存游戏",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (selectedSlot >= 0) {
            // 获取当前玩家在当前地图的坐标
            // 注意：this.x 和 this.y 应该是当前地图的坐标
            SaveData data = new SaveData(m, inventory, x, y, showmapName);
            if (GameSaver.saveGame(data, selectedSlot + 1)) {
                show("游戏已保存到槽位 " + (selectedSlot + 1) + "！");
            } else {
                show("保存失败！请检查文件权限。");
            }
        }
    }
}