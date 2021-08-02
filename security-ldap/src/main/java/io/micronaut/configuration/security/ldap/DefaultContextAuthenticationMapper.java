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
package io.micronaut.configuration.security.ldap;

import io.micronaut.core.convert.value.ConvertibleValues;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.Authentication;

import io.micronaut.security.token.config.TokenConfiguration;
import jakarta.inject.Singleton;
import java.util.Set;

/**
 * The default implementation to create an {@link AuthenticationResponse} from a successful
 * ldap authentication result.
 *
 * @author James Kleeh
 * @since 1.0
 */
@Singleton
public class DefaultContextAuthenticationMapper implements ContextAuthenticationMapper {

    private final TokenConfiguration tokenConfiguration;

    public DefaultContextAuthenticationMapper(TokenConfiguration tokenConfiguration) {
        this.tokenConfiguration = tokenConfiguration;
    }

    @Override
    public AuthenticationResponse map(ConvertibleValues<Object> attributes, String username, Set<String> groups) {
        return AuthenticationResponse.build(username, groups, tokenConfiguration);
    }
}
