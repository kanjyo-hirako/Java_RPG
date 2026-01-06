package main.java.com.javarpg;

import java.io.*;

public class GameSaver {
    // 存档文件路径模板
    private static final String SAVE_FILE_TEMPLATE = "savegame_slot_%d.dat";
    private static final int MAX_SAVE_SLOTS = 3; // 最多支持3个存档槽位

    // 保存游戏到指定槽位
    public static boolean saveGame(SaveData data, int slot) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            System.err.println("无效的存档槽位: " + slot);
            return false;
        }

        String saveFilePath = String.format(SAVE_FILE_TEMPLATE, slot);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFilePath))) {
            oos.writeObject(data);
            System.out.println("游戏已保存至槽位 " + slot + ": " + new File(saveFilePath).getAbsolutePath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 保存游戏到默认槽位1
    public static boolean saveGame(SaveData data) {
        return saveGame(data, 1);
    }

    // 读取指定槽位的游戏
    public static SaveData loadGame(int slot) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            System.err.println("无效的存档槽位: " + slot);
            return null;
        }

        String saveFilePath = String.format(SAVE_FILE_TEMPLATE, slot);
        File file = new File(saveFilePath);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (SaveData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 读取默认槽位的游戏
    public static SaveData loadGame() {
        return loadGame(1);
    }

    // 检查指定槽位是否存在存档
    public static boolean hasSaveFile(int slot) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            return false;
        }

        String saveFilePath = String.format(SAVE_FILE_TEMPLATE, slot);
        return new File(saveFilePath).exists();
    }

    // 检查默认槽位是否存在存档
    public static boolean hasSaveFile() {
        return hasSaveFile(1);
    }

    // 获取最大存档槽数量
    public static int getMaxSaveSlots() {
        return MAX_SAVE_SLOTS;
    }

    // 删除指定槽位的存档
    public static boolean deleteSaveFile(int slot) {
        if (slot < 1 || slot > MAX_SAVE_SLOTS) {
            System.err.println("无效的存档槽位: " + slot);
            return false;
        }

        String saveFilePath = String.format(SAVE_FILE_TEMPLATE, slot);
        File file = new File(saveFilePath);
        if (file.exists()) {
            return file.delete();
        }
        return true; // 文件不存在也认为删除成功
    }
}
