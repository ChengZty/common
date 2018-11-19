package com.heytea.common.mp.extension.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.heytea.common.mp.extension.LogicAbstractMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Map;

/**
 * <p>
 * 根据 ID 删除
 * </p>
 *
 * @author hubin
 * @since 2018-06-13
 */
public class LogicDeleteByMap extends LogicAbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql;
        SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BY_MAP;
        if (tableInfo.isLogicDelete()) {
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlLogicSet(tableInfo), sqlWhereByMap(tableInfo));
        } else {
            sqlMethod = SqlMethod.DELETE_BY_MAP;
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), this.sqlWhereByMap(tableInfo));
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        return addUpdateMappedStatement(mapperClass, Map.class, sqlMethod.getMethod(), sqlSource);
    }
}
