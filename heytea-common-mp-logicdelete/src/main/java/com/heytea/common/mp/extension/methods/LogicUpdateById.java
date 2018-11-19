package com.heytea.common.mp.extension.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.heytea.common.mp.extension.LogicAbstractMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <p>
 * 根据 ID 更新有值字段
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public class LogicUpdateById extends LogicAbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql;
        SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
        if (tableInfo.isLogicDelete()) {

            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet(true, false, tableInfo, "et."),
                tableInfo.getKeyColumn(), new StringBuilder("et.").append(tableInfo.getKeyProperty()).toString(),
                new StringBuilder("<if test=\"et instanceof java.util.Map\">")
                    .append("<if test=\"et.MP_OPTLOCK_VERSION_ORIGINAL!=null\">")
                    .append(" AND ${et.MP_OPTLOCK_VERSION_COLUMN}=#{et.MP_OPTLOCK_VERSION_ORIGINAL}")
                    .append("</if></if>").append(getLogicDeleteSql(true,tableInfo)));
        } else {
            sqlMethod = SqlMethod.UPDATE_BY_ID;
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
                sqlSet(false, false, tableInfo, "et."),
                tableInfo.getKeyColumn(), "et." + tableInfo.getKeyProperty(),
                new StringBuilder("<if test=\"et instanceof java.util.Map\">")
                    .append("<if test=\"et.MP_OPTLOCK_VERSION_ORIGINAL!=null\">")
                    .append(" AND ${et.MP_OPTLOCK_VERSION_COLUMN}=#{et.MP_OPTLOCK_VERSION_ORIGINAL}")
                    .append("</if></if>"));
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }
}
