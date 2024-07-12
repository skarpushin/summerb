/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.scaffold.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.scaffold.impl.EasyCrudServiceProxyFactoryImpl;
import org.summerb.easycrud.scaffold.impl.ScaffoldedMethodFactoryMySqlImpl;

/**
 * Use it when you don't want to impl the whole stack of interfaces and classes of Easy CRUD, when
 * you just need to execute one custom query.
 *
 * <p>Add this annotation to the method of custom sub-interface of {@link EasyCrudService}, impl of
 * which is instantiated by @link EasyCrudScaffold} (instead of defining class that implements this
 * interface)
 *
 * <p>Use named parameters which names will be resolved against method argument names. Make sure
 * parameter names are included in bytecode! Add "-parameters" to compiler args. With maven do it
 * like this:
 *
 * <pre>
 * {@code
 * <plugins>
 *   <plugin>
 *     <groupId>org.apache.maven.plugins</groupId>
 *     <artifactId>maven-compiler-plugin</artifactId>
 *     <version>3.8.0</version>
 *     <configuration>
 *       <compilerArgs>
 *         <arg>-parameters</arg>
 *       </compilerArgs>
 *     </configuration>
 *   </plugin>
 * </plugins>
 * }
 * </pre>
 *
 * <p>Then you can easily define new query to the underlying database.
 *
 * <p>This is normally implemented by {@link ScaffoldedMethodFactoryMySqlImpl}, but could be easily
 * overridden, by injecting custom {@link ScaffoldedMethodFactory} into {@link
 * EasyCrudServiceProxyFactoryImpl}
 *
 * <p>NOTE: Now, although service-layer supposed to know nothing about particulars of the storage
 * layer, I still believe that this "convenience shortcut" worth a shot.
 *
 * @author sergeyk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScaffoldedQuery {
  /**
   * @return SQL query to be used to perform the query
   */
  String value();

  /**
   * @return Customize row mapper if needed. If none specified, then EasyCrud will attempt to guess.
   *     For objects by default will be used {@link BeanPropertyRowMapper}
   */
  Class<? extends RowMapper> rowMapper() default RowMapper.class;
}
