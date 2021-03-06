package com.tencent.tbds.alert.dao;

import com.tencent.tbds.alert.domain.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Created by jerryjzhang on 2016/3/17.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, String>, JpaSpecificationExecutor {
     Page<Alert> findAll(Pageable pageable);
}
