package io.tofpu.response.provider;

public class UserProvider extends AbstractUserProvider {
    public void sendMessage(final String message) {
        System.out.println("Message received: " + message);
    }

    @Override
    public boolean hasPermission(final String node) {
        System.out.println("Permission check: " + node);
        return true;
    }
}
