package cool.drinkup.drinkup.external.sms;

public interface SmsSender {
    void sendSms(String phoneNumber, String code);
    boolean verifySms(String phoneNumber, String code);
}
