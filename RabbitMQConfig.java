// RabbitMQConfig.java
@Configuration
public class RabbitMQConfig {
    
    public static final String QUEUE = "notification-queue";
    public static final String EXCHANGE = "notification-exchange";
    public static final String ROUTING_KEY = "notification.routingkey";
    
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx-exchange")
                .withArgument("x-dead-letter-routing-key", "dlx-routingkey")
                .build();
    }
    
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }
    
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
    
    // DLQ Configuration
    @Bean
    public Queue dlq() { return new Queue("dlq"); }
    
    @Bean
    public DirectExchange dlx() { return new DirectExchange("dlx-exchange"); }
    
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq()).to(dlx()).with("dlx-routingkey");
    }
}
