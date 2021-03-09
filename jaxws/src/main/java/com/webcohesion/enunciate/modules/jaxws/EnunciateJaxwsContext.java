/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webcohesion.enunciate.modules.jaxws;

import com.webcohesion.enunciate.EnunciateContext;
import com.webcohesion.enunciate.api.services.ServiceGroup;
import com.webcohesion.enunciate.module.EnunciateModuleContext;
import com.webcohesion.enunciate.modules.jaxb.EnunciateJaxbContext;
import com.webcohesion.enunciate.modules.jaxb.model.ImplicitSchemaElement;
import com.webcohesion.enunciate.modules.jaxb.model.SchemaInfo;
import com.webcohesion.enunciate.modules.jaxws.model.EndpointInterface;
import com.webcohesion.enunciate.modules.jaxws.model.WebMessage;
import com.webcohesion.enunciate.modules.jaxws.model.WebMessagePart;
import com.webcohesion.enunciate.modules.jaxws.model.WebMethod;
import com.webcohesion.enunciate.util.OneTimeLogMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ryan Heaton
 */
@SuppressWarnings ( "unchecked" )
public class EnunciateJaxwsContext extends EnunciateModuleContext {

  private final EnunciateJaxbContext jaxbContext;
  private final boolean useSourceParameterNames;
  private final Map<String, WsdlInfo> wsdls = new HashMap<String, WsdlInfo>();
  private final List<EndpointInterface> endpointInterfaces = new ArrayList<EndpointInterface>();

  public EnunciateJaxwsContext(EnunciateJaxbContext jaxbContext, boolean useSourceParameterNames) {
    super(jaxbContext.getContext());
    this.jaxbContext = jaxbContext;
    this.useSourceParameterNames = useSourceParameterNames;
  }

  public EnunciateContext getContext() {
    return context;
  }

  public EnunciateJaxbContext getJaxbContext() {
    return jaxbContext;
  }

  public boolean isUseSourceParameterNames() {
    return useSourceParameterNames;
  }

  public List<EndpointInterface> getEndpointInterfaces() {
    return endpointInterfaces;
  }

  public Map<String, WsdlInfo> getWsdls() {
    return wsdls;
  }

  /**
   * Add an endpoint interface to the model.
   *
   * @param ei The endpoint interface to add to the model.
   */
  public void add(EndpointInterface ei) {
    String namespace = ei.getTargetNamespace();

    String prefix = this.jaxbContext.addNamespace(namespace);

    WsdlInfo wsdlInfo = wsdls.get(namespace);
    if (wsdlInfo == null) {
      wsdlInfo = new WsdlInfo(jaxbContext);
      wsdlInfo.setId(prefix);
      wsdls.put(namespace, wsdlInfo);
      wsdlInfo.setTargetNamespace(namespace);
    }

    for (WebMethod webMethod : ei.getWebMethods()) {
      for (WebMessage webMessage : webMethod.getMessages()) {
        for (WebMessagePart messagePart : webMessage.getParts()) {
          if (messagePart.isImplicitSchemaElement()) {
            ImplicitSchemaElement implicitElement = (ImplicitSchemaElement) messagePart;
            String particleNamespace = messagePart.getParticleQName().getNamespaceURI();
            SchemaInfo schemaInfo = this.jaxbContext.getSchemas().get(particleNamespace);
            if (schemaInfo == null) {
              schemaInfo = new SchemaInfo(this.jaxbContext);
              schemaInfo.setId(this.jaxbContext.addNamespace(particleNamespace));
              schemaInfo.setNamespace(particleNamespace);
              this.jaxbContext.getSchemas().put(particleNamespace, schemaInfo);
            }
            schemaInfo.getImplicitSchemaElements().add(implicitElement);
          }
        }
      }
    }

    wsdlInfo.getEndpointInterfaces().add(ei);
    this.endpointInterfaces.add(ei);
    debug("Added %s as a JAX-WS endpoint interface.", ei.getQualifiedName());


    if (getContext().getProcessingEnvironment().findSourcePosition(ei) == null) {
      OneTimeLogMessage.SOURCE_FILES_NOT_FOUND.log(getContext());
      if (OneTimeLogMessage.SOURCE_FILES_NOT_FOUND.getLogged() <= 3) {
        info("Unable to find source file for %s.", ei.getQualifiedName());
      }
      else {
        debug("Unable to find source file for %s.", ei.getQualifiedName());
      }
    }
  }

  public String getContextPath() {
    return "";
  }

}
