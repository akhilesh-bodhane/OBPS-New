package org.egov.edcr.repository;

import org.egov.edcr.entity.EdcrMasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EdcrMasterDataRepository extends JpaRepository<EdcrMasterData, Long>, JpaSpecificationExecutor<EdcrMasterData>{
	
}
