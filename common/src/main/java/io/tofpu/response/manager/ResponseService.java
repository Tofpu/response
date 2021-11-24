package io.tofpu.response.manager;

import io.tofpu.response.handler.ResponseHandler;
import io.tofpu.response.provider.AbstractLoggerProvider;
import io.tofpu.response.repository.ResponseRepository;

import java.io.File;

public class ResponseService {
    private final ResponseRepository responseRepository;
    private final ResponseHandler responseHandler;

    public ResponseService(final AbstractLoggerProvider abstractLoggerProvider,
            final File parentFolder) {
        this.responseRepository = new ResponseRepository(abstractLoggerProvider, parentFolder);
        this.responseHandler = new ResponseHandler(responseRepository);
    }

    public void load() {
        this.responseRepository.load();
    }

    public void response(final ResponseHandler.ResponseOperation responseOperation) {
        this.responseHandler.response(responseOperation);
    }

    public void flush(final boolean asyncOperation) {
        this.responseRepository.flush(asyncOperation);
    }
}
