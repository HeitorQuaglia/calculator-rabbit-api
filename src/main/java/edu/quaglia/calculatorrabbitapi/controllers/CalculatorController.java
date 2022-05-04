package edu.quaglia.calculatorrabbitapi.controllers;


import edu.quaglia.calculatorrabbitapi.services.RabbitMQService;
import models.Calculation;
import models.CalculationResult;
import models.enums.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@Validated
@RequestMapping("calculator")
public class CalculatorController {
    private final Logger LOGGER = LoggerFactory.getLogger(CalculatorController.class);
    private final RabbitMQService rabbitMQClient;

    public CalculatorController(RabbitMQService rabbitMQClient) {
        this.rabbitMQClient = rabbitMQClient;
    }

    @GetMapping("/sum")
    public ResponseEntity<?> add(@RequestParam(name = "a") BigDecimal a, @RequestParam(name = "b") BigDecimal b) {
        LOGGER.info("Received a {} and b {} for addition", a, b);

        CalculationResult resultDto = rabbitMQClient.handleCalculation(new Calculation(a, b, Operation.ADD));

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    @GetMapping("/sub")
    public ResponseEntity<?> subtract(@RequestParam(name = "a") BigDecimal a, @RequestParam(name = "b") BigDecimal b) {
        LOGGER.info("Received a {} and b {} for subtract", a, b);

        CalculationResult resultDto = rabbitMQClient.handleCalculation(new Calculation(a, b, Operation.SUBTRACT));

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    @GetMapping("/mpy")
    public ResponseEntity<?> multiply(@RequestParam(name = "a") BigDecimal a, @RequestParam(name = "b") BigDecimal b) {
        LOGGER.info("Received a {} and b {} for multiply", a, b);

        CalculationResult resultDto = rabbitMQClient.handleCalculation(new Calculation(a, b, Operation.DIVIDE));

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    @GetMapping("/div")
    public ResponseEntity<?> divide(@RequestParam(name = "a") BigDecimal a, @RequestParam(name = "b") BigDecimal b) {
        LOGGER.info("Received a {} and b {} for divide", a, b);

        CalculationResult resultDto = rabbitMQClient.handleCalculation(new Calculation(a, b, Operation.MULTIPLY));

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }
}
