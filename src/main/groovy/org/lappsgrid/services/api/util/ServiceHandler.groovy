package org.lappsgrid.services.api.util

import jp.go.nict.langrid.client.RequestAttributes
import jp.go.nict.langrid.client.soap.SoapClientFactory
import jp.go.nict.langrid.service_1_2.foundation.MatchingCondition
import jp.go.nict.langrid.service_1_2.foundation.Order
import jp.go.nict.langrid.service_1_2.foundation.servicemanagement.ServiceEntrySearchResult
import jp.go.nict.langrid.service_1_2.foundation.servicemanagement.ServiceManagementService

/**
 * The ServiceHandler class the does the actual work of querying a ServiceManager instance
 * for its list of services.
 *
 * @author Keith Suderman
 */
class ServiceHandler {

    String url
    String username
    String password
    String node

    def params

    Map searchTerms

    public ServiceHandler() {
        searchTerms = [
                id:'serviceId',
                name: 'serviceName',
                desc: 'serviceDescription',
                type: 'serviceType',
                domain: 'serviceTypeDomain'
        ]
    }

    Map handle() {

        def conditions = new MatchingCondition[params.size()]
        int i = 0
        params.each { name,value ->
            name = searchTerms[name] ?: name
            conditions[i] = new MatchingCondition(name, value, "PARTIAL")
            ++i
        }

        def order = [] as Order[]
        SoapClientFactory f = new SoapClientFactory();
        ServiceManagementService s = f.create(
                ServiceManagementService.class,
                new URL("$url/services/ServiceManagement")
        );
        RequestAttributes attr = (RequestAttributes)s;
        attr.setUserId(username);
        attr.setPassword(password);

        // The data that will be rendered as JSON or passed to the html template.
        Map result = [:]
        result.url = url
        result.totalCount = 0
        result.elements = []

        // The ServiceManager only allows us to fetch metadata for 100 services at a
        // time. So we have to be prepared to page through the entire list if more than
        // 100.
        int PAGE_SIZE = 100
        int count = 0
        ServiceEntrySearchResult more = s.searchServices(count, PAGE_SIZE, conditions, order, "ALL");
        while (more.elements.length > 0) {
            result.elements.addAll more.elements
            count += more.elements.length
            more = s.searchServices(count, PAGE_SIZE, conditions, order, "ALL")
        }
        result.totalCount = count
        return result
    }
}
