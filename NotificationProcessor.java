// NotificationProcessor.java
@Component
@RequiredArgsConstructor
public class NotificationProcessor {
    
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SmsService smsService;
    private final InAppService inAppService;
    
    @RabbitListener(queues = "notification-queue")
    public void handleNotification(Notification notification) {
        try {
            switch (notification.getType()) {
                case EMAIL:
                    emailService.sendEmail(notification);
                    break;
                case SMS:
                    smsService.sendSms(notification);
                    break;
                case IN_APP:
                    inAppService.sendInApp(notification);
                    break;
            }
            notification.setStatus(NotificationStatus.DELIVERED);
        } catch (Exception e) {
            handleFailedNotification(notification, e);
        } finally {
            notificationService.updateNotification(notification);
        }
    }
    
    private void handleFailedNotification(Notification notification, Exception e) {
        if (notification.getRetryCount() >= 3) {
            notification.setStatus(NotificationStatus.FAILED);
        } else {
            notification.setStatus(NotificationStatus.RETRYING);
            notification.setRetryCount(notification.getRetryCount() + 1);
            resendNotification(notification);
        }
    }
    
    private void resendNotification(Notification notification) {
        rabbitTemplate.convertAndSend(
                "notification-exchange",
                "notification.routingkey", 
                notification,
                message -> {
                    message.getMessageProperties().setDelay(
                        calculateBackoff(notification.getRetryCount())
                    );
                    return message;
                });
    }
    
    private int calculateBackoff(int retryCount) {
        return (int) (Math.pow(2, retryCount) * 1000);
    }
}
