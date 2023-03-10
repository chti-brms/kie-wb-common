/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.shared.rest;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectRequest extends Entity {
    
    String groupId;
    String version;
    
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId( String groupId ) {
        this.groupId = groupId;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion( String version ) {
        this.version = version;
    }
    
}