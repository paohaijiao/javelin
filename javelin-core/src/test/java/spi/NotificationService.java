package spi;

public interface NotificationService {
    void send(String message);

    boolean supports(String type);
}
