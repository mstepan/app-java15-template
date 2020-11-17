package com.max.app.auth;

public sealed interface IdmProvider {

    boolean login(String username, String passwordHash);

    record IdcsProvider() implements IdmProvider {
        @Override
        public boolean login(String username, String passwordHash) {
            System.out.printf("IdCS login called with %s %s%n", username, passwordHash);
            return true;
        }
    }

    record SamlProvider() implements IdmProvider {
        @Override
        public boolean login(String username, String passwordHash) {
            System.out.printf("SAML login called with %s %s%n", username, passwordHash);
            return true;
        }
    }
}
