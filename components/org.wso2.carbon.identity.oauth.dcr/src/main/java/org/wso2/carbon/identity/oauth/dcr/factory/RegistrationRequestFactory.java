/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth.dcr.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.FrameworkClientException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.FrameworkRuntimeException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityRequestFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityResponse;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityRequest;
import org.wso2.carbon.identity.oauth.common.OAuthConstants;
import org.wso2.carbon.identity.oauth.dcr.model.RegistrationRequest;
import org.wso2.carbon.identity.oauth.dcr.model.RegistrationRequestProfile;
import org.wso2.carbon.identity.oauth.dcr.util.DCRConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;

/**
 * RegistrationRequestFactory build the request for DCR Registry Request.
 */
public class RegistrationRequestFactory extends HttpIdentityRequestFactory {

    private static Log log = LogFactory.getLog(RegistrationRequestFactory.class);

    @Override
    public boolean canHandle(HttpServletRequest request, HttpServletResponse response) throws
                                                                                       FrameworkRuntimeException {
        boolean canHandle = false;
        if (request != null) {
            Matcher matcher = DCRConstants.DCR_ENDPOINT_REGISTER_URL_PATTERN.matcher(request.getRequestURI());
            if (matcher.matches() && HttpMethod.POST.equals(request.getMethod())) {
                canHandle = true;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("canHandle " + canHandle + " by RegistrationRequestFactory.");
        }
        return canHandle;
    }


    @Override
    public RegistrationRequest.RegistrationRequestBuilder create(HttpServletRequest request,
                                                                 HttpServletResponse response)
            throws FrameworkClientException {

        if (log.isDebugEnabled()) {
            log.debug("create RegistrationRequest.RegistrationRequestBuilder by RegistrationRequestFactory.");
        }
        RegistrationRequest.RegistrationRequestBuilder registerRequestBuilder = new
                RegistrationRequest.RegistrationRequestBuilder(request, response);
        create(registerRequestBuilder, request, response);
        return registerRequestBuilder;

    }

    @Override
    public void create(IdentityRequest.IdentityRequestBuilder builder, HttpServletRequest request,
                       HttpServletResponse response) throws FrameworkClientException {

        RegistrationRequest.RegistrationRequestBuilder registerRequestBuilder =
                (RegistrationRequest.RegistrationRequestBuilder) builder;

        super.create(registerRequestBuilder, request, response);


        try {
            Reader requestBodyReader = request.getReader();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonData = (JSONObject) jsonParser.parse(requestBodyReader);
            if (log.isDebugEnabled()) {
                log.debug("DCR request json : " + jsonData.toJSONString());
            }

            RegistrationRequestProfile registrationRequestProfile =
                    registerRequestBuilder.getRegistrationRequestProfile();
            if (registrationRequestProfile == null) {
                registrationRequestProfile = new RegistrationRequestProfile();
            }

            Object obj = jsonData.get(RegistrationRequest.RegisterRequestConstant.REDIRECT_URIS);
            if (obj != null && obj instanceof JSONArray) {
                JSONArray redirectUris = (JSONArray) obj;
                for (int i = 0; i < redirectUris.size(); i++) {
                    registrationRequestProfile.getRedirectUris().add(redirectUris.get(i).toString());
                }
            } else if (obj != null) {
                registrationRequestProfile.getRedirectUris().add((String) obj);
            }

            registrationRequestProfile.setTokenEndpointAuthMethod((String) jsonData
                    .get(RegistrationRequest.RegisterRequestConstant.TOKEN_ENDPOINT_AUTH_METHOD));


            obj = jsonData.get(RegistrationRequest.RegisterRequestConstant.GRANT_TYPES);
            if (obj != null && obj instanceof JSONArray) {
                JSONArray redirectUris = (JSONArray) obj;
                for (int i = 0; i < redirectUris.size(); i++) {
                    registrationRequestProfile.getGrantTypes().add(redirectUris.get(i).toString());
                }
            } else if (obj != null) {
                registrationRequestProfile.getGrantTypes().add((String) obj);
            }


            obj = jsonData.get(RegistrationRequest.RegisterRequestConstant.RESPONSE_TYPES);
            if (obj != null && obj instanceof JSONArray) {
                JSONArray redirectUris = (JSONArray) obj;
                for (int i = 0; i < redirectUris.size(); i++) {
                    registrationRequestProfile.getResponseTypes().add(redirectUris.get(i).toString());
                }
            } else if (obj != null) {
                registrationRequestProfile.getResponseTypes().add((String) obj);
            }

            registrationRequestProfile
                    .setClientName((String) jsonData.get(RegistrationRequest.RegisterRequestConstant.CLIENT_NAME));

            registrationRequestProfile.setClientUri((String) jsonData
                    .get(RegistrationRequest.RegisterRequestConstant.CLIENT_URI));
            registrationRequestProfile.setLogoUri((String) jsonData
                    .get(RegistrationRequest.RegisterRequestConstant.LOGO_URI));


            obj = jsonData.get(RegistrationRequest.RegisterRequestConstant.SCOPE);
            if (obj != null && obj instanceof JSONArray) {
                JSONArray redirectUris = (JSONArray) obj;
                for (int i = 0; i < redirectUris.size(); i++) {
                    registrationRequestProfile.getScopes().add(redirectUris.get(i).toString());
                }
            } else if (obj != null) {
                registrationRequestProfile.getScopes().add((String) obj);
            }

            obj = jsonData.get(RegistrationRequest.RegisterRequestConstant.CONTACTS);
            if (obj != null && obj instanceof JSONArray) {
                JSONArray redirectUris = (JSONArray) obj;
                for (int i = 0; i < redirectUris.size(); i++) {
                    registrationRequestProfile.getContacts().add(redirectUris.get(i).toString());
                }
            } else if (obj != null) {
                registrationRequestProfile.getContacts().add((String) obj);
            }

            registrationRequestProfile
                    .setTosUri((String) jsonData.get(RegistrationRequest.RegisterRequestConstant.TOS_URI));
            registrationRequestProfile.setPolicyUri((String) jsonData
                    .get(RegistrationRequest.RegisterRequestConstant.POLICY_URI));
            registrationRequestProfile.setJwksUri((String) jsonData
                    .get(RegistrationRequest.RegisterRequestConstant.JWKS_URI));
            registrationRequestProfile.setJkws((String) jsonData
                    .get(RegistrationRequest.RegisterRequestConstant.JWKS));
            registrationRequestProfile.setSoftwareId((String) jsonData
                    .get(RegistrationRequest.RegisterRequestConstant.SOFTWARE_ID));
            registrationRequestProfile.setSoftwareVersion((String) jsonData
                    .get(RegistrationRequest.RegisterRequestConstant.SOFTWARE_VERSION));

            //TODO:This parameter is a custom one and we have to remove if we can collect the user name by having
            // some authentication mechanism.
            registrationRequestProfile
                    .setOwner((String) jsonData.get(RegistrationRequest.RegisterRequestConstant.EXT_PARAM_OWNER));

            registerRequestBuilder.setRegistrationRequestProfile(registrationRequestProfile);


        } catch (IOException e) {
            String errorMessage = "Error occurred while reading servlet request body, " + e.getMessage();
            FrameworkClientException.error(errorMessage, e);
        } catch (ParseException e) {
            String errorMessage = "Error occurred while parsing the json object, " + e.getMessage();
            FrameworkClientException.error(errorMessage, e);
        }
    }

    @Override
    public HttpIdentityResponse.HttpIdentityResponseBuilder handleException(FrameworkClientException exception,
                                                                            HttpServletRequest request,
                                                                            HttpServletResponse response) {
        HttpIdentityResponse.HttpIdentityResponseBuilder builder =
                new HttpIdentityResponse.HttpIdentityResponseBuilder();
        String errorMessage = generateErrorResponse("ddd", exception.getMessage()).toJSONString();
        builder.setBody(errorMessage);
        builder.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
        builder.addHeader(OAuthConstants.HTTP_RESP_HEADER_CACHE_CONTROL,
                          OAuthConstants.HTTP_RESP_HEADER_VAL_CACHE_CONTROL_NO_STORE);
        builder.addHeader(OAuthConstants.HTTP_RESP_HEADER_PRAGMA,
                          OAuthConstants.HTTP_RESP_HEADER_VAL_PRAGMA_NO_CACHE);
        builder.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        return builder;
    }

    protected JSONObject generateErrorResponse(String error, String description) {
        JSONObject obj = new JSONObject();
        obj.put("error", error);
        obj.put("error_description", description);
        return obj;
    }
}
