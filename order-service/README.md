# Order Service

This service is responsible for publishing `OrderEvent` to `Kafka` and consuming `StockCheckEvent`, `ValidationEvent`, `BillingEvent` from it.