/****************************************************************
@author: Narendra Maravi
Date   : 01-JAN-2023
Class  : BpaNocApplicationHistoryRepository.java
******************************************************************/
package org.egov.bpa.transaction.repository;

import java.util.List;

import org.egov.bpa.transaction.entity.BpaNocApplicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface BpaNocApplicationHistoryRepository extends JpaRepository<BpaNocApplicationHistory, Long> {

	@Query("select noc from BpaNocApplicationHistory noc where noc.nocApplicationNumber =:nocAppNo")
	List<BpaNocApplicationHistory> findByNocApplicationNumber(@Param("nocAppNo") String nocAppNo);
	
	@Modifying
	@Query("update BpaNocApplicationHistory noc set noc.status = '62' where noc.nocApplicationNumber =:nocAppNo")
	void updateNocApplicationStatus(@Param("nocAppNo") String nocAppNo);

}
