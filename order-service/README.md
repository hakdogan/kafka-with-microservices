# Order Service

This service is responsible for publishing `OrderEvent` to `Kafka` and consuming `StockCheckEvent`, `ValidationEvent`, `BillingEvent` from it.

You can run this service by following command

```shell script
mvn liberty:run
```

or

```shell script
sh run.sh
```