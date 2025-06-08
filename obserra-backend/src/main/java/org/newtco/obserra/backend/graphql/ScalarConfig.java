package org.newtco.obserra.backend.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;

/**
 * Configuration for GraphQL scalar types.
 * This class registers custom scalar types for use in the GraphQL schema.
 */
@DgsComponent
public class ScalarConfig {

    /**
     * Register the Object scalar for the Map type.
     * The Object scalar is provided by the graphql-dgs-extended-scalars library
     * and can be used to represent complex objects like maps.
     *
     * @param builder the RuntimeWiring.Builder to register the scalar with
     * @return the updated RuntimeWiring.Builder
     */
    @DgsRuntimeWiring
    public RuntimeWiring.Builder addScalars(RuntimeWiring.Builder builder) {
        return builder.scalar(ExtendedScalars.Object);
    }
}
