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
package io.micronaut.security.authentication;

import io.micronaut.context.annotation.Primary;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;


/**
 * Handles the server response when an {@link AuthenticationException} is thrown.
 *
 * @author James Kleeh
 * @since 1.0
 */
@Singleton
@Primary
@Produces
public class AuthenticationExceptionHandler implements ExceptionHandler<AuthenticationException, MutableHttpResponse<?>> {
    protected final ApplicationEventPublisher<?> eventPublisher;

    /**
     * @param eventPublisher The event publisher
     * @deprecated This will be removed in the next major version, so that this class uses the ErrorProcessor API
     */
    @Deprecated
    public AuthenticationExceptionHandler(ApplicationEventPublisher<?> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, AuthenticationException exception) {
        JsonError error = new JsonError(exception.getMessage());
        error.link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.unauthorized().body(error);
    }
}

