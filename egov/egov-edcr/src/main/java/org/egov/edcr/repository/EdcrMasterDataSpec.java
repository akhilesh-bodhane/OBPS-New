package org.egov.edcr.repository;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

import org.egov.edcr.contract.EdcrMasterDataRequest;
import org.egov.edcr.entity.EdcrMasterData;
import org.springframework.data.jpa.domain.Specification;

public class EdcrMasterDataSpec {

	public EdcrMasterDataSpec() {
		// TODO Auto-generated constructor stub
	}

	public static final Specification<EdcrMasterData> edcrMasterDataSearchSpec(EdcrMasterDataRequest edcrMasterDataRequest) {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            if (isNotBlank(edcrMasterDataRequest.getCode()))
                predicates.add(builder.equal(root.get("code"), edcrMasterDataRequest.getCode()));
            if (isNotBlank(edcrMasterDataRequest.getSector()))
                predicates.add(builder.equal(root.get("sector"), edcrMasterDataRequest.getSector()));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
