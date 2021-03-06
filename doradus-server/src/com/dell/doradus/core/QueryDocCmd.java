/*
 * Copyright (C) 2014 Dell, Inc.
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

package com.dell.doradus.core;

import com.dell.doradus.common.ApplicationDefinition;
import com.dell.doradus.common.TableDefinition;
import com.dell.doradus.common.UNode;
import com.dell.doradus.common.Utils;
import com.dell.doradus.search.SearchResultList;
import com.dell.doradus.service.StorageService;
import com.dell.doradus.service.rest.NotFoundException;
import com.dell.doradus.service.rest.UNodeOutCallback;
import com.dell.doradus.service.schema.SchemaService;

/**
 * Implements the REST commands: GET or PUT /{application}/{table}/_query. The query
 * parameters are passed in an input entity. Verifies the application and table, parses
 * the input entity, and passes the query to the application's registered storage service.
 */
public class QueryDocCmd extends UNodeOutCallback {

    @Override
    public UNode invokeUNodeOut(UNode inNode) {
        String application = m_request.getVariableDecoded("application");
        ApplicationDefinition appDef = SchemaService.instance().getApplication(application);
        if (appDef == null) {
            throw new NotFoundException("Unknown application: " + application);
        }
        Utils.require(inNode != null, "This command requires an input entity");
        
        String table = m_request.getVariableDecoded("table");
        TableDefinition tableDef = appDef.getTableDef(table);
        Utils.require(tableDef != null, "Unknown table for application '%s': %s", application, table);
        
        UNode rootNode = UNode.parse(m_request.getInputBody(), m_request.getInputContentType());
        StorageService storageService = SchemaService.instance().getStorageService(appDef);
        SearchResultList searchResult = storageService.objectQueryDoc(tableDef, rootNode);
        return searchResult.toDoc();
    }   // invokeUNodeOut

}   // class QueryDocCmd 
