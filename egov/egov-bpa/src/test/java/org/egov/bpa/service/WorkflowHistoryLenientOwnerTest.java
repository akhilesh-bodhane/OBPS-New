package org.egov.bpa.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.egov.bpa.transaction.service.WorkflowHistoryService;
import org.egov.bpa.transaction.workflow.BpaWorkFlowService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/** A list row whose owner position has no assignment must not break the whole search result. */
public class WorkflowHistoryLenientOwnerTest {

    @InjectMocks
    private WorkflowHistoryService workflowHistoryService;
    @Mock
    private BpaWorkFlowService bpaWorkFlowService;

    @Before
    public void before() {
        initMocks(this);
        when(bpaWorkFlowService.getAssignmentsByPositionAndDate(eq(116L), any(Date.class))).thenReturn(new ArrayList<>());
    }

    @Test(expected = ApplicationRuntimeException.class)
    public void strictLookupStillThrows() {
        workflowHistoryService.getUserDesignationAndPositionByPositionAndDate(116L, new Date());
    }

    @Test
    public void lenientDesignationLookupReturnsPlaceholder() {
        Map<String, String> owner = workflowHistoryService.getUserDesignationAndPositionLenient(116L, new Date());
        assertEquals("N/A", owner.get("name"));
        assertTrue(owner.get("designation").isEmpty());
    }

    @Test
    public void lenientNameLookupReturnsPlaceholder() {
        assertEquals("N/A", workflowHistoryService.getOwnerNameLenient(116L, new Date()));
    }
}
