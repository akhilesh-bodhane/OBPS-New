package org.egov.edcr.service;

import org.egov.edcr.contract.EdcrMasterDataRequest;
import org.egov.edcr.entity.EdcrMasterData;
import org.egov.edcr.repository.EdcrMasterDataRepository;
import org.egov.edcr.repository.EdcrMasterDataSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EdcrMasterService {
	
	@Autowired
    private EdcrMasterDataRepository edcrMasterDataRepository;

    @Transactional
    public EdcrMasterData edcrMasterData(EdcrMasterData edcrMasterData) {
        return edcrMasterDataRepository.saveAndFlush(edcrMasterData);
    }

    public Page<EdcrMasterData> getEdcrMasterData(EdcrMasterDataRequest edcrMasterDataRequest) {
        Pageable pageable = new PageRequest(edcrMasterDataRequest.pageNumber(),
        		edcrMasterDataRequest.pageSize(),
        		edcrMasterDataRequest.orderDir(), edcrMasterDataRequest.orderBy());
        return edcrMasterDataRepository.findAll(EdcrMasterDataSpec.edcrMasterDataSearchSpec(edcrMasterDataRequest), pageable);
    }
}
