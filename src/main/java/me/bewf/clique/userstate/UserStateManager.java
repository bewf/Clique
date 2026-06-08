package me.bewf.clique.userstate;

public class UserStateManager {

    private static UserState state = UserState.NORMAL;

    public static UserState getState() {
        return state;
    }

    public static void setState(UserState newState) {
        state = newState;
    }

    public static boolean isOffline() {
        return state == UserState.OFFLINE;
    }

    public static boolean isDnd() {
        return state == UserState.DND;
    }

    public static boolean isNormal() {
        return state == UserState.NORMAL;
    }
}