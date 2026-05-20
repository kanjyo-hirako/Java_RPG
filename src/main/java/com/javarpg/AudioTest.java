package main.java.com.javarpg;

import main.java.com.javarpg.AudioPlayer;

public class AudioTest {
    public static void main(String[] args) {
        System.out.println("开始测试音频播放...");
        try {
            // 测试文件是否存在
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            java.net.URL resource = classLoader.getResource("background_music.wav");
            if (resource != null) {
                System.out.println("找到音乐文件: " + resource.getPath());

                // 尝试创建AudioPlayer
                AudioPlayer player = new AudioPlayer("background_music.wav");
                System.out.println("音频播放器创建成功");

                // 等待几秒
                Thread.sleep(3000);
                player.stop();
                System.out.println("音频播放结束");
            } else {
                System.out.println("未找到音乐文件");
                // 列出所有资源
                System.out.println("类路径资源:");
                java.net.URL rootResource = classLoader.getResource("");
                if (rootResource != null) {
                    System.out.println("根资源路径: " + rootResource.getPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}