package org.newtco.obserra.backend.collector;

import java.io.IOException;
import java.net.URI;

import jakarta.annotation.Nonnull;

import org.newtco.obserra.backend.model.ObService;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class CollectorUtils {
    private CollectorUtils() {
        // Utility class should not be instantiated
    }

   public static CollectionException fromConnectionError(ObService service, Collector<?> collector, Exception e) {
       var message = "Connection failed while collecting %s data for service %s: %s".formatted(
               collector.type(),
               service.getName(),
               e.getMessage());
               
       // Connection errors are typically retriable
       return new CollectionException(message, e, true);
   }

    /**
     * Standard error handler for a RestClient that converts HTTP errors to CollectionExceptions.
     *
     * @param service   The service being collected
     * @param collector The collector that is collecting data
     *
     * @return A ResponseErrorHandler that converts HTTP errors to CollectionExceptions.
     */
    public static ResponseErrorHandler collectorHttpErrorHandler(ObService service, Collector<?> collector) {
        return new CollectorErrorHandler(service.getName(), collector.name());
    }

    public record CollectorErrorHandler(String service, String type) implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().isError();
        }

        @Override
        public void handleError(@Nonnull URI url, @Nonnull HttpMethod method, ClientHttpResponse response) throws IOException {
            var status = response.getStatusCode();
            String responseBody = null;
            
            // Try to read the response body if available
            try (var bodyStream = response.getBody()) {
                byte[] bytes = bodyStream.readAllBytes();
                if (bytes.length > 0) {
                    responseBody = new String(bytes);
                }
            } catch (Exception e) {
                // Ignore errors reading the body
            }
            
            var message = "Collection of %s data for service %s failed with HTTP %d: %s".formatted(
                    type,
                    service,
                    status.value(),
                    response.getStatusText());
                    
            if (responseBody != null) {
                message += " - Response: " + responseBody;
            }

            throw new CollectionException(message, null, isRetriable(status.value()));
        }

        private boolean isRetriable(int status) {
            // 500+, Request Timeout, or Rate Limit Exceeded are retriable
            return status >= 500 || status == 408 || status == 429;
        }
    }

}