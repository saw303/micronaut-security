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
package io.micronaut.security.oauth2.endpoint.token.request.password;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.Internal;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.oauth2.configuration.OauthClientConfiguration;
import io.micronaut.security.oauth2.endpoint.token.request.TokenEndpointClient;
import io.micronaut.security.oauth2.endpoint.token.response.DefaultOpenIdAuthenticationMapper;
import io.micronaut.security.oauth2.endpoint.token.response.OauthAuthenticationMapper;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdAuthenticationMapper;
import io.micronaut.security.oauth2.endpoint.token.response.validation.OpenIdTokenResponseValidator;
import io.micronaut.security.oauth2.grants.GrantType;
import io.micronaut.security.oauth2.client.OpenIdProviderMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Factory creating {@link AuthenticationProvider} beans that delegate
 * to the password grant flow of an OAuth 2.0 or OpenID provider.
 *
 * @author James Kleeh
 * @since 1.2.0
 */
@Factory
@Internal
class PasswordGrantFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordGrantFactory.class);

    /**
     * For authentication providers delegating to OAuth 2.0 providers,
     * the {@param openIdAuthenticationMapper}, {@param openIdProviderMetadata},
     * and {@param tokenResponseValidator} parameters are expected
     * to be null.
     *
     * For authentication providers delegating to OpenID providers,
     * the {@param openIdAuthenticationMapper} parameter is expected to be null.
     *
     * @param clientConfiguration The client configuration
     * @param oauthAuthenticationMapper The OAuth 2.0 user details mapper
     * @param openIdAuthenticationMapper The client specific OpenID authentication mapper
     * @param openIdProviderMetadata The OpenID provider metadata
     * @param tokenEndpointClient The token endpoint client
     * @param defaultOpenIdAuthenticationMapper The default OpenID user details mapper
     * @param tokenResponseValidator The OpenID token response validator
     * @return The authentication provider
     */
    @EachBean(OauthClientConfiguration.class)
    AuthenticationProvider passwordGrantProvider(
            @Parameter OauthClientConfiguration clientConfiguration,
            @Parameter @Nullable OauthAuthenticationMapper oauthAuthenticationMapper,
            @Parameter @Nullable OpenIdAuthenticationMapper openIdAuthenticationMapper,
            @Parameter @Nullable OpenIdProviderMetadata openIdProviderMetadata,
            TokenEndpointClient tokenEndpointClient,
            @Nullable DefaultOpenIdAuthenticationMapper defaultOpenIdAuthenticationMapper,
            @Nullable OpenIdTokenResponseValidator tokenResponseValidator) {

        if (clientConfiguration.isEnabled()) {
            if (clientConfiguration.getGrantType() == GrantType.PASSWORD) {
                if (clientConfiguration.getToken().isPresent()) {
                    if (oauthAuthenticationMapper != null) {
                        return new OauthPasswordAuthenticationProvider(tokenEndpointClient, clientConfiguration, oauthAuthenticationMapper);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Skipped password grant flow for provider [{}] because no user details mapper could be found", clientConfiguration.getName());
                        }
                    }
                } else if (clientConfiguration.getOpenid().isPresent()) {
                    if (openIdProviderMetadata != null && tokenResponseValidator != null) {
                        if (openIdAuthenticationMapper == null) {
                            openIdAuthenticationMapper = defaultOpenIdAuthenticationMapper;
                        }
                        if (openIdAuthenticationMapper != null) {
                            return new OpenIdPasswordAuthenticationProvider(clientConfiguration, openIdProviderMetadata, tokenEndpointClient, openIdAuthenticationMapper, tokenResponseValidator);
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Skipped password grant flow for provider [{}] because no user details mapper could be found", clientConfiguration.getName());
                            }
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Skipped password grant flow for provider [{}] because no provider metadata and token validator could be found", clientConfiguration.getName());
                        }
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Skipped password grant flow for provider [{}] because no token endpoint or openid configuration was found", clientConfiguration.getName());
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Skipped password grant flow for provider [{}] because the grant type is not 'password'", clientConfiguration.getName());
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skipped password grant flow for provider [{}] because the configuration is disabled", clientConfiguration.getName());
            }
        }

        return null;
    }
}
