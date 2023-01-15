/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package xxs.common.module.datagenerate.db.jdbc.type;

import org.springframework.util.CollectionUtils;
import xxs.common.module.datagenerate.db.dto.JdbcTypeInfo;
import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.utils.random.RandomString;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class NStringTypeHandler extends BaseTypeHandler<String> {

    @Override
    protected String getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        JdbcTypeInfo jdbcTypeInfo = tableColumnInfo.getJdbcTypeInfo();
        Integer count = 10;
        if (jdbcTypeInfo != null) {
            List<Object> infoArguments = jdbcTypeInfo.getArguments();
            if (!CollectionUtils.isEmpty(infoArguments)) {
                if (infoArguments.get(0) instanceof Integer) {
                    count = (Integer) infoArguments.get(0);
                    count = count / 3;
                }
            }
        }
        return RandomString.getRandomChineseString(random.nextInt(count));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setNString(i, parameter);
    }
}
