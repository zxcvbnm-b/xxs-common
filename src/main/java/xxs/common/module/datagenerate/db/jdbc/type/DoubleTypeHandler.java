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

import xxs.common.module.datagenerate.db.dto.TableColumnInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class DoubleTypeHandler extends BaseTypeHandler<Double> {

    @Override
    protected Double getParameterByTableColumnInfo(TableColumnInfo tableColumnInfo) {
        Random random = new Random();
        return Math.abs(random.nextDouble());
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Double parameter, TableColumnInfo tableColumnInfo)
            throws SQLException {
        ps.setDouble(i, parameter);
    }

}
