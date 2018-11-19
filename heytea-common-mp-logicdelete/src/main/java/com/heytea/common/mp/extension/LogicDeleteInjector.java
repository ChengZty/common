package com.heytea.common.mp.extension;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.heytea.common.mp.extension.methods.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogicDeleteInjector extends AbstractSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList() {
        return Stream.of(
                new Insert(),
                new LogicDelete(),
                new LogicDeleteByMap(),
                new LogicDeleteById(),
                new LogicDeleteBatchByIds(),
                new LogicUpdate(),
                new LogicUpdateById(),
                new LogicSelectById(),
                new LogicSelectBatchByIds(),
                new LogicSelectByMap(),
                new LogicSelectOne(),
                new LogicSelectCount(),
                new LogicSelectMaps(),
                new LogicSelectMapsPage(),
                new LogicSelectObjs(),
                new LogicSelectList(),
                new LogicSelectPage()
        ).collect(Collectors.toList());
    }
}
