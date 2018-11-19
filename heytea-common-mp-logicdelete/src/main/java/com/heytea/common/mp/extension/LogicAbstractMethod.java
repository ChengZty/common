package com.heytea.common.mp.extension;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>
 * 抽象的注入方法类
 * </p>
 *
 * @author hubin
 * @since 2018-04-06
 */
public abstract class LogicAbstractMethod extends AbstractMethod {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public LogicAbstractMethod() {
        // TO DO
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param table 表信息
     * @return sql and 片段
     */
    public String getLogicDeleteSql(boolean startWithAnd, TableInfo table) {
        StringBuilder sql = new StringBuilder();
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            if (fieldInfo.isLogicDelete()) {
                if (startWithAnd) {
                    sql.append(" AND ");
                }
                sql.append(fieldInfo.getColumn());
                // 判断逻辑删除字段是否为null
                sql.append(" is null");
            }
        }
        return sql.toString();
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param table 表信息
     * @return sql set 片段
     */
    protected String sqlLogicSet(TableInfo table) {
        List<TableFieldInfo> fieldList = table.getFieldList();
        StringBuilder set = new StringBuilder("SET ");
        int i = 0;
        for (TableFieldInfo fieldInfo : fieldList) {
            if (fieldInfo.isLogicDelete()) {
                if (++i > 1) {
                    set.append(",");
                }
                set.append(fieldInfo.getColumn()).append("=");
                set.append("now()");
            }
        }
        return set.toString();
    }

    // ------------ 处理逻辑删除条件过滤 ------------

    @Override
    protected String sqlWhereEntityWrapper(TableInfo table) {
        if (table.isLogicDelete()) {
            StringBuilder where = new StringBuilder(128);
            where.append("<choose><when test=\"ew!=null and !ew.emptyOfWhere\">");
            where.append("<trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\">");
            where.append("<if test=\"ew.entity!=null\">");
            if (StringUtils.isNotEmpty(table.getKeyProperty())) {
                where.append("<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">");
                where.append(" AND ").append(table.getKeyColumn()).append("=#{ew.entity.");
                where.append(table.getKeyProperty()).append("}");
                where.append("</if>");
            }
            List<TableFieldInfo> fieldList = table.getFieldList();
            for (TableFieldInfo fieldInfo : fieldList) {
                where.append(convertIfTag(fieldInfo, "ew.entity.", false));
                where.append(" AND ").append(sqlCondition(fieldInfo.getCondition(),
                        fieldInfo.getColumn(), "ew.entity." + fieldInfo.getEl()));
                where.append(convertIfTag(fieldInfo, true));
            }
            where.append("</if>");
            // 删除逻辑
            where.append(getLogicDeleteSql(true, table));
            // SQL 片段
            where.append("<if test=\"ew.sqlSegment!=null and ew.sqlSegment!=''\"> AND ${ew.sqlSegment}</if>");
            where.append("</trim>");
            where.append("</when><otherwise>WHERE ");
            // 删除逻辑
            where.append(getLogicDeleteSql(false, table));
            where.append("</otherwise></choose>");
            return where.toString();
        }
        // 正常逻辑
        return super.sqlWhereEntityWrapper(table);
    }

    @Override
    protected String sqlWhereByMap(TableInfo table) {
        if (table.isLogicDelete()) {
            return new StringBuilder()
                    .append("<trim prefix=\"WHERE\" prefixOverrides=\"AND\">")
                    .append("<if test=\"cm!=null and !cm.isEmpty\">")
                    .append("<foreach collection=\"cm\" index=\"k\" item=\"v\" separator=\"AND\">")
                    .append("<choose><when test=\"v==null\"> ${k} IS NULL </when>")
                    .append("<otherwise> ${k}=#{v} </otherwise></choose>")
                    .append("</foreach>")
                    .append("</if>")
                    .append(getLogicDeleteSql(true, table))
                    .append("</trim>")
                    .toString();
        }
        // 正常逻辑
        return super.sqlWhereByMap(table);
    }

}
