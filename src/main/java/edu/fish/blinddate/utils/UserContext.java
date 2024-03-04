package edu.fish.blinddate.utils;

public abstract class UserContext {
    private static ThreadLocal<Integer> userThreadLocal = new ThreadLocal<>();

    public static Integer getUserId() {
        return userThreadLocal.get();
    }

    public static void setUserId(Integer userId) {
        userThreadLocal.set(userId);
    }

    public static void removeUserId() {
        userThreadLocal.remove();
    }
}
