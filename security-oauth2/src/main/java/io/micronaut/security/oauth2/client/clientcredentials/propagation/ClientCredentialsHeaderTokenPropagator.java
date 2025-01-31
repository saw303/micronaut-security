/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.security.oauth2.client.clientcredentials.propagation;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.security.oauth2.client.clientcredentials.ClientCredentialsEnabled;
import io.micronaut.security.oauth2.configuration.OauthClientConfiguration;
import java.util.Optional;

/**
 * Propagates a token obtained via client credentials based off of a header.
 *
 * @author Sergio del Amo
 * @since 2.2.0
 */
@Internal
@EachBean(OauthClientConfiguration.class)
@Requires(condition = ClientCredentialsEnabled.class)
@Requires(condition = ClientCredentialsHeaderPropagatorEnabled.class)
public class ClientCredentialsHeaderTokenPropagator implements ClientCredentialsTokenPropagator {

    public static final String SPACE = " ";
    protected final ClientCredentialsHeaderTokenPropagatorConfiguration configuration;

    /**
     * @param configuration The token propagator configuration
     */
    public ClientCredentialsHeaderTokenPropagator(ClientCredentialsHeaderTokenPropagatorConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Writes the token to the request.
     * @param request The {@link MutableHttpRequest} instance
     * @param token A token ( e.g. JWT token, basic auth token...)
     */
    @Override
    public void writeToken(@NonNull MutableHttpRequest<?> request, @NonNull String token) {
        request.header(configuration.getHeaderName(), headerValue(token));
    }

    /**
     * Search for a JWT token in a HTTP request.
     * @param request The request to look for the token in
     * @return if the JWT token is found it is returned, empty if not
     */
    @Override
    public Optional<String> findToken(@NonNull HttpRequest<?> request) {
        HttpHeaders headers = request.getHeaders();
        Optional<String> authorizationHeader = headers.findFirst(configuration.getHeaderName());
        return authorizationHeader.flatMap(this::extractTokenFromAuthorization);
    }

    /**
     * @param token the token being written
     * @return the value which will be written to an HTTP Header
     */
    protected String headerValue(String token) {
        StringBuilder sb = prefix();
        sb.append(token);
        return sb.toString();
    }

    /**
     * @param authorization Authorization header value
     * @return If prefix is 'Bearer' for 'Bearer XXX' it returns 'XXX'
     */
    protected Optional<String> extractTokenFromAuthorization(String authorization) {
        StringBuilder sb = prefix();
        String str = sb.toString();
        if (authorization.startsWith(str)) {
            return Optional.of(authorization.substring(str.length()));
        }
        return Optional.empty();
    }

    @NonNull
    private StringBuilder prefix() {
        StringBuilder sb = new StringBuilder();
        final String prefix = configuration.getPrefix();
        if (prefix != null && !prefix.isEmpty()) {
            sb.append(prefix);
            if (!prefix.endsWith(SPACE)) {
                sb.append(SPACE);
            }
        }
        return sb;
    }

    /**
     *
     * @return The HttpHeaderTokenPropagator Configuration
     */
    public ClientCredentialsHeaderTokenPropagatorConfiguration getConfiguration() {
        return configuration;
    }
}
