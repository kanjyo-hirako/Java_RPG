package main.java.com.javarpg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class GameMenu extends JFrame {

    // 预设图片路径常量。重要：如果使用 Maven 或 IDE，请将图片放在 src/main/resources/images/ 目录下。
    // 如果直接使用文件路径，请确保这些路径相对于您的运行目录是正确的。
    private static final String TITLE_IMAGE_PATH = "images/title.png";
    private static final String START_BUTTON_PATH = "images/start_button.png";
    private static final String LOAD_BUTTON_PATH = "images/load_button.png";
    private static final String SETTINGS_BUTTON_PATH = "images/settings_button.png";
    private static final String EXIT_BUTTON_PATH = "images/exit_button.png";
    private static final String BACKGROUND_MUSIC_PATH = "background_music.wav";//背景音乐路径

    // 游戏核心对象，用于传递给 GameMap
    private final Magician player;
    // 音频播放器
    private static AudioPlayer backgroundMusic;

    public GameMenu(Magician player) { 
        this.player = player;
        // 初始化背景音乐
        backgroundMusic = new AudioPlayer(BACKGROUND_MUSIC_PATH);
        SwingUtilities.invokeLater(this::initMenu);
    }

    private void initMenu() {
        this.setTitle("Java RPG 冒险之旅");
        this.setSize(950, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // 设置整个窗口的默认背景色
        this.getContentPane().setBackground(new Color(35, 35, 45));

        // 1. 创建并添加标题面板 (Title Panel)
        addTitlePanel();

        // 2. 创建并添加按钮面板 (Button Panel)
        addButtonPanel();

        this.setVisible(true);
    }

    /**
     * 创建标题面板，用于放置游戏 Logo/标题图片
     */
    private void addTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 120));
        titlePanel.setBackground(new Color(35, 35, 45));
        titlePanel.setOpaque(true);

        JLabel titleLabel = new JLabel("Java RPG 冒险之旅");
        titleLabel.setForeground(new Color(255, 223, 0)); // 更亮的金色文字
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 56));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 添加阴影效果
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30),
            BorderFactory.createEtchedBorder(new Color(100, 100, 100), new Color(60, 60, 70))
        ));

        titlePanel.add(titleLabel);
        this.add(titlePanel, BorderLayout.NORTH);
    }

    /**
     * 创建按钮面板，用于放置所有可点击的选项
     */
    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(35, 35, 45));
        buttonPanel.setOpaque(true);

        buttonPanel.add(Box.createVerticalGlue());

        // 创建按钮并添加到面板
        buttonPanel.add(createImageButton("开始新的冒险", START_BUTTON_PATH, this::startGame));
        // 添加按钮之间的间距
        buttonPanel.add(Box.createVerticalStrut(30));

        buttonPanel.add(createImageButton("继续旅程", LOAD_BUTTON_PATH, this::loadGame));
        buttonPanel.add(Box.createVerticalStrut(30));

        buttonPanel.add(createImageButton("游戏设置", SETTINGS_BUTTON_PATH, this::showSettingsPanel));
        buttonPanel.add(Box.createVerticalStrut(30));

        buttonPanel.add(createImageButton("暂别世界", EXIT_BUTTON_PATH, this::exitGame));

        buttonPanel.add(Box.createVerticalGlue());

        this.add(buttonPanel, BorderLayout.CENTER);
    }
    
    /**
     * 创建一个包含图片和点击事件的 JLabel (模拟按钮)
     */
    private JLabel createImageButton(String defaultText, String imagePath, Runnable action) {
        JLabel button = new JLabel(defaultText, SwingConstants.CENTER);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setForeground(new Color(240, 240, 240)); // 默认浅白色字体
        button.setFont(new Font("微软雅黑", Font.BOLD, 28));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 110), 2, true),
            BorderFactory.createEmptyBorder(18, 30, 18, 30)
        ));
        button.setBackground(new Color(65, 65, 85));
        button.setOpaque(true);

        boolean imageLoaded = false;
        try {
            URL imageUrl = getClass().getClassLoader().getResource(imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                button.setIcon(icon);
                button.setText(""); // 图片加载成功，清空文本
                button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
                imageLoaded = true;
            }
        } catch (Exception e) {
            // 图片加载失败，保持默认文本
            System.err.println("警告: 无法加载按钮图片 " + imagePath);
        }

        // 如果图片加载失败，确保文本显示且提供最小尺寸
        if (!imageLoaded) {
            button.setText(defaultText);
            button.setPreferredSize(new Dimension(400, 80));
        }

        // 添加鼠标点击事件
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 添加点击效果和音效
                button.setBackground(new Color(85, 85, 105));
                // 播放点击音效（如果音频系统支持）
                if (backgroundMusic != null) {
                    // 可以在这里添加点击音效
                }
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(100);
                        button.setBackground(new Color(65, 65, 85));
                    } catch (InterruptedException ex) {
                        button.setBackground(new Color(65, 65, 85));
                    }
                });
                action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(255, 223, 0)); // 悬停时变为更亮的金色
                button.setBackground(new Color(75, 75, 95));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 223, 0), 2, true),
                    BorderFactory.createEmptyBorder(18, 30, 18, 30)
                ));
                // 悬停音效
                if (backgroundMusic != null) {
                    // 可以在这里添加悬停音效
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(240, 240, 240)); // 恢复默认颜色
                button.setBackground(new Color(65, 65, 85));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(90, 90, 110), 2, true),
                    BorderFactory.createEmptyBorder(18, 30, 18, 30)
                ));
            }
        });

        return button;
    }


    private void startGame() {
        System.out.println("--- 开始新游戏 ---");
        this.dispose(); 
        // 确保 Magician/Godzila 类和 GameMap 类存在且可访问
        new GameMap(player,"village"); 
    }

    private void loadGame() {
        // 检查是否有任何存档
        boolean hasAnySave = false;
        for (int i = 1; i <= GameSaver.getMaxSaveSlots(); i++) {
            if (GameSaver.hasSaveFile(i)) {
                hasAnySave = true;
                break;
            }
        }

        if (hasAnySave) {
            // 构建槽位选项
            String[] options = new String[GameSaver.getMaxSaveSlots()];
            int validSlotCount = 0;

            for (int i = 0; i < options.length; i++) {
                int slot = i + 1;
                if (GameSaver.hasSaveFile(slot)) {
                    options[i] = "槽位 " + slot + " (已存档)";
                    validSlotCount++;
                } else {
                    options[i] = "槽位 " + slot + " (空)";
                }
            }

            // 如果只有一个有效存档，直接加载它
            if (validSlotCount == 1) {
                int slotToLoad = -1;
                for (int i = 1; i <= GameSaver.getMaxSaveSlots(); i++) {
                    if (GameSaver.hasSaveFile(i)) {
                        slotToLoad = i;
                        break;
                    }
                }

                if (slotToLoad != -1) {
                    loadGameFromSlot(slotToLoad);
                    return;
                }
            }

            // 显示选择对话框
            int selectedSlot = JOptionPane.showOptionDialog(
                this,
                "请选择要读取的存档槽位:",
                "读取游戏",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (selectedSlot >= 0 && GameSaver.hasSaveFile(selectedSlot + 1)) {
                loadGameFromSlot(selectedSlot + 1);
            } else if (selectedSlot >= 0) {
                JOptionPane.showMessageDialog(this, "该槽位没有存档。", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "没有找到任何存档记录。", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadGameFromSlot(int slot) {
        SaveData data = GameSaver.loadGame(slot);
        if (data != null) {
            System.out.println("--- 读取存档成功 (槽位 " + slot + ") ---");
            this.dispose();
            // 使用新的构造函数启动游戏
            new GameMap(data);
        } else {
            JOptionPane.showMessageDialog(this, "读取存档失败！文件可能已损坏。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSettingsPanel() {
        JDialog settingsDialog = new JDialog(this, "游戏设置", true);
        settingsDialog.setSize(450, 300);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.getContentPane().setBackground(new Color(35, 35, 45));

        // 创建主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(35, 35, 45));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 创建标题标签
        JLabel titleLabel = new JLabel("游戏设置", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 223, 0)); // 更亮的金色
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 创建音量控制面板
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        volumePanel.setBackground(new Color(50, 50, 65));
        volumePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 110), 2, true),
            "音量控制",
            0,
            0,
            new Font("微软雅黑", Font.PLAIN, 16),
            new Color(220, 220, 220)
        ));

        JLabel volumeLabel = new JLabel("音量:");
        volumeLabel.setForeground(new Color(240, 240, 240));
        volumeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));

        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setBackground(new Color(50, 50, 65));
        volumeSlider.setForeground(new Color(240, 240, 240));
        volumeSlider.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 设置初始音量
        backgroundMusic.setVolume(50);

        volumeSlider.addChangeListener(e -> {
            int volume = volumeSlider.getValue();
            backgroundMusic.setVolume(volume);
            System.out.println("音量调节到: " + volumeSlider.getValue());
        });

        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(35, 35, 45));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton closeButton = new JButton("确定");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        closeButton.setBackground(new Color(70, 130, 180));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(new Color(100, 149, 237));
                closeButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180).darker().darker(), 2, true),
                    BorderFactory.createEmptyBorder(10, 25, 10, 25)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(new Color(70, 130, 180));
                closeButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180).darker(), 2, true),
                    BorderFactory.createEmptyBorder(10, 25, 10, 25)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // 添加点击确认音效
                if (backgroundMusic != null) {
                    // 可以在这里添加确认音效
                }
            }
        });

        closeButton.addActionListener(e -> {
            // 添加关闭动画效果
            settingsDialog.dispose();
        });

        buttonPanel.add(closeButton);

        // 组装界面
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(volumePanel);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(buttonPanel);

        settingsDialog.add(mainPanel);
        settingsDialog.setVisible(true);
    }
    
    private void exitGame() {
        System.exit(0);
    }
}