/**
 * OpctlItomRequestTest.java - This program tests the Oracle Function
 * that translates an OpCTL approval request document to a ServiceNow
 * incident document, authenticates connection to a ServiceNow endpoint
 * using secret stored in OCI Vault, and posts the incident in a table
 * in ServiceNow ITOM service via REST.
 *
 * AUTHOR   DATE        DETAILS
 * ramkrish 06/17/2022  Modify handler to translate incident data
 * ramkrish 06/16/2022  Creation
 * 45678901234567890123456789012345678901234567890123456789012345678901234567890
 */
package com.opctlitom.fn;

import com.fnproject.fn.testing.*;
import org.junit.*;
import static org.junit.Assert.*;

public class OpctlItomRequestTest {
/*
    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    public void shouldReturnGreeting() {
        testing.givenEvent().enqueue();
        testing.thenRun(OpctlItomRequest.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("OpctlItom Test Success!", result.getBodyAsString());
    }
 */
}
