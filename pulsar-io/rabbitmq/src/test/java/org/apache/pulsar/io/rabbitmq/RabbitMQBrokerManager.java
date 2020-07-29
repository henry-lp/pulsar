/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
package org.apache.pulsar.io.rabbitmq;

import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class RabbitMQBrokerManager {

    private final String PORT = "5672";
    private final Broker broker = new Broker();


    public void startBroker() throws Exception {
        BrokerOptions brokerOptions = getBrokerOptions();
        broker.startup(brokerOptions);
    }

    public void stopBroker() {
        broker.shutdown();
    }

    BrokerOptions getBrokerOptions() throws Exception {
        Path tmpFolder = Files.createTempDirectory("qpidWork");
        Path homeFolder = Files.createTempDirectory("qpidHome");
        File etc = new File(homeFolder.toFile(), "etc");
        etc.mkdir();
		try (java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File(etc, "passwd"))) {
			fos.write("guest:guest\n".getBytes());
			fos.close();
			org.apache.qpid.server.BrokerOptions brokerOptions = new org.apache.qpid.server.BrokerOptions();
			brokerOptions.setConfigProperty("qpid.work_dir", tmpFolder.toAbsolutePath().toString());
			brokerOptions.setConfigProperty("qpid.amqp_port", PORT);
			brokerOptions.setConfigProperty("qpid.home_dir", homeFolder.toAbsolutePath().toString());
			java.lang.String configPath = getFile("qpid.json").getAbsolutePath();
			brokerOptions.setInitialConfigurationLocation(configPath);
			return brokerOptions;
		}
    }

    private File getFile(String name) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(name).getFile());
    }
}
