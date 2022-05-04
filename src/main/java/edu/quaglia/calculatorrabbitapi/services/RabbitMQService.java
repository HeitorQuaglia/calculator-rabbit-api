package edu.quaglia.calculatorrabbitapi.services;

import models.Calculation;
import models.CalculationResult;
import models.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

@Service
public class RabbitMQService {
    public static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQService.class);

    @Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;
    @Autowired
    private DirectExchange directExchange;
    private static final String ROUTING_KEY = "expression";

    public CalculationResult handleCalculation(Calculation calculation) {
        synchronized (Control.class) {
            LOGGER.info("Sending expression {} {} {} to queue", calculation.getA(), calculation.getOperation(), calculation.getB());

            Control.mdId = MDC.get("UNIQUE_ID");

            CalculationResult result = null;

            AsyncRabbitTemplate.RabbitConverterFuture<CalculationResult> rabbitConverterFuture;

            try {
                rabbitConverterFuture = asyncRabbitTemplate.convertSendAndReceiveAsType(directExchange.getName(), ROUTING_KEY, calculation, new ParameterizedTypeReference<>() {
                });

                if (MDC.getCopyOfContextMap() == null) MDC.put("UNIQUE_ID", Control.mdId);

                rabbitConverterFuture.addCallback(new ListenableFutureCallback<>() {

                    @Override
                    public void onFailure(Throwable ex) {
                        LOGGER.error("Cannot get response for expression {}", calculation, ex);
                    }

                    @Override
                    public void onSuccess(CalculationResult registrationDto) {
                        LOGGER.info("Expression received {}", calculation);
                    }
                });

                result = rabbitConverterFuture.get();

            } catch (InterruptedException | ExecutionException exception) {
                LOGGER.error("Error with sending request to queue, {}", exception.getMessage());
            }

            return result;
        }
    }
}
