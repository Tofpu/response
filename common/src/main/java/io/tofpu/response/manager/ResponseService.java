package io.tofpu.response.manager;

import io.tofpu.response.handler.ResponseHandler;
import io.tofpu.response.provider.AbstractLoggerProvider;
import io.tofpu.response.repository.ResponseRepository;

import java.io.File;

public class ResponseService {
    private final ResponseRepository responseRepository;
    private final ResponseHandler responseHandler;

    /**
     * Initializes your own {@link ResponseService} with
     * your given logger provider and parent folder
     *
     * @param abstractLoggerProvider your own logger provider
     * @param parentFolder your parent directory/folder
     */
    public ResponseService(final AbstractLoggerProvider abstractLoggerProvider,
            final File parentFolder) {
        this.responseRepository = new ResponseRepository(abstractLoggerProvider, parentFolder);
        this.responseHandler = new ResponseHandler(responseRepository);
    }

    /**
     * Loads the responses from the parent {@link File};
     * specified by the {@link ResponseService} constructor
     */
    public void load() {
        this.responseRepository.load();
    }

    /**
     * Automatically responds to the given {@link ResponseHandler.ResponseOperation}
     * properties. although, the type of response would depend on the @link ResponseHandler.ResponseOperationType}.
     *
     * @param responseOperation the operation to respond to.
     */
    public void response(final ResponseHandler.ResponseOperation responseOperation) {
        this.responseHandler.response(responseOperation);
    }

    /**
     * Flushes the registered {@link io.tofpu.response.Response}s to
     * their own dedicated {@link File}s, to be more specific; it would
     * update the files content if the file already exists.
     * <p></p>
     * Please be aware that if you set the async operation
     * to false (sync), it'd clear the registered {@link io.tofpu.response.Responses}
     *
     * @param asyncOperation true if you want to run the
     * flush operation async, otherwise; set it to false if want you
     * run the operation sync. although it'll empty the registered
     * responses
     */
    public void flush(final boolean asyncOperation) {
        this.responseRepository.flush(asyncOperation);
    }
}
