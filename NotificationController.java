// NotificationController.java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/notifications")
    public ResponseEntity<String> sendNotification(
            @RequestBody NotificationRequest request) {
        notificationService.processNotification(request);
        return ResponseEntity.accepted().body("Notification queued");
    }
    
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
}
