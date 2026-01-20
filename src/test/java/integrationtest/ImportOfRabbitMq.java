package integrationtest;

import dev.runnerz.RunnerzApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.project.event_managment.config.RabbitMQConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = RunnerzApplication.class)
@Import(RabbitMQConfig.class)
public class ImportOfRabbitMq {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testRabbitMQConfig() {
        assertEquals(this.isRabbitMqImportedFromEventService(), true);

        assertNotNull(rabbitTemplate, "RabbitTemplate should not be null");

        // Assert configuration constants from RabbitMQConfig
        assertEquals("user.event.exchange", RabbitMQConfig.EVENT_EXCHANGE,
                "EVENT_EXCHANGE should be 'user.event.exchange'");
        assertEquals("notification.queue", RabbitMQConfig.NOTIFICATION_QUEUE,
                "NOTIFICATION_QUEUE should be 'notification.queue'");
    }

    private boolean isRabbitMqImportedFromEventService() {
        return rabbitTemplate != null;
    }
}
