// src/lib/apollo-client.ts
import { ApolloClient, InMemoryCache } from "@apollo/client";

const client = new ApolloClient({
    uri: "http://localhost:8080/graphql", // The URL of your Spring Boot GraphQL endpoint
    cache: new InMemoryCache(),
});

export default client;