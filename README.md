# PickBox
Pick box is a light weight alternative to Graphql for java. 

It allows for the user to craft a query and only retrieve fields they're interested in. This can allow the server to access less resources if they're uneeded for the request. It also allows many potential endpoints to be covered by one flexible endpoint.

Pick box differs from graphql in that there is no seperate schema, as the objects themselves provide the schema. This avoids errors with the schema becoming out of date with objects in the project. 

Pick box also doesn't control the endpoint itself, allowing you to have more control over what is sent back in the request and how the endpoint is exposed (type of serialization, camel vs snake case for json, or timeouts)

Graphql allows for the manipulation of objects, however Pick Graph doesn't support that functinality. If there's a need for that though, we might give it a shot.

Pick graph is Open Source! Feel free to submit a pr and we can get it added for you. You're also free to clone and use it in any way you would like. Commercial or otherwise.

Features
- request GET queries
- automatic schema
- custom parameters to resolver methods

Omitted Features from Graphql
- mutation mappings
- automatic endpoint creation
- graphiql interface



# Usage

```
<dependency>
  <groupId>pick.box</groupId>
  <artifactId>pick-box</artifactId>
  <version>1.1.0</version>
</dependency>
```

You might also need to add this repository tag if you haven't set up github packages (common)

```

  <repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/wbf22/PickBox</url>
    </repository>
  </repositories>

```
above the build section of your pom.

To use Pick Box you actually don't need an endpoint, but most commonly you'll use it in that context. When a request comes in, you can use Pick Box like this:
```
public class Endpoint {

    public MyObject methodCalledByEndpoint(MyObject request) {
        PickBox pickbox = new PickBox(
            List.of(new MyObjectResolver())
        );
        return PickBox.resolveRequest(request, null);
    }
```
The request object is the same type as the return object, however the request object has default values for the fields of that type. So if your object MyObject was like this:

```
public class MyObject {
    private String name;
    private Integer value;
}
```

Than a request would come in like this:
```
{
    "name" : "",
    "value" : 1
}
```

or if the client only wanted to retrieve the name they'd send
```
{
    "name" : ""
}
```
If you notice in our example above, we provide PickBox with a list of 'resolvers', in our case only one resolver 'MyObjectResolver'. This is the class you use to retrieve the object 'MyObject'. 

Here's how you'd define the resolver:
```
public class MyObjectResolver extends Resolver<MyObject, Object, Object> {

    @Override
    public MyObject resolve(Object requestParent, Object extraData) {
        MyObject obj = new MyObject(
            "James Sorenson",
            1
        );

        return obj;
    }
     
}
```
So PickBox will call this resolver when the request contains fields for this object.

The three types for the Resolver class (Resolver<ReturnType,ParentType,ExtraData>) are as follows:
- ReturnType: the result type of the resolver
- ParentType: if this object is nested in another object, the parent object will be provided as it has been fulfilled up to this point
- ExtraData: this a custom object you can provide when you call `PickBox.resolveRequest(request, myExtraData);`. This can include primary keys you can use to retrieve the object, or whatever other parameters you may need.


Normally you'd have multiple resolvers to be able to perform or skip retrieval logic based on the request. This comes in handy with large nested objects allowing the client to only request the parts they need. This functionality is the power of graphql and PickBox!




### Example in Spring Boot

Here's simple setup in a spring boot project:

Our example object returned in the request
```
public class MyObject {
    private String name;
    private Integer value;
}
```

Our configuration class that makes a PickBox bean
```
@Configuration
public class PickBoxConfig {
    

    @Bean
    public PickBox pickBox() {

        return new PickBox(
            List.of(new MyObjectResolver())
        );
    }
}
```
Our resolver class provided to PickBox to retrieve a 'MyObject' object. When we call 'resolveRequest' PickBox will then filter the result of this overriden method:
```
public class MyObjectResolver extends Resolver<MyObject, Object, Object> {

    @Override
    public MyObject resolve(Object requestParent, Object extraData) {
        MyObject obj = new MyObject(
            "James Sorenson",
            1
        );

        return obj;
    }
     
}
```

An finally here's our controller with endpoints:
```

@RestController
@RequestMapping("/my-endpoint")
@RequiredArgsConstructor
public class Endpoint {

    private final PickBox pickBox;


    @RequestMapping(method=RequestMethod.OPTIONS)
    public PickBoxRequest options() {
        return PickBox.getDefaultObject(MyObject.class);
    }


    
    @PostMapping
    public PickBoxRequest pickBoxEndpoint(
        @RequestBody MyObject request
    ) {
        return pickBox.resolveRequest(request, null);
    }
    
}

```
The first endpoint here is a an OPTIONS endpoint which is the way we communicate to the client what they can request from our endpoint. The `PickBox.getDefaultObject(MyObject.class)` call returns a default object that for our class would look like this:
```
{
    "name" : "",
    "value" : 1
}
```

The second endpoint is the actual method called by the client to retrieve objects. They pass in a request with requested fields being non null (or not missing). 

So there's a basic setup in spring boot.

## Complex Example
Here's a more complex example, that might help you see the benefits of using something like PickBox, and also help you see how to handle different cases. You probably don't need to read through this, but it can be helpful when you're struggling through something and need a good example.

Here's the object we'll be providing in our endpoint
```
public class GeneralRequest {

    private List<Order> orders;
    private List<Shipment> shipments;
}
```
This object allows us to expose mutiple objects with our endpoint. In this object we put the various objects clients can request. They'll get back something like this
```
{
    "orders" : [...],
    "shipments": [...]
}
```
depending on wether they request both 'orders' and 'shipments' or  not. 

Here's the sub objects of our example 'GeneralRequest'

Order:
```
public class Order {
    private String orderId;
    private Address destination;
    private ZonedDateTime orderDate;
    private ZonedDateTime expectedDeliveryDate;
    private String customerLast4;
    private String customerId;

    private List<OrderLine> orderLines;

}
```
```
public class Address {

    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
}
```
```
public class OrderLine {

    private String itemId;
    private int quantity;
    private BigDecimal msrp;
    private BigDecimal wholesalePrice;
    private BigDecimal customerPrice;

    private List<String> shipmentIds;
}
```

Shipment:
```
public class Shipment {

    private String shipmentId;

    private ZonedDateTime departureDate;
    private ZonedDateTime arrivalDate;

    private Address origin;
    private Address destination;

    private Capacity capacity;

    private boolean refrigerated;
    private boolean frozen;
    private boolean hazmat;
    private boolean fragile;

    private boolean boxesOrHeavy;
}
```
```
public class Capacity {

    private BigDecimal weight;
    private List<Volume> volumes;

}
```
```
public class Volume {
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;

}
```


And here's our configuration for making the PickBox bean
```
@Configuration
public class PickBoxConfig {
    

    @Bean
    public PickBox pickBox() {

        return new PickBox(
            List.of(
                new GeneralRequestResolver(),
                new OrderResolver(), 
                new ShipmentResolver()
            )
        );
    }
}
```

And our resolvers (I'm pretending we made getters and setters)
```

public class GeneralRequestResolver extends Resolver<GeneralRequest, Object, Object> {



    @Override
    public GeneralRequest resolve(Object arg0, Object arg1) {
        
        return new PickBoxRequest();
    }
    
}
```

```
public class OrderResolver extends Resolver<List<Order>, PickBoxRequest, Object> {


    @Override
    public List<Order> resolve(PickBoxRequest request, Object extraData) {

        // normally you'd call your database here or something
        Order order = new Order();
        order.setOrderId( IdUtil.genId("ord") );
        order.setAddress( null );

        OrderLine orderLine = new OrderLine();
        orderLine.setItemId( IdUtil.genId("itm") );
        orderLine.setCustomerPrice( BigDecimal.valueOf(47.54) );
        orderLine.setQuantity( 1 );
        orderLine.setShipmentIds(
            List.of(
                IdUtil.genId("ship")
            )
        );
        
        order.setOrderLines(
            List.of(
                orderLine
            )
        );

        return List.of(order);
    }
     
}
```
```

public class ShipmentResolver extends Resolver<List<Shipment>, PickBoxRequest, Object> {

    @Override
    public List<Shipment> resolve(PickBoxRequest parent, Object extraData) {
        // normally you'd call your database here or something
        Shipment shipment = new Shipment();
        shipment.setShipmentId( IdUtil.genId("ship") );
        shipment.setArrivalDate( ZonedDateTime.now().plusDays(10) );

        Capacity capacity = new Capacity();
        capacity.setWeight( BigDecimal.valueOf(152) );

        Volume volume = new Volume();
        volume.setDepth( BigDecimal.valueOf(2) );
        volume.setWidth( BigDecimal.valueOf(3) );
        volume.setHeight( BigDecimal.valueOf(3) );
        capacity.setVolumes( List.of(volume) );
        shipment.setCapacity( capacity );

        return List.of(shipment);
    }
    
}
```

And here's the endpoint:
```

@RestController
@RequestMapping("/logistics")
@RequiredArgsConstructor
public class Endpoint {

    private final PickBox pickBox;



    @RequestMapping(method=RequestMethod.OPTIONS)
    public PickBoxRequest options() {
        return PickBox.getDefaultObject(PickBoxRequest.class);
    }


    
    @PostMapping
    public PickBoxRequest pickBoxEndpoint(
        @RequestBody PickBoxRequest request
    ) {
        return pickBox.resolveRequest(request, null);
    }



    @PostMapping("/clean")
    public Map<String, Object> pickBoxEndpointClean(
        @RequestBody PickBoxRequest request
    ) {
        return PickerUtil.mapify(
            pickBox.resolveRequest(request, null)
        );
    }
    
}
```

Here's we have our OPTIONS endpoint to provide the client with a default object communicating possible fields to include in their request. 

We also have our standard endpoint 'pickBoxEndpoint' providing PickBox functionality for the client. 

The last endpoint 'pickBoxEndpointClean' is basically the same thing as 'pickBoxEndpoint' but wipes out null fields from the returned object from PickBox using the 'mapify' function. This bascially returns the exact object the client sent in, but with their requested fields filled in. In our other endpoint, all the other fields they didn't request will show up as null. If you don't care about that, you don't need to make an endpoint like this. But if you like that feature, it's there!