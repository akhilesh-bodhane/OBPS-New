package org.egov.edcr.web.controller.report;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.egov.edcr.contract.EdcrMasterDataRequest;
import org.egov.edcr.entity.EdcrMasterData;
import org.egov.edcr.service.EdcrMasterService;
import org.egov.infra.web.support.ui.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/edcrmaster/search")
public class EdcrMasterSearchController {

	@Autowired
    private EdcrMasterService edcrMasterService;

    @ModelAttribute
    public EdcrMasterDataRequest edcrMasterDataRequest() {
        return new EdcrMasterDataRequest();
    }

    @GetMapping
    public String edcrMasterDataSearchView() {
        return "edcrmaster-search";
    }

    @PostMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DataTable<EdcrMasterData> edcrMasterDataReport(@ModelAttribute EdcrMasterDataRequest edcrMasterDataRequest) {
        return new DataTable<>(edcrMasterService.getEdcrMasterData(edcrMasterDataRequest),
        		edcrMasterDataRequest.draw());
    }
}
