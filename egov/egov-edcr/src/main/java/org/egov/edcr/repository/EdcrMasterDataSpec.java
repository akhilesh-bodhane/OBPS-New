package org.egov.edcr.repository;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.persistence.criteria.Predicate;

import org.egov.edcr.contract.EdcrMasterDataRequest;
import org.egov.edcr.entity.EdcrMasterData;
import org.springframework.data.jpa.domain.Specification;

public class EdcrMasterDataSpec {

	public EdcrMasterDataSpec() {
		// TODO Auto-generated constructor stub
	}

	public static final Specification<EdcrMasterData> edcrMasterDataSearchSpec(EdcrMasterDataRequest edcrMasterDataRequest) {
        return (root, query, builder) -> {
            final Predicate predicate = builder.conjunction();
            if (isNotBlank(edcrMasterDataRequest.getCode()))
                predicate.getExpressions()
                        .add(builder.equal(root.get("code"), edcrMasterDataRequest.getCode()));
            if (isNotBlank(edcrMasterDataRequest.getSector()))
                predicate.getExpressions()
                        .add(builder.equal(root.get("sector"), edcrMasterDataRequest.getSector()));
            return predicate;
        };
    }
}
