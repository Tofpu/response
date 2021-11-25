package io.tofpu.response;

import org.jetbrains.annotations.Contract;

public class Response {
    private final String identifier;
    private String response;

    @Contract(pure = true)
    public Response(final String identifier, final String response) {
        this.identifier = identifier;
        this.response = response;
    }

    /**
     * Registers the response to display when
     * associated with the identifier.
     *
     * @param response the string to display.
     */
    public void setResponse(final String response) {
        this.response = response;
    }

    /**
     * Returns the identifier of this response instance
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the response content
     *
     * @return the response content
     */
    public String getResponse() {
        return response;
    }
}
