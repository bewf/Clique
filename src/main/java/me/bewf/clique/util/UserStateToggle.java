package me.bewf.clique.userstate;

public final class UserStateToggle {

    // current active state
    private static UserState state = UserState.NORMAL;

    public static void set(UserState newState) {
        state = newState;
    }

    public static UserState get() {
        return state;
    }

    public static boolean is(UserState check) {
        return state == check;
    }

    public static void toggleOffline() {
        state = (state == UserState.OFFLINE) ? UserState.NORMAL : UserState.OFFLINE;
    }

    public static void toggleDnd() {
        state = (state == UserState.DND) ? UserState.NORMAL : UserState.DND;
    }
}