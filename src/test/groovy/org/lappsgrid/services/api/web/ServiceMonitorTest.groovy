package org.lappsgrid.services.api.web

import jp.go.nict.langrid.client.RequestAttributes
import jp.go.nict.langrid.client.soap.SoapClientFactory
import jp.go.nict.langrid.service_1_2.foundation.servicemanagement.ServiceManagementService
import jp.go.nict.langrid.service_1_2.foundation.servicemonitor.ServiceMonitorService

/**
 *
 */
class ServiceMonitorTest {

    void run() {
        File file = new File("/etc/lapps/api.ini")
        ConfigObject configuration = new ConfigSlurper().parse(file.text)
        SoapClientFactory f = new SoapClientFactory();
        ServiceMonitorService s = f.create(
                ServiceMonitorService.class,
                new URL("http://vassar.lappsgrid.org/services/ServiceMonitor")
        );
        RequestAttributes attr = (RequestAttributes)s;
        attr.setUserId(configuration.vassar.username);
        attr.setPassword(configuration.vassar.password);

        //Calendar y2018 = Calendar.getInstance().set(2018, 1, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance()
        
        int[] counts = s.getAccessCounts("tester", "", now, "YEAR") as int[];
//        println "There are ${counts.size()} entries"
//        counts.each { println it }

    }

    static void main(String[] args) {
        new ServiceMonitorTest().run()
    }
}
