package org.worldbank.armm.app.utils;

import ca.dalezak.androidbase.utils.Strings;

import java.net.URI;
import java.util.Date;

public class Prefs extends ca.dalezak.androidbase.utils.Prefs {

    public class Keys {
        public static final String SERVER = "server";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String COOKIE = "cookie";
        public static final String TOKEN = "token";
    }

    public static Boolean hasServer() {
        return contains(Keys.SERVER) && !Strings.isNullOrEmpty(getString(Keys.SERVER));
    }

    public static String getServer() {
        return getString(Keys.SERVER);
    }

    public static void setServer(String server) {
        save(Keys.SERVER, server);
    }

    public static void clearCookie() {
        remove(Keys.COOKIE);
    }
    public static void setCookie(String cookie) {
        save(Keys.COOKIE, cookie);
    }

    public static String getCookie() {
        return getString(Keys.COOKIE, null);
    }

    public static Boolean hasCookie() {
        return contains(Keys.COOKIE) && !Strings.isNullOrEmpty(getString(Keys.COOKIE));
    }

    public static boolean hasSince(URI url) {
        return contains(url.toString()) && !Strings.isNullOrEmpty(getString(url.toString()));
    }

    public static void setSince(URI url, Date date) {
        save(url.toString(), date);
    }

    public static Date getSince(URI url) {
        return getDate(url.toString());
    }

    public static Boolean hasName() {
        return contains(Keys.NAME) && !Strings.isNullOrEmpty(getString(Keys.NAME));
    }

    public static String getName() {
        return getString(Keys.NAME);
    }

    public static void setName(String username) {
        save(Keys.NAME, username);
    }

    public static Boolean hasEmail() {
        return contains(Keys.EMAIL) && !Strings.isNullOrEmpty(getString(Keys.EMAIL));
    }

    public static String getEmail() {
        return getString(Keys.EMAIL);
    }

    public static void setEmail(String email) {
        save(Keys.EMAIL, email);
    }

    public static Boolean hasToken() {
        return contains(Keys.TOKEN) && !Strings.isNullOrEmpty(getString(Keys.TOKEN));
    }

    public static String getToken() {
        return getString(Keys.TOKEN);
    }

    public static void setToken(String token) {
        save(Keys.TOKEN, token);
    }
}