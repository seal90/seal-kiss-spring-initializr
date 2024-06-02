package com.github.seal90.kiss.multi.integration.db.dao;

import com.github.seal90.kiss.multi.integration.db.dos.DemoDO;
import com.github.seal90.kiss.multi.integration.db.dos.DemoParam;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DemoDAO {
    long countByParam(DemoParam demoParam);

    int deleteByParam(DemoParam demoParam);

    int deleteByPrimaryKey(Long id);

    int insert(DemoDO row);

    int insertSelective(DemoDO row);

    List<DemoDO> selectByParam(DemoParam demoParam);

    DemoDO selectByPrimaryKey(Long id);

    int updateByParamSelective(@Param("row") DemoDO row, @Param("demoParam") DemoParam demoParam);

    int updateByParam(@Param("row") DemoDO row, @Param("demoParam") DemoParam demoParam);

    int updateByPrimaryKeySelective(DemoDO row);

    int updateByPrimaryKey(DemoDO row);
}