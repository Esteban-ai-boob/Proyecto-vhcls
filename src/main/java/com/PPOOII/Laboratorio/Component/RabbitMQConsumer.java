package com.PPOOII.Laboratorio.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

@Component
public class RabbitMQConsumer {
    private static final Logger Logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    private final AtomicInteger mensajesProcesados = new AtomicInteger(0);
    private final ConcurrentLinkedDeque<String> ultimosMensajes = new ConcurrentLinkedDeque<>();
    private static final int MAX_MENSAJES = 5;

    @RabbitListener(queues = "notificaciones_queue")
    public void recibirMensaje(String mensaje) {
        Logger.info("🐇 [RABBITMQ] Mensaje recibido en la cola: {}", mensaje);
        mensajesProcesados.incrementAndGet();
        
        ultimosMensajes.addFirst(mensaje);
        if (ultimosMensajes.size() > MAX_MENSAJES) {
            ultimosMensajes.removeLast();
        }
    }

    public int getMensajesProcesados() {
        return mensajesProcesados.get();
    }

    public List<String> getUltimosMensajes() {
        return new ArrayList<>(ultimosMensajes);
    }
}
