package com.wayfair.aggregator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wayfair.aggregator.component.ProductComponent;
import com.wayfair.aggregator.datastorage.AggregateStorage;
import com.wayfair.aggregator.model.CartProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequestMapping("/aggregator")
public class AggregatorController {

    private static final String SCHEDULED_TASKS = "scheduledTasks";

    @Resource
    private ProductComponent productComponent;

    @Autowired
    private ScheduledAnnotationBeanPostProcessor postProcessor;

    @GetMapping(value = "/stop")
    public ResponseEntity<String> stopSchedule() {
        postProcessor.postProcessBeforeDestruction(productComponent, SCHEDULED_TASKS);
        return new ResponseEntity<String>("Product Scheduler Stopped", HttpStatus.OK);
    }

    @GetMapping(value = "/start/{cartId}/{productId}")
    public ResponseEntity<CartProductModel> startSchedule(@NotNull @PathVariable String  cartId,@NotNull @PathVariable String productId) {
        CartProductModel cartProductModel = AggregateStorage.getModelInstance();
        AggregateStorage storage = AggregateStorage.getInstance();
        cartProductModel.setCartId(cartId);
        cartProductModel.setProductId(productId);
        storage.storeCartProduct(cartProductModel);
        postProcessor.postProcessAfterInitialization(productComponent, SCHEDULED_TASKS);
        return new ResponseEntity<CartProductModel>(storage.get(), HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Set> listSchedules() throws JsonProcessingException {
        Set<ScheduledTask> setTasks = postProcessor.getScheduledTasks();
        if (!setTasks.isEmpty()) {
            return new ResponseEntity<Set>(setTasks, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }


    }
