// NotificationService.java
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public void processNotification(NotificationRequest request) {
        // Validate request
        if (request.getUserId() == null || request.getMessage() == null) {
            throw new IllegalArgumentException("Invalid notification request");
        }
        
        // Save to database
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .message(request.getMessage())
                .type(request.getType())
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .build();
        
        notificationRepository.save(notification);
        
        // Send to queue
        rabbitTemplate.convertAndSend(
                "notification-exchange",
                "notification.routingkey",
                notification
        );
    }
    
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
