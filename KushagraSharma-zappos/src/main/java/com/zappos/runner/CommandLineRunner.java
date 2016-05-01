package com.zappos.runner;

import com.zappos.business.ZapposClient;

public class CommandLineRunner {
    public static void main(final String[] args) throws Exception {
        final ZapposClient searchResource = new ZapposClient();
        searchResource.search("boots");
    }
}

