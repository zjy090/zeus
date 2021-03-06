package top.yulegou.zeus.publish.db;/*
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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import top.yulegou.zeus.dao.domain.ZPublishRule;
import top.yulegou.zeus.dao.domain.ZTask;
import top.yulegou.zeus.dao.domain.publish.DbColumnBindConfig;
import top.yulegou.zeus.dao.domain.publish.DbConnectionConfig;
import top.yulegou.zeus.dao.domain.publish.DbPublishRuleConfig;
import top.yulegou.zeus.dao.domain.publish.DbTableConfig;
import top.yulegou.zeus.domain.ContentCollectedDTO;
import top.yulegou.zeus.domain.PublishResult;
import top.yulegou.zeus.manager.db.MysqlManager;
import top.yulegou.zeus.util.ZeusBeanUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author irisroyalty
 * @date 2020/7/4
 **/
@Slf4j
public class MysqlPublishExecutor {
    @Autowired
    private MysqlManager mysqlManager;

    private static MysqlPublishExecutor mysqlPublishExecutor = new MysqlPublishExecutor();
    public static MysqlPublishExecutor getPublish() {
        if (mysqlPublishExecutor.mysqlManager == null) {
            mysqlPublishExecutor.mysqlManager = ZeusBeanUtil.getBean(MysqlManager.class);
        }
        return mysqlPublishExecutor;
    }
    private MysqlPublishExecutor() {}
    public PublishResult publish(ContentCollectedDTO content, ZTask zTask,
                                 ZPublishRule publishRule, DbConnectionConfig connectionConfig) {
        DbPublishRuleConfig ruleConfig = (DbPublishRuleConfig) publishRule.getRuleConfig();
        DbTableConfig tableConfig = ruleConfig.getTableConfig();
        if (tableConfig == null
                || tableConfig.getTableFields() == null
                || tableConfig.getTableFields().isEmpty()) {
            return PublishResult.success();
        }
        for (DbColumnBindConfig tableField : tableConfig.getTableFields()) {
            tableField.getTableName();
            if (tableField.getFieldBind() == null
                    || tableField.getFieldBind().isEmpty()) {
                continue;
            }
            Map<String, String> insertKeyValue = new HashMap<>();
            tableField.getFieldBind().forEach((x, y)-> {
                String realKey = null;
                String fieldContent = null;
                if (StringUtils.startsWith(y, "field:")) {
                    realKey = StringUtils.substring(y, 6);
                    fieldContent = content.getFieldsRst().get(realKey);
                } else if (StringUtils.startsWith(y, "custom:")) {
                    fieldContent = StringUtils.substring(y, 7);
                }
               if (StringUtils.isNotEmpty(fieldContent)) {
                   insertKeyValue.put(x, fieldContent);
               }
            });
            try {
                mysqlManager.insertRow(connectionConfig, tableField.getTableName(), insertKeyValue);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                PublishResult.failed(throwables.getMessage());
            }

        };
        return PublishResult.success();
    }
}
