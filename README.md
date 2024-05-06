# PickBox
Pick box is a light weight alternative to Graphql for java. 

It allows for the user to craft a query and only retrieve fields they're interested in. This can allow the server to access less resources if they're uneeded for the request. It also allows many potential endpoints to be covered by one flexible endpoint.

Pick box differs from graphql in that there is no seperate schema, as the objects themselves provide the schema. This avoids errors with the schema becoming out of date with objects in the project. 

Pick box also doesn't control the endpoint itself, allowing you to have more control over what is sent back in the request and how the endpoint is exposed (type of serialization, camel vs snake case for json, or timeouts)

Graphql allows for the manipulation of objects, however Pick Graph doesn't support that functinality. If there's a need for that though, we might give it a shot.

Pick graph is Open Source! Feel free to submit a pr and we can get it added for you. You're also free to clone and use it in any way you would like. Commercial or otherwise.



