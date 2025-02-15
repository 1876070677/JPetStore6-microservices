package org.mybatis.jpetstore.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrnLogMapper {
    void insertTrnLog(String uuid);

    int checkChange(String uuid);
}
