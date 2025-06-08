package cool.drinkup.drinkup.common.log.event;

public class UserEvent {
    public final static String USER = "USER";

    public static class BehaviorEvent {
        public final static String LOGIN = "LOGIN";
        public final static String LOGOUT = "LOGOUT";
        public final static String REGISTER = "REGISTER";
        public final static String PROFILE_GET = "PROFILE_GET";
        public final static String PROFILE_UPDATE = "PROFILE_UPDATE";
    }
}
