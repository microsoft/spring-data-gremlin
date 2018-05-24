/*
 * Copyright 2014-2018 the original author or authors.
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
package example.springdata.gremlin.config;

import com.microsoft.spring.data.gremlin.common.GremlinConfiguration;
import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.config.AbstractGremlinConfiguration;
import com.microsoft.spring.data.gremlin.repository.config.EnableGremlinRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@EnableGremlinRepositories(basePackages = "example.springdata.gremlin.repository")
@EnableConfigurationProperties(GremlinConfiguration.class)
@PropertySource("classpath:application.yml")
public class UserRepositoryConfiguration extends AbstractGremlinConfiguration {

    @Autowired
    private GremlinConfiguration config;

    @Override
    public GremlinConfiguration getGremlinConfiguration() {
        return this.config;
    }
}
