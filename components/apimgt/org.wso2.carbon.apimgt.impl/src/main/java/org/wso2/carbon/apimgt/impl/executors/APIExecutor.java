/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.apimgt.impl.executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.api.FaultGatewaysException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIStatus;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIManagerFactory;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.registry.extensions.interfaces.Execution;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.util.Map;
import java.util.Set;

/**
 * This class is an implementation of the
 * interface {@link org.wso2.carbon.governance.registry.extensions.interfaces.Execution}
 * This class consists methods that will create, prototype, publish, block, deprecate and
 * retire  an API to API Manager.
 *
 * This executor used to publish a service to API store as a API.
 *
 * @see org.wso2.carbon.governance.registry.extensions.interfaces.Execution
 */
public class APIExecutor implements Execution {

    Log log = LogFactory.getLog(APIExecutor.class);

    /**
     * This method is called when the execution class is initialized.
     * All the execution classes are initialized only once.
     *
     * @param parameterMap Static parameter map given by the user.
     *                     These are the parameters that have been given in the
     *                     lifecycle configuration as the parameters of the executor.
     */
    public void init(Map parameterMap) {
    }

    /**
     * @param context      The request context that was generated from the registry core.
     *                     The request context contains the resource, resource path and other
     *                     variables generated during the initial call.
     * @param currentState The current lifecycle state.
     * @param targetState  The target lifecycle state.
     * @return Returns whether the execution was successful or not.
     */
    public boolean execute(RequestContext context, String currentState, String targetState) {
        boolean executed = false;
        String user = "admin"; //need to pass the logged in user here
        String domain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        String userWithDomain = APIUtil.appendDomainWithUser(user, domain);
        userWithDomain = APIUtil.replaceEmailDomainBack(userWithDomain);

        try {
            String tenantUserName = MultitenantUtils.getTenantAwareUsername(userWithDomain);
            int tenantId = ServiceReferenceHolder.getInstance().getRealmService().getTenantManager().getTenantId(domain);

            GenericArtifactManager artifactManager = APIUtil
                    .getArtifactManager(context.getSystemRegistry(), APIConstants.API_KEY);
            Registry registry = ServiceReferenceHolder.getInstance().
                    getRegistryService().getGovernanceUserRegistry(tenantUserName, tenantId);
            Resource apiResource = context.getResource();
            String artifactId = apiResource.getUUID();
            if (artifactId == null) {
                return executed;
            }
            GenericArtifact apiArtifact = artifactManager.getGenericArtifact(artifactId);
            API api = APIUtil.getAPI(apiArtifact);
            APIProvider apiProvider = APIManagerFactory.getInstance().getAPIProvider(userWithDomain);

            APIStatus oldStatus = api.getStatus();
            APIStatus newStatus = APIUtil.getApiStatus(targetState);
            if ((oldStatus.equals(APIStatus.CREATED) || oldStatus.equals(APIStatus.PROTOTYPED))
                    && newStatus.equals(APIStatus.PUBLISHED)) {
                Set<Tier> tiers = api.getAvailableTiers();
                String endPoint = api.getEndpointConfig();
                if(endPoint != null && endPoint.trim().length() > 0){
                    if(tiers == null || tiers.size()<= 0 ){
                       throw new APIManagementException("Failed to publish service to API store while executing APIExecutor. No Tiers selected");
                    }
                }else{
                    throw new APIManagementException("Failed to publish service to API store while executing APIExecutor. No endpoint selected");
                }
            }
            executed = apiProvider.updateAPIStatus(api.getId(), targetState, true, false, true);
            //Setting resource again to the context as it's updated within updateAPIStatus method
            String apiPath = APIUtil.getAPIPath(api.getId());

            apiResource = registry.get(apiPath);
            context.setResource(apiResource);
        } catch (RegistryException e) {
            log.error("Failed to get the generic artifact while executing APIExecutor. ", e);
        } catch (APIManagementException e) {
            log.error("Failed to publish service to API store while executing APIExecutor. ", e);
        } catch (FaultGatewaysException e) {
            log.error("Failed to publish service gateway while executing APIExecutor. ", e);
        } catch (UserStoreException e) {
            log.error("Failed to publish service gateway while executing APIExecutor. ", e);
        }
        return executed;
    }
}
