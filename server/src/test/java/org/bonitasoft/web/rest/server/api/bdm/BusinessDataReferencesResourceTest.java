package org.bonitasoft.web.rest.server.api.bdm;

import static org.bonitasoft.web.rest.server.utils.ResponseAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.api.BusinessDataAPI;
import org.bonitasoft.engine.business.data.BusinessDataReference;
import org.bonitasoft.engine.business.data.MultipleBusinessDataReference;
import org.bonitasoft.engine.business.data.SimpleBusinessDataReference;
import org.bonitasoft.web.rest.server.api.bdm.BusinessDataReferencesResource;
import org.bonitasoft.web.rest.server.utils.RestletTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;

@RunWith(MockitoJUnitRunner.class)
public class BusinessDataReferencesResourceTest extends RestletTest {

    @Mock
    private BusinessDataAPI bdmAPI;

    @Override
    protected ServerResource configureResource() {
        return new BusinessDataReferencesResource(bdmAPI);
    }

    private SimpleBusinessDataReference buildSimpleEmployeeReference(final String name, final long businessDataId) {
        return new org.bonitasoft.engine.business.data.impl.SimpleBusinessDataReferenceImpl(name,
                "com.bonitasoft.pojo.Employee", businessDataId);
    }

    private MultipleBusinessDataReference buildMultipleEmployeeReference(final String name, final long... businessDataIds) {
        final List<Long> ids = new ArrayList<Long>();
        for (final long businessDataId : businessDataIds) {
            ids.add(businessDataId);
        }
        return new org.bonitasoft.engine.business.data.impl.MultipleBusinessDataReferenceImpl(name,
                "com.bonitasoft.pojo.Employee", ids);
    }

    @Test
    public void should_respond_bad_request_when_caseId_filter_not_specified() throws Exception {

        final Response response = request("/bdm/businessDataReference?f=unknownfilter,caseId=").get();

        assertThat(response).hasStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    @Test
    public void should_respond_bad_request_when_caseId_filter_is_not_a_number() throws Exception {

        final Response response = request("/bdm/businessDataReference?f=caseId%3Dtoto").get();

        assertThat(response).hasStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    @Test
    public void should_respond_bad_request_when_page_and_count_parameters_are_not_specified() throws Exception {

        final Response response = request("/bdm/businessDataReference?f=caseId%3D486&p=4").get();

        assertThat(response).hasStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    @Test
    public void should_respond_bad_request_when_page_parameter_is_not_a_number() throws Exception {

        final Response response = request("/bdm/businessDataReference?f=caseId%3D486&p=toto&c=2").get();

        assertThat(response).hasStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    @Test
    public void should_respond_bad_request_when_count_parameter_is_not_a_number() throws Exception {

        final Response response = request("/bdm/businessDataReference?f=caseId%3D486&p=0&c=foo").get();

        assertThat(response).hasStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    @Test
    public void should_return_the_references_of_the_business_data_of_the_process_instance() throws Exception {
        final List<BusinessDataReference> references = new ArrayList<BusinessDataReference>();
        references.add(buildSimpleEmployeeReference("john", 487467354L));
        references.add(buildMultipleEmployeeReference("Ateam", 687646784L, 2313213874354L));
        when(bdmAPI.getProcessBusinessDataReferences(486L, 10, 10)).thenReturn(references);

        // TODO add queryParam to RequestBuilder
        final Response response = request("/bdm/businessDataReference?f=caseId%3D486&p=1&c=10").get();

        assertThat(response).hasStatus(Status.SUCCESS_OK);
        assertThat(response).hasJsonEntityEqualTo(readFile("refs.json"));
    }


}
