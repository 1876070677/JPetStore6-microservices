package org.mybatis.jpetstore.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrnLogMapper {
    void insertTrnLog(int uuid);

    int checkChange(int uuid);
}
