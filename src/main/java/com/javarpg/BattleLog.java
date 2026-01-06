package main.java.com.javarpg;

public class BattleLog {
    private String message;

    public BattleLog(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}