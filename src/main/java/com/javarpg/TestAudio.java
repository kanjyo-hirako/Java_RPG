package main.java.com.javarpg;

public class TestAudio {
    public static void main(String[] args) {
        System.out.println("测试音频播放...");
        AudioPlayer player = new AudioPlayer("background_music.wav");

        // 等待几秒钟听音乐
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        player.stop();
        System.out.println("测试完成");
    }
}