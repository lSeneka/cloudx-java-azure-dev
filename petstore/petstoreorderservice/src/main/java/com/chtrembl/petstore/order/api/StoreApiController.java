package com.chtrembl.petstore.order.api;

import com.chtrembl.petstore.order.control.CosmosDbService;
import com.chtrembl.petstore.order.control.mapper.OrderMapper;
import com.chtrembl.petstore.order.control.mapper.ProductMapper;
import com.chtrembl.petstore.order.model.ContainerEnvironment;
import com.chtrembl.petstore.order.model.Order;
import com.chtrembl.petstore.order.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-12-21T10:17:19.885-05:00")

@Controller
@RequestMapping("${openapi.swaggerPetstore.base-path:/petstoreorderservice/v2}")
public class StoreApiController implements StoreApi {

    static final Logger log = LoggerFactory.getLogger(StoreApiController.class);

    private final ObjectMapper objectMapper;

    private final NativeWebRequest request;

    @Autowired
    @Qualifier(value = "cacheManager")
    private CacheManager cacheManager;

    @Autowired
    private ContainerEnvironment containerEnvironment;

    @Autowired
    private StoreApiCache storeApiCache;

    @Autowired
    CosmosDbService cosmosDbService;

    @Override
    public StoreApiCache getBeanToBeAutowired() {
        return storeApiCache;
    }

    @org.springframework.beans.factory.annotation.Autowired
    public StoreApiController(ObjectMapper objectMapper, NativeWebRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    // should really be in an interceptor
    public void conigureThreadForLogging() {
        try {
            this.containerEnvironment.setContainerHostName(
                    InetAddress.getLocalHost().getHostAddress() + "/" + InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            log.info("PetStoreOrderService getRequest() error: " + e.getMessage());
        }
        MDC.put("containerHostName", this.containerEnvironment.getContainerHostName());
        MDC.put("session_Id", request.getHeader("session-id"));
    }

    @RequestMapping(value = "store/info", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<String> info() {
        conigureThreadForLogging();

        // password used for cred scan demo
        String password = "foobar";
        log.info("PetStoreOrderService incoming GET request to petstoreorderservice/v2/info");

        int ordersCacheSize = 0;
        try {
            org.springframework.cache.concurrent.ConcurrentMapCache mapCache = ((org.springframework.cache.concurrent.ConcurrentMapCache) this.cacheManager
                    .getCache("orders"));
            ordersCacheSize = mapCache.getNativeCache().size();
        } catch (Exception e) {
            log.warn(String.format("could not get the orders cache size :%s", e.getMessage()));
        }

        // giving consumers JSON regardless here, info wasn't part of the swagger
        // contract :)
        ApiUtil.setResponse(request, "application/json",
                "{ \"service\" : \"order service\", \"version\" : \"" + containerEnvironment.getAppVersion()
                        + "\", \"container\" : \"" + containerEnvironment.getContainerHostName()
                        + "\", \"ordersCacheSize\" : \"" + ordersCacheSize + "\", \"author\" : \"" + containerEnvironment.getAuthor()
                        + "\" }");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Order> placeOrder(
            @ApiParam(value = "order placed for purchasing the product", required = true) @Valid @RequestBody Order body) {
        conigureThreadForLogging();

        String acceptType = request.getHeader("Accept");
        String contentType = request.getHeader("Content-Type");
        if (acceptType != null && contentType != null && acceptType.contains("application/json")
                && contentType.contains("application/json")) {

            log.info(String.format(
                    "PetStoreOrderService incoming POST request to petstoreorderservice/v2/order/placeOder for order id:%s",
                    body.getId()));

            var order = cosmosDbService.findById(body.getId());

            order.setId(body.getId());
            order.setEmail(body.getEmail());
            order.setComplete(body.isComplete());
            order.setStatus(body.getStatus() != null ? body.getStatus().name() : null);
            order.setShipDate(body.getShipDate());
            order.setProducts(new ArrayList<>());

            var products = accumulateProducts(body, order);
            order.setProducts(products);

            try {
                this.cosmosDbService.save(order);
                var orderJSON = objectMapper.writeValueAsString(OrderMapper.INSTANCE.toOrderModel(order));
                ApiUtil.setResponse(request, "application/json", orderJSON);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Order>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Order>(HttpStatus.NOT_IMPLEMENTED);

    }

    private static List<com.chtrembl.petstore.order.entity.Order.Product> accumulateProducts(Order body, com.chtrembl.petstore.order.entity.Order order) {
        var incomeProducts = body.getProducts().stream()
                .map(ProductMapper.INSTANCE::toProductEntity)
                .collect(Collectors.toMap(
                        com.chtrembl.petstore.order.entity.Order.Product::getId,
                        Function.identity()
                ));

        var products = order.getProducts().stream()
                .filter(product -> incomeProducts.containsKey(product.getId())
                        && incomeProducts.get(product.getId()).getQuantity() != 0
                )
                .filter(product -> incomeProducts.get(product.getId()).getQuantity() + product.getQuantity() >= 1)
                .collect(Collectors.toList());

        products.forEach(
                product -> product.setQuantity(
                        product.getQuantity() + incomeProducts.get(product.getId()).getQuantity()
                )
        );

        incomeProducts.values().stream()
                .filter(
                        incomeProduct -> products.stream().noneMatch(product -> incomeProduct.getId().equals(product.getId())
                                && incomeProduct.getQuantity() > 0)
                )
                .forEach(products::add);
        return products;
    }

    @Override
    public ResponseEntity<Order> getOrderById(
            @ApiParam(value = "ID of the order that needs to be deleted", required = true) @PathVariable("orderId") String orderId) {
        conigureThreadForLogging();

        String acceptType = request.getHeader("Accept");
        String contentType = request.getHeader("Content-Type");
        if (acceptType != null && acceptType.contains("application/json")) {

            log.info(String.format(
                    "PetStoreOrderService incoming GET request to petstoreorderservice/v2/order/getOrderById for order id:%s",
                    orderId));

            List<Product> products = this.storeApiCache.getProducts();

            var order = this.cosmosDbService.findById(orderId);

            if (products != null) {
                // cross reference order data (order only has product id and qty) with product
                // data....
                try {
                    if (order.getProducts() != null) {
                        for (var p : order.getProducts()) {
                            Product peekedProduct = getProduct(products, p.getId());
                            p.setName(peekedProduct.getName());
                            p.setPhotoURL((peekedProduct.getPhotoURL()));
                        }
                    }
                } catch (Exception e) {
                    log.error(String.format(
                            "PetStoreOrderService incoming GET request to petstoreorderservice/v2/order/getOrderById for order id:%s failed: %s",
                            orderId, e.getMessage()));
                }
            }

            try {
                ApiUtil.setResponse(request, "application/json", objectMapper.writeValueAsString(order));
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Order>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Order>(HttpStatus.NOT_IMPLEMENTED);
    }

    private Product getProduct(List<Product> products, Long id) {
        conigureThreadForLogging();

        return products.stream().filter(product -> id.equals(product.getId())).findAny().orElse(null);
    }

    @Override
    public ResponseEntity<Void> deleteOrder(
            @Min(1L) @ApiParam(value = "ID of the order that needs to be deleted", required = true) @PathVariable("orderId") String orderId) {
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getInventory() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Map<String, Integer>>(objectMapper.readValue("{  \"key\" : 0}", Map.class),
                        HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Map<String, Integer>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Map<String, Integer>>(HttpStatus.NOT_IMPLEMENTED);
    }
}
