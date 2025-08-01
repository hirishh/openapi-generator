/*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.15.0-SNAPSHOT).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package org.openapitools.mockserver.api;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.http.Fault;

public class DefaultApiMockServer {

    public static MappingBuilder stubFooGet0(String response) {
        MappingBuilder stub = get(urlPathEqualTo("/foo"))
            .withHeader("Accept", havingExactly("application/json"))
            .willReturn(aResponse()
                .withStatus(0)
                .withHeader("Content-Type", "application/json")
                .withBody(response)
            );


        return stub;
    }

    public static MappingBuilder stubFooGetFault(Fault fault) {
        MappingBuilder stub = get(urlPathEqualTo("/foo"))
            .withHeader("Accept", havingExactly("application/json"))
            .willReturn(aResponse()
                .withFault(fault)
            );


        return stub;
    }

    public static String fooGet200ResponseSample1() {
        return "{ \"string\" : { \"bar\" : \"bar\" } }";
    }



}
