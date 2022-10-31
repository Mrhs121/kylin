/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kylin.rest.controller;

import static org.apache.kylin.common.constant.HttpConstant.HTTP_VND_APACHE_KYLIN_V4_PUBLIC_JSON;

import org.apache.kylin.common.util.JsonUtil;
import org.apache.kylin.common.util.NLocalFileMetadataTestCase;
import org.apache.kylin.rest.constant.Constant;
import org.apache.kylin.rest.request.ViewDDLRequest;
import org.apache.kylin.rest.service.SparkDDLService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class SparkDDLControllerTest extends NLocalFileMetadataTestCase {
  private MockMvc mockMvc;

  @Mock
  private SparkDDLService sparkDDLService;

  @InjectMocks
  private SparkDDLController ddlController = Mockito.spy(new SparkDDLController());

  private final Authentication authentication = new TestingAuthenticationToken("ADMIN", "ADMIN", Constant.ROLE_ADMIN);

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(ddlController)
        .defaultRequest(MockMvcRequestBuilders.get("/")).build();
    SecurityContextHolder.getContext().setAuthentication(authentication);
    overwriteSystemProp("HADOOP_USER_NAME", "root");
    createTestMetadata();
  }

  @After
  public void tearDown() {
    cleanupTestMetadata();
  }

  @Test
  public void testExecuteSQL() throws Exception {
    ViewDDLRequest request = new ViewDDLRequest();
    request.setProject("ssb");

    mockMvc.perform(MockMvcRequestBuilders.post("/api/spark_source/ddl")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtil.writeValueAsString(request))
        .accept(MediaType.parseMediaType(HTTP_VND_APACHE_KYLIN_V4_PUBLIC_JSON)))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }

  @Test
  public void testDescription() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/spark_source/ddl/description?project=ssb")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.parseMediaType(HTTP_VND_APACHE_KYLIN_V4_PUBLIC_JSON)))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  }
}