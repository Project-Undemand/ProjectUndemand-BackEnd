package PU.pushop.order.service;

import com.siot.IamportRestClient.IamportClient;

public class PaymentService {
    private IamportClient api;


    public PaymentService() {
        this.api = new IamportClient("REST API KEY", "REST API SECRET");
    }

}

