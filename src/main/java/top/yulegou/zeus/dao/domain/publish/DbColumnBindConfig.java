package top.yulegou.zeus.dao.domain.publish;/*
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

import lombok.Data;
import top.yulegou.zeus.domain.DbColumnsDTO;

import java.util.Map;

/**
 * @author irisroyalty
 * @date 2020/6/29
 **/
@Data
public class DbColumnBindConfig {
    String tableName;
    Map<String, String> fieldBind;
    /**
     * 字段相关信息,
     */
    Map<String, DbColumnsDTO> forShow;
}
