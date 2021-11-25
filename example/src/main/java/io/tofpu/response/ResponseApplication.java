package io.tofpu.response;

import io.tofpu.response.handler.ResponseHandler;
import io.tofpu.response.manager.ResponseService;
import io.tofpu.response.provider.EventProvider;
import io.tofpu.response.provider.LoggerProvider;

import java.io.File;

public class ResponseApplication {
    public static void main(String[] args) {
        new ResponseApplication();
    }

    private final ResponseService responseService;

    public ResponseApplication() {
        this.responseService = new ResponseService(new LoggerProvider(), new File("datafolder"));

        this.responseService.load();
        registerResponse();
        receiveResponse();
        this.responseService.flush(false);

//        modifyResponse();
//        receiveResponse();
//
//        deleteResponse();
//        receiveResponse();
    }

    public void registerResponse() {
        final ResponseHandler.ResponseOperation responseOperation = ResponseHandler.ResponseOperation
                .of(ResponseHandler.ResponseOperationType.REGISTER,
                        new EventProvider("#test:our-response", "test:our-response"));

        responseService.response(responseOperation);
    }

    public void receiveResponse() {
        final ResponseHandler.ResponseOperation responseOperation = ResponseHandler.ResponseOperation
                .of(ResponseHandler.ResponseOperationType.RETRIEVE,
                        new EventProvider("?test", "test"));

        responseService.response(responseOperation);
    }

    public void modifyResponse() {
        final ResponseHandler.ResponseOperation responseOperation = ResponseHandler.ResponseOperation
                .of(ResponseHandler.ResponseOperationType.MODIFY,
                        new EventProvider("$test:our newly modified response", "test:our newly modified response"));

        responseService.response(responseOperation);
    }

    public void deleteResponse() {
        final ResponseHandler.ResponseOperation responseOperation = ResponseHandler.ResponseOperation
                .of(ResponseHandler.ResponseOperationType.DELETE,
                        new EventProvider("!test", "test"));

        responseService.response(responseOperation);
    }
}
