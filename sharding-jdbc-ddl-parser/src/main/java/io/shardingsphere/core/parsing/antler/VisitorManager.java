/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
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
 * </p>
 */

package io.shardingsphere.core.parsing.antler;

import java.util.HashMap;
import java.util.Map;

import io.shardingsphere.core.constant.DatabaseType;
import io.shardingsphere.core.parsing.antler.statement.visitor.CreateTableVisitor;
import io.shardingsphere.core.parsing.antler.statement.visitor.OnlySingleTableVisitor;
import io.shardingsphere.core.parsing.antler.statement.visitor.StatementVisitor;
import io.shardingsphere.core.parsing.antler.visitor.OnlyMultiTableVisitor;
import io.shardingsphere.core.parsing.antler.visitor.mysql.MySQLAlterTableVisitor;
import io.shardingsphere.core.parsing.antler.visitor.oracle.OracleAlterTableVisitor;
import io.shardingsphere.core.parsing.antler.visitor.postgre.PostgreAlterTableVisitor;
import io.shardingsphere.core.parsing.antler.visitor.sqlserver.SQLServerAlterTableVisitor;

public class VisitorManager {
    private Map<String, StatementVisitor> visitors = new HashMap<String, StatementVisitor>();
    private static VisitorManager instance = new VisitorManager();

    private VisitorManager() {
        visitors.put("CreateTable", new CreateTableVisitor());
        visitors.put(DatabaseType.MySQL + "AlterTable", new MySQLAlterTableVisitor());
        visitors.put(DatabaseType.PostgreSQL + "AlterTable", new PostgreAlterTableVisitor());
        visitors.put(DatabaseType.Oracle + "AlterTable", new OracleAlterTableVisitor());
        visitors.put(DatabaseType.SQLServer + "AlterTable", new SQLServerAlterTableVisitor());
        visitors.put("DropTable", new OnlySingleTableVisitor());
        visitors.put("TruncateTable", new OnlySingleTableVisitor());

        visitors.put(DatabaseType.MySQL + "DropTable", new OnlyMultiTableVisitor());
        
        visitors.put(DatabaseType.PostgreSQL + "DropTable", new OnlyMultiTableVisitor());
        visitors.put(DatabaseType.PostgreSQL + "TruncateTable", new OnlyMultiTableVisitor());
    }

    public static VisitorManager getInstance() {
        return instance;
    }

    /**
     * get statement visitor
     * 
     * @param dbType
     * @param commandName
     * @return
     */
    public StatementVisitor getVisitor(final DatabaseType dbType, final String commandName) {
        String key = dbType.name() + commandName;
        StatementVisitor visitor = visitors.get(key);
        if (visitor != null) {
            return visitor;
        }

        return visitors.get(commandName);
    }
}
