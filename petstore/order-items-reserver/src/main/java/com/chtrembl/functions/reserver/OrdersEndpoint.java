package com.chtrembl.functions.reserver;

import com.chtrembl.functions.reserver.dto.OrderRequest;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BlobOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class OrdersEndpoint {
    @FunctionName("AddOrUpdateUserOrder")
    @BlobOutput(name = "payload", path = "orders/{sessionId}.json", connection = "AZURE_STORAGE_CONNECTION_STRING")
    public void saveOrder(
            @HttpTrigger(name = "req", route = "orders", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<OrderRequest>> request,
            @BlobOutput(name = "payload", path = "orders/{sessionId}.json", connection = "AZURE_STORAGE_CONNECTION_STRING") OutputBinding<String> blobOrder,
            final ExecutionContext context) {
        if (request.getBody().isPresent()) {
            var body = request.getBody().get();
            context.getLogger().info(body.toString());
            blobOrder.setValue(body.payload());
        }
    }
}
