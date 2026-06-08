package me.bewf.clique.userstate;

public final class UserStateManager {

    public static UserState getState() {
        return UserStateToggle.get();
    }

    public static void setState(UserState state) {
        UserStateToggle.set(state);
    }

    public static boolean isOffline() {
        return UserStateToggle.is(UserState.OFFLINE);
    }

    public static boolean isDnd() {
        return UserStateToggle.is(UserState.DND);
    }

    public static boolean isNormal() {
        return UserStateToggle.is(UserState.NORMAL);
    }

    public static void toggleOffline() {
        UserStateToggle.toggleOffline();
    }

    public static void toggleDnd() {
        UserStateToggle.toggleDnd();
    }
}