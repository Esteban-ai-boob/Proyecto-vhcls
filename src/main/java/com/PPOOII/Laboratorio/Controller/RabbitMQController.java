package com.PPOOII.Laboratorio.Controller;

import com.PPOOII.Laboratorio.Component.RabbitMQConsumer;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api/v1/rabbitmq")
@CrossOrigin(origins = "*")
public class RabbitMQController {

    private final RabbitAdmin rabbitAdmin;
    private final RabbitMQConsumer rabbitMQConsumer;

    public RabbitMQController(RabbitAdmin rabbitAdmin, RabbitMQConsumer rabbitMQConsumer) {
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitMQConsumer = rabbitMQConsumer;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Información de la cola
        Properties queueProperties = rabbitAdmin.getQueueProperties("notificaciones_queue");
        int mensajesEnCola = 0;
        int consumidoresActivos = 0;
        
        if (queueProperties != null) {
            // Se debe castear a entero si es que la API lo permite, o parsearlo desde String
            Object msgCountObj = queueProperties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
            if (msgCountObj != null) {
                mensajesEnCola = Integer.parseInt(msgCountObj.toString());
            }
            
            Object consumerCountObj = queueProperties.get(RabbitAdmin.QUEUE_CONSUMER_COUNT);
            if (consumerCountObj != null) {
                consumidoresActivos = Integer.parseInt(consumerCountObj.toString());
            }
        }
        
        status.put("cola", "notificaciones_queue");
        status.put("mensajesEnCola", mensajesEnCola);
        status.put("consumidoresActivos", consumidoresActivos);
        
        // Información del consumidor
        status.put("mensajesProcesados", rabbitMQConsumer.getMensajesProcesados());
        status.put("ultimosMensajes", rabbitMQConsumer.getUltimosMensajes());
        
        return status;
    }
}
