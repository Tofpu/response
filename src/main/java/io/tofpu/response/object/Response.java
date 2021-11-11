package io.tofpu.response.object;

public class Response {
    // The following variables are not final due to being subject to change.
    private String identifier, response;

    public Response(final String identifier, final String response) {
        this.identifier = identifier;
        this.response = response;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public void setResponse(final String response) {
        this.response = response;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getResponse() {
        return response;
    }
}
