package com.tenquare.qa.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tenquare.qa.pojo.Problem;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ProblemDao extends JpaRepository<Problem,String>,JpaSpecificationExecutor<Problem>{

    @Query(value = "select * from tb_problem,tb_pl where id = problemid and labelid = ? ORDER BY replytime Desc",nativeQuery = true)
    public Page<Problem> newList(String labelid, Pageable pageable);

    @Query(value = "select * from tb_problem,tb_pl where id = problemid and labelid = ? ORDER BY reply Desc",nativeQuery = true)
    public Page<Problem> hotList(String labelid, Pageable pageable);

    @Query(value = "select * from tb_problem,tb_pl where id = problemid and labelid = ? AND reply=0 ORDER BY createtime Desc",nativeQuery = true)
    public Page<Problem> waitList(String labelid, Pageable pageable);
}
