# Load-balancer
It is a load balancer is a component that, once invoked, it distributes incoming requests to a list of registered providers and return the value obtained from one of the registered providers to the original caller.
 
## Run and test application
Clean and Install dependencies and build befoore running the application
`mvn clean`
`mvn install`

Run the application. It Creates a registry and a load balancer, and perform some operations then prints out results

Run JUnit tests.
`mvn test`
